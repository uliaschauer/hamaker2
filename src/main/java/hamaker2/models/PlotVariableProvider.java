/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.models;

import java.util.ArrayList;

/**
 * Class the implements routines needed for a plot variable provider
 * @author asulrich
 */
public abstract class PlotVariableProvider {

    /**
     * Return the list of all implemented plot variables
     * @param namePrefix Prefix for human readable name
     * @param idPrefix Prefix for machine parseable ID
     * @return List of all implemented plot variables
     */
    public ArrayList<PlotVariable> plotVariables(String namePrefix, String idPrefix) {
        return new ArrayList<>();
    }

    /**
     * Return the value of a given plot variable
     * @param id ID of the plot variable
     * @return Value of the plot variable
     */
    public double getPlotVariableValue(String id) {
        return PlotVariable.kUnknownPlotVariableID;
    }

    /**
     * Set the value of a given plot variable
     * @param id ID of the plot variable
     * @param value Value of the plot variable
     */
    public void setPlotVariableValue(String id, double value) {
    }
}
