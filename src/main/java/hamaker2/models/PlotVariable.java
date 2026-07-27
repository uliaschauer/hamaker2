/*
 * PlotVariable.java
 *
 * Created on April 27, 2007, 5:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hamaker2.models;

import hamaker2.*;
import java.text.DecimalFormat;

/**
 * Implements a plot variable definition
 * @author uli
 */
public class PlotVariable {
    
    private final String m_id;
    private final String m_name;
    private final String m_displayUnit;
    private double m_min, m_max;
    private final double m_displayUnitFactor;
    private final String m_format;

    /**
     *
     */
    public static final double kUnknownPlotVariableID = -100000000;

    /**
     *
     */
    public static final PlotVariable kUnknownPlotVariable = null;
    
    /**
     * Creates a new instance of PlotVariable
     * @param id_prefix ID prefix of the plot variable
     * @param name_prefix Name prefix of the plot variable
     * @param id ID of the plot variable
     * @param name Name of the plot variable
     * @param unit Unit of the plot variable
     * @param min Minimum value of the plot variable
     * @param max Maximum value of the plot variable
     * @param factor Conversion factor from internal to display units
     * @param format Format string for display values
     */
    public PlotVariable(String id_prefix, String name_prefix, String id, String name, String unit, double min, double max, double factor, String format) {
        m_id = id_prefix + id;
        m_name = name_prefix + name;
        m_displayUnit = unit;
        m_min = min;
        m_max = max;
        m_displayUnitFactor = factor;
        m_format = format;
    }
    
    /**
     * Return the ID of the plot variable
     * @return ID of the plot variable
     */
    public String getID() {
        return m_id;
    }
    
    /**
     * Return the name of the plot variable
     * @return Name of the plot variable     */
    public String getName() {
        return m_name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    /**
     * Return the display unit
     * @return Display unit
     */
    public String getDisplayUnit() {
        return m_displayUnit;
    }
    
    /**
     * Return the minimum value
     * @return Minimum value
     */
    public double getMin() {
        return m_min;
    }
    
    /**
     * Return the minimum value as a display string
     * @return Minimum value
     */
    public String getMinAsStringForDisplay() {
        return new DecimalFormat(m_format).format(m_min * m_displayUnitFactor);
    }
    
    /**
     * Set the minimum value
     * @param min Minimum value
     */
    public void setMin(double min) {
        m_min = min;
    }
    
    /**
     * Set the minimum value from display text
     * @param text Minimum value
     */
    public void setMinFromText(String text) {
        m_min = Utils.StringToDouble(text) / m_displayUnitFactor;
    }
    
    /**
     * Return the maximum value
     * @return Maximum value
     */
    public double getMax() {
        return m_max;
    }
    
    /**
     * Return maximum value as display string
     * @return Maximum value
     */
    public String getMaxAsStringForDisplay() {
        return new DecimalFormat(m_format).format(m_max * m_displayUnitFactor);
    }
    
    /**
     * Set the maximum value
     * @param max Maximum value
     */
    public void setMax(double max) {
        m_max = max;
    }
    
    /**
     * Set the maximum value from display string
     * @param text Maximum value
     */
    public void setMaxFromText(String text) {
        m_max = Utils.StringToDouble(text) / m_displayUnitFactor;
    }
    
    /**
     * Return the display unit conversion factor
     * @return Display unit conversion factor
     */
    public double getDisplayUnitFactor() {
        return m_displayUnitFactor;
    }
    
    /**
     * Return the display format
     * @return Display format
     */
    public DecimalFormat getFormat() {
        return new DecimalFormat(m_format);
    }
}
