/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.particleSizeDistribution;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.swing.JPanel;

/**
 * Provides a common standard implementation for particle size distributions
 * 
 * @author asulrich
 */
public interface ParticleSizeDistribution {
    
    /**
     * Return a unique identifier for the particle size distribution
     * @return Unique identifier
     */
    public String id();
        
    /**
     * Return the name of the particle size distribution
     * @return Name of the particle size distribution
     */
    public String name();
    
    /**
     * Return a reference shown to the user, where details about the distribution can be found
     * @return 
     */
    public String reference();
    
    /**
     * Return the version code for this distribution. Used for versioned saves.
     * @return Version code
     */
    public String version();
        
    /**
     * Return a deep copy of the size distribution object
     * @return a deep copy
     */
    public ParticleSizeDistribution copy();
    
    /**
     * Returns if the size distribution has additional parameters to be saved
     * @return 
     */
    public boolean hasParameters();
    
    /**
     * Writes this particle size distribution data to file
     * @param writer The file writer object
     * @throws java.io.IOException
     */
    public void save(BufferedWriter writer) throws IOException;
    
    /**
     * Reads this particle size distribution from a file
     * @param reader The file reader object
     * @throws java.io.IOException
     */
    public void load(BufferedReader reader) throws IOException;
    
    /**
     * Return the panel displaying control elements
     * @return JPanel containing control elements
     */
    public JPanel panel();

    /**
     * Fill the control elements with the values stored in the distribution instance
     */
    public void populateValues();

    /**
     * Retrieve values from the control elements back to the instance
     */
    public void retrieveValues();
    
    /**
     * Retrieve the panels preferred size and store in an internal variable
     */
    public void savePrefSize();
    
    /**
     * Reset the panel to it's preferred size
     */
    public void setPrefSize();
    
    /**
     * Set the panel to zero size to hide it in the card layout
     */
    public void setZeroSize();
    
    
    /******************************************************************/
    /* Size distribution functions                                    */
    /******************************************************************/    
    
    /**
     * Return the number of data points returned by this distribution
     * @return 
     */
    public int numPoints();
        
    public double[] radii();
    public double[] diameters();
    public double[] numberFractions();
    public double[] volumeFractions();
    public double[] cumulativeNumberFraction();
    public double[] cumulativeVolumeFraction();
    
    public double getRv(double fraction);
    public double getDv(double fraction);
    public double getRvmean();
    public double getDvmean();
}
