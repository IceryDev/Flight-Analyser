package com.still_processing.FlightData.Utils;

import com.still_processing.Application.MapPage.MapViewFull;
import com.still_processing.Application.MapPage.PlaneMarker;
import com.still_processing.FlightData.Database;
import com.still_processing.FlightData.FlightInfo;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import org.openstreetmap.gui.jmapviewer.Coordinate;

public class LiveDataHandler {

    private static final int REFRESH_PERIOD_S = 20;
    private static Thread refreshThread;
    private static boolean refreshRunning = false;
    private static boolean batchRefresh = false;
    public static MapViewFull mvf;
    public static double[] getNewCoordinate(Coordinate c, double rot, double vel){
        GeodesicData result = Geodesic.WGS84.Direct(c.getLat(), c.getLon(), rot, vel * REFRESH_PERIOD_S);
        return new double[]{result.lat2, result.lon2, result.azi2};
    }

    public static void startRefresh(){
        if (refreshThread != null || mvf == null) { return; }
        refreshThread = new Thread(() -> {
            try {
                refreshRunning = true;
                while (refreshRunning){
                    update();
                    Thread.sleep(REFRESH_PERIOD_S * 1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted!");
            }
        });
        refreshThread.start();
    }

    public static void stopRefresh(){
        refreshRunning = false;
        refreshThread.interrupt();
        refreshThread = null;
    }

    private static void update() {
        mvf.removeAllMapMarkers();
        for (FlightInfo i : Database.flights){
            double[] translation = getNewCoordinate(
                    new Coordinate(i.plane.latitude, i.plane.longitude),
                    i.plane.heading, i.plane.velocity);
            i.plane.latitude = translation[0];
            i.plane.longitude = translation[1];
            i.plane.heading = translation[2];
            mvf.addMapMarker(new PlaneMarker(new Coordinate(i.plane.latitude, i.plane.longitude), i.plane.heading, mvf));
        }
    }

    private static void appendNewMarkers(){
        mvf.removeAllMapMarkers();
        for (FlightInfo i : Database.flights){
            mvf.addMapMarker(new PlaneMarker(new Coordinate(i.plane.latitude, i.plane.longitude), i.plane.heading, mvf));
        }
    }

    public static void setBatchRefresh() {
        batchRefresh = true;
        appendNewMarkers();
        batchRefresh = false;
    }
}
