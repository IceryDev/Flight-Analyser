package com.still_processing.FlightData.Graphs;

public class ScatterPlotData {
    String axisX;
    String axisY;
    float[][] data;

    public ScatterPlotData(String x, String y, float[][] data){
        this.axisX = x;
        this.axisY = y;
        this.data = data;
    }
}
