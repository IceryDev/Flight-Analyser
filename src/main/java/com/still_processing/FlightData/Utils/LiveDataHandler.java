package com.still_processing.FlightData.Utils;

import com.still_processing.Application.MapPage.MapViewFull;
import com.still_processing.Application.MapPage.PlaneMarker;
import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Database;
import com.still_processing.FlightData.FlightFetcher;
import com.still_processing.FlightData.FlightInfo;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Refreshes live data every {@link LiveDataHandler#REFRESH_PERIOD_S} seconds.
 *
 * @author Ulaş İçer
 */
public class LiveDataHandler {

    private static final int REFRESH_PERIOD_S = 10;
    private static final int REQUEST_AFTER_ITERATION = 10000;
    private static final SwingWorker<Void, Void> requestWorker = new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
            FlightFetcher.fetchLiveFlightInfo(100);
            return null;
        }
    };
    private static int iterationNo = 0;
    private static Thread refreshThread;
    private static boolean refreshRunning = false;
    private static AtomicBoolean queueRunning = new AtomicBoolean(false);

    private static LinkedBlockingQueue<Runnable> eventQueue = new LinkedBlockingQueue<>();

    public static MapViewFull mvf;

    /**
     * Returns the next location projection based on current plane velocity, position, and rotation.
     * @param c Current coordinates of the plane.
     * @param rot Rotation, relative to North, in degrees.
     * @param vel Velocity in m/s
     * @return Next latitude, longitude, and rotation values as an array of doubles, respectively.
     */
    public static double[] getNewCoordinate(Coordinate c, double rot, double vel){
        GeodesicData result = Geodesic.WGS84.Direct(c.getLat(), c.getLon(), rot, vel * REFRESH_PERIOD_S);
        return new double[]{result.lat2, result.lon2, result.azi2};
    }

    /**
     * Starts the routine that adds refresh events to the event queue.
     *
     * @author Ulaş İçer
     */
    public static void startRefresh(){
        if (refreshThread != null || mvf == null) { return; }
        refreshThread = new Thread(() -> {
            try {
                refreshRunning = true;
                while (refreshRunning){
                    if (Database.flights.isEmpty() || iterationNo >= REQUEST_AFTER_ITERATION){
                        requestWorker.execute();
                        iterationNo = 0;
                    }
                    eventQueue.add(LiveDataHandler::update);
                    runQueue();
                    Thread.sleep(REFRESH_PERIOD_S * 1000);
                    iterationNo++;
                }
            } catch (InterruptedException e) {
                System.out.println("Refresh Interrupted!");
            }
        });
        refreshThread.start();
    }

    /**
     * Halts the refresh events from queueing up.
     *
     * @author Ulaş İçer
     */
    public static void stopRefresh(){
        if (refreshThread == null) { return; }
        refreshRunning = false;
        refreshThread.interrupt();
        refreshThread = null;
    }

    /**
     * Updates the current flight parameters with the projected values
     * from the function {@link LiveDataHandler#getNewCoordinate(Coordinate, double, double)}
     *
     * @author Ulaş İçer
     */
    private static void update() {
        List<FlightInfo> snapshot = new ArrayList<>(Database.flights);
        SwingUtilities.invokeLater(() -> {
            mvf.removeAllMapMarkers();
            for (FlightInfo i : snapshot){
                double[] translation = getNewCoordinate(
                        new Coordinate(i.plane.latitude, i.plane.longitude),
                        i.plane.heading, i.plane.velocity);
                i.plane.latitude = translation[0];
                i.plane.longitude = translation[1];
                i.plane.heading = translation[2];
                mvf.addMapMarker(new PlaneMarker(new Coordinate(i.plane.latitude, i.plane.longitude), i.plane.heading, mvf, Settings.PLANE_RED));
            }
        });
    }

    /**
     * Adds the {@link Runnable} r to the event queue.
     * @param r The runnable to be passed.
     *
     * @author Ulaş İçer
     */
    public static void addToQueue(Runnable r) {
        eventQueue.add(r);
        runQueue();
    }

    /**
     * If the queue is not already running, runs until all the events in the queue thread is consumed.
     *
     * @author Ulaş İçer
     */
    private static void runQueue(){
        if (!queueRunning.compareAndSet(false, true)) { return; }
        while (!eventQueue.isEmpty()){
            try {
                //System.out.println("Running : " + eventQueue.peek().toString());
                eventQueue.take().run();
            } catch (InterruptedException e) {
                System.err.println("Error: Failed to Run Element in Queue. [LiveDataHandler.java]");
            }
        }
        queueRunning.set(false);
    }
}
