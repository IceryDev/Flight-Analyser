package com.still_processing.FlightData.Graphs;

public class ScatterPlotData {
    public String axisX;
    public String axisY;
    public float[][] data;

    public ScatterPlotData(String x, String y, float[][] data){
        this.axisX = x;
        this.axisY = y;
        this.data = data;
    }
}
