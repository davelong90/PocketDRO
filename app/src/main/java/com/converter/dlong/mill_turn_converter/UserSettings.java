package com.converter.dlong.mill_turn_converter;

import java.io.Serializable;

/**
 * This is a wrapper object class that will save all the
 * user input data to be serialized for file I/O
 */
public class UserSettings implements Serializable {

    private double xRev, yRev, edgeFinderDiam, xDist, yDist = 0;
    private boolean revIsMetric, edgeIsMetric, distIsMetric = false;

    public UserSettings()
    {

    }

    public UserSettings(double xRev, double yRev, double edgeFinderDiam, double xDist, double yDist,
                        boolean revIsMetric, boolean edgeIsMetric, boolean distIsMetric)
    {
        this.xRev = xRev;
        this.yRev = yRev;
        this.edgeFinderDiam = edgeFinderDiam;
        this.xDist = xDist;
        this.yDist = yDist;
        this.revIsMetric = revIsMetric;
        this.edgeIsMetric = edgeIsMetric;
        this.distIsMetric = distIsMetric;
    }

    public double getxRev() {
        return xRev;
    }

    public void setxRev(double xRev) {
        this.xRev = xRev;
    }

    public double getyRev() {
        return yRev;
    }

    public void setyRev(double yRev) {
        this.yRev = yRev;
    }

    public double getEdgeFinderDiam() {
        return edgeFinderDiam;
    }

    public void setEdgeFinderDiam(double edgeFinderDiam) {
        this.edgeFinderDiam = edgeFinderDiam;
    }

    public double getxDist() {
        return xDist;
    }

    public void setxDist(double xDist) {
        this.xDist = xDist;
    }

    public double getyDist() {
        return yDist;
    }

    public void setyDist(double yDist) {
        this.yDist = yDist;
    }

    public boolean isRevIsMetric() {
        return revIsMetric;
    }

    public void setRevIsMetric(boolean revIsMetric) {
        this.revIsMetric = revIsMetric;
    }

    public boolean isEdgeIsMetric() {
        return edgeIsMetric;
    }

    public void setEdgeIsMetric(boolean edgeIsMetric) {
        this.edgeIsMetric = edgeIsMetric;
    }

    public boolean isDistIsMetric() {
        return distIsMetric;
    }

    public void setDistIsMetric(boolean distIsMetric) {
        this.distIsMetric = distIsMetric;
    }
}
