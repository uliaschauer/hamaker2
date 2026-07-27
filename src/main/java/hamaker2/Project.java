/*
 * HamakerData.java
 *
 * Created on March 24, 2007, 2:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2;

import hamaker2.io.HamakerExcelExporter;
import hamaker2.io.HamakerFileWriter;
import hamaker2.io.HamakerFileReader;
import hamaker2.models.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * This class holds all the data structures required in Hamaker.
 *
 * @author uli
 */
public final class Project {

    private boolean m_unsavedChanges;
    private String m_lastSavePath;
    private String m_title;
    private final ArrayList<Serie> m_series;
    private Serie m_selectedSerie;
    private PlotType m_plotType;
    private PlotQuantity m_plotQuantity;
    private final ArrayList<PlotVariable> m_plotVariables;
    private String m_selectedPlotVariable1, m_selectedPlotVariable2;
    private final double kPlotPoints = 500.0;
    private ArrayList<ArrayList<Vector3D>> m_plotPoints2D;
    private ArrayList<ArrayList<ArrayList<Vector3D>>> m_plotPoints3D;
    private double m_primaryAxisMin, m_primaryAxisMax;
    private double m_secondaryAxisMin, m_secondaryAxisMax;
    private double m_interactionAxisMin = -40.0, m_interactionAxisMax = 40.0;
    private final Executor m_executor = Executors.newSingleThreadExecutor();

    /**
     * Creates a new instance of Project object
     */
    public Project() {
        this("");
    }

    /**
     * Creates a new instance of Project object and populates it with data read
     * from a file
     *
     * @param file The path of the file to be read
     */
    public Project(String file) {
        m_unsavedChanges = false;
        m_lastSavePath = "";

        m_title = "Untitled";

        m_series = new ArrayList<>();
        m_series.add(new Serie());
        m_selectedSerie = m_series.get(0);

        m_plotType = PlotType.Plot2D;
        m_plotQuantity = PlotQuantity.Potential;

        m_plotVariables = new ArrayList<>();
        updatePlotVariables();
        m_selectedPlotVariable1 = "serie_distance";
        m_selectedPlotVariable2 = "serie_body1_psd_monodisperse_diameter";

        m_plotPoints2D = new ArrayList<>();
        m_plotPoints3D = new ArrayList<>();

        //recalculatePlot(null);

        if (!file.equals("")) {
            Project.this.load(new File(file));
        }
    }

    /**
     * Return the complete list of plot variables in the open project
     *
     * @return
     */
    public ArrayList<PlotVariable> getPlotVariables() {
        return m_plotVariables;
    }

    /**
     * Update the list of plot variables in all series, avoiding duplicate
     * entries.
     */
    public void updatePlotVariables() {

        m_plotVariables.clear();

        Iterator<Serie> i = m_series.iterator();
        while (i.hasNext()) {
            Serie s = i.next();
            ArrayList<PlotVariable> seriesVariables = s.plotVariables("", "serie_");

            Iterator<PlotVariable> t_serie = seriesVariables.iterator();
            while (t_serie.hasNext()) {
                PlotVariable v = t_serie.next();

                //check if id does not yet exist in variables
                Iterator<PlotVariable> t_global = m_plotVariables.iterator();
                boolean found = false;
                while (t_global.hasNext()) {
                    if (v.getID().equals(t_global.next().getID())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    m_plotVariables.add(v);
                }
            }
        }
    }

    /**
     * Get the plot variable at index i (required for legacy reader)
     *
     * @deprecated
     * @param i Index of the plot variable
     * @return Plot variable at index i
     */
    @Deprecated
    public PlotVariable getPlotVariable(int i) {
        return getPlotVariables().get(i);
    }

    /**
     * Return plot variable with given ID
     *
     * @param id The ID of the plot variable
     * @return the plot variable
     */
    private PlotVariable plotVariableWithID(String id) {
        Iterator<PlotVariable> i = getPlotVariables().iterator();
        while (i.hasNext()) {
            PlotVariable p = i.next();
            if (p.getID().equals(id)) {
                return p;
            }
        }

        System.out.println("Failed to find plot variable with ID: " + id);
        return null;
    }

    /**
     * Return the first selected plot variable
     *
     * @return The selected plot variable
     */
    public PlotVariable getSelectedVariable1() {
        return plotVariableWithID(m_selectedPlotVariable1);
    }

    /**
     * Return the name of the first selected plot variable
     *
     * @return Name of the selected plot variable
     */
    public String getSelectedVariable1Name() {
        return getSelectedVariable1().getName();
    }

    /**
     * Return the unit of the first selected plot variable
     *
     * @return Unit of the selected plot variable
     */
    public String getSelectedVariable1Unit() {
        return getSelectedVariable1().getDisplayUnit();
    }

    /**
     * Set the first selected plot variable to the one given ID
     *
     * @param plotVariableID ID of the plot variable
     */
    public void setSelectedVariable1(String plotVariableID) {
        
        boolean found = false;
        
        for (PlotVariable m_plotVariable : m_plotVariables) {
            //System.out.println("Variable " + i + ": " + m_plotVariables.get(i).getID());
            if (plotVariableID.equals(m_plotVariable.getID())) {
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("Unknown plot variable ID: " + plotVariableID);
        }
        
        m_selectedPlotVariable1 = plotVariableID;
        m_unsavedChanges = true;
    }

    /**
     * Set first selected plot variable by index
     *
     * @deprecated
     * @param index index of plot variable to be selected
     */
    @Deprecated
    public void setSelectedVariable1Index(int index) {
        m_selectedPlotVariable1 = getPlotVariable(index).getID();
        m_unsavedChanges = true;
    }

    /**
     * Return the second selected plot variable
     *
     * @return The selected plot variable
     */
    public PlotVariable getSelectedVariable2() {
        return plotVariableWithID(m_selectedPlotVariable2);
    }

    /**
     * Return the name of the second selected plot variable
     *
     * @return Name of the selected plot variable
     */
    public String getSelectedVariable2Name() {
        return getSelectedVariable2().getName();
    }

    /**
     * Return the unit of the second selected plot variable
     *
     * @return Unit of the selected plot variable
     */
    public String getSelectedVariable2Unit() {
        return getSelectedVariable2().getDisplayUnit();
    }

    /**
     * Set the second selected plot variable to the one given ID
     *
     * @param plotVariableID ID of the plot variable
     */
    public void setSelectedVariable2(String plotVariableID) {
        m_selectedPlotVariable2 = plotVariableID;
        m_unsavedChanges = true;
    }

    /**
     * Set second selected plot variable by index
     *
     * @deprecated
     * @param index index of plot variable to be selected
     */
    @Deprecated
    public void setSelectedVariable2Index(int index) {
        m_selectedPlotVariable2 = getPlotVariable(index).getID();
        m_unsavedChanges = true;
    }

    /**
     * Return the title of the current project
     *
     * @return title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * Set the project title (test for empty string)
     *
     * @param title Title string
     */
    public void setTitle(String title) {
        if (!title.equals("")) {
            m_title = title;
        }
        m_unsavedChanges = true;
    }

    /**
     * Return the array list of series
     *
     * @return array list of all series
     */
    public ArrayList<Serie> getSeries() {
        return m_series;
    }

    /**
     * Return the number of series in the project
     *
     * @return number of series
     */
    public int getSeriesCount() {
        return m_series.size();
    }

    /**
     * Return the series at a given index
     *
     * @param i index of the series
     * @return series at index i
     */
    public Serie getSerie(int i) {
        return (Serie) m_series.get(i);
    }

    /**
     * Return the currently selected series
     *
     * @return currently selected series
     */
    public Serie getSelectedSerie() {
        return m_selectedSerie;
    }

    /**
     * Return the index of the currently selected series
     *
     * @return
     */
    public int getSelectedSerieIndex() {
        return m_series.indexOf(m_selectedSerie);
    }

    /**
     * Selected the given series
     *
     * @param serie Series to be selected
     */
    public void setSelectedSerie(Serie serie) {
        m_selectedSerie = serie;
    }

    /**
     * Select the series at the given index
     *
     * @param index index to be selected
     */
    public void setSelectedSerieIndex(int index) {
        if (index < 0) {
            index = 0;
        }
        m_selectedSerie = m_series.get(index);
    }

    /**
     * Add a new series to the project
     */
    public void addSerie() {

        //Create a unique series name. The default is "Untitled Serie" and is appended with incremental numbers if series with the same name already exist
        String name = "Untitled Serie";
        boolean ok = false;
        int count = 1;
        while (!ok) {
            ok = true;
            for (int i = 0; i < m_series.size(); i++) {
                if (getSerie(i).getName().equals(name)) {
                    name = "Untitled Serie" + String.valueOf(++count);
                    ok = false;
                }
            }
        }

        Serie s = new Serie();
        m_series.add(s);
        m_selectedSerie = s;
        getSelectedSerie().setName(name);

        m_unsavedChanges = true;
    }

    /**
     * Add a given series to the project
     *
     * @param s Series to be added
     */
    public void addSerie(Serie s) {
        m_series.add(s);
    }

    /**
     * Remove given series from the project
     *
     * @param serie Series to be removed
     */
    public void deleteSerie(Serie serie) {
        //select the previous serie or the first one
        m_selectedSerie = m_series.get(Math.max(0, m_series.indexOf(serie) - 1));
        m_series.remove(serie);

        m_unsavedChanges = true;
    }

    /**
     * Remove all series from the project
     */
    public void deleteAllSeries() {
        m_series.clear();
    }

    /**
     * Duplicate the series at index
     *
     * @param index Index of series to be duplicated
     */
    public void duplicateSerie(int index) {
        Serie serie = getSerie(index);
        String name = "Copy of " + serie.getName();

        Serie s = Serie.newInstance(serie);
        m_series.add(s);
        m_selectedSerie = s;
        getSelectedSerie().setName(name);

        m_unsavedChanges = true;
    }

    /**
     * Plot type enum structure
     */
    public enum PlotType {

        /**
         * The 2D plot type
         */
        Plot2D("2D"),
        /**
         * The 3D plot type
         */
        Plot3D("3D");

        private final String m_label;

        PlotType(String label) {
            m_label = label;
        }

        /**
         * Get the GUI label of the selected plot type
         *
         * @return
         */
        @Override
        public String toString() {
            return m_label;
        }
    }

    /**
     * Return the currently selected plot type
     *
     * @return currently selected plot type
     */
    public PlotType getPlotType() {
        return m_plotType;
    }

    /**
     * Set the given plot type
     *
     * @param type Type to be set
     */
    public void setPlotType(PlotType type) {
        m_plotType = type;
        m_unsavedChanges = true;
    }

    /**
     * Plot quantity enum type
     */
    public enum PlotQuantity {

        /**
         * Plot the interaction potential
         */
        Potential("Potential"),
        /**
         * Plot the interaction force, repulsive being negative
         */
        Force_neg("Force (rep. -)"),
        /**
         * Plot the interaction force, repulsive being positive
         */
        Force_pos("Force (rep. +)");

        private final String m_label;

        PlotQuantity(String label) {
            m_label = label;
        }

        /**
         * Return the GUI label of the plot type
         *
         * @return
         */
        @Override
        public String toString() {
            return m_label;
        }
    }

    /**
     * Get the quantity to be plotted
     *
     * @return Selected plot quantity
     */
    public PlotQuantity getPlotQuantity() {
        return m_plotQuantity;
    }

    /**
     * Set the selected plot quantity
     *
     * @param quantity Quantity to be plotted
     */
    public void setPlotQuantity(PlotQuantity quantity) {
        m_plotQuantity = quantity;
        m_unsavedChanges = true;
    }

    /**
     * Does the project need to be saved
     *
     * @return Does the project need saving
     */
    public boolean getNeedsSave() {
        if (m_unsavedChanges) {
            return true;
        }
        for (int i = 0; i < getSeriesCount(); i++) {
            if (getSerie(i).getNeedsSave()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Save the project
     *
     * @param noDialog Don't show a file path dialog
     * @return true when successfully saved
     */
    public boolean save(boolean noDialog) {
        String path = m_lastSavePath;

        if (!noDialog || path.equals("")) {
            //show the save dialog

            JFileChooser fc = new JFileChooser(new UserPreferences().getDefaultPath());
            fc.setFileFilter(new Ham2FileFilter());

            int returnVal = fc.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = fc.getSelectedFile().getPath();

                //make sure the path has the proper stuffix
                if (!path.endsWith(".ham2")) {
                    path = path + ".ham2";
                }
            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                return false;
            }
        }

        //create the file descriptor
        File f = new File(path);

        new UserPreferences().setDefaultPath(f.getParent());

        //if the file exists remove it
        if (f.exists()) {
            f.delete();
        }

        HamakerFileWriter.writeFile(this, f);

        m_unsavedChanges = false;
        m_lastSavePath = path;

        return true;
    }

    /**
     * Load project from disk, showing a file selection
     */
    public void load() {
        JFileChooser fc = new JFileChooser(new UserPreferences().getDefaultPath());
        fc.setFileFilter(new Ham2FileFilter());

        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            load(fc.getSelectedFile());

            new UserPreferences().setDefaultPath(fc.getSelectedFile().getParent());
        }
    }

    /**
     * Load project from specified file
     *
     * @param file File object to be read
     */
    private void load(File file) {

        if (!HamakerFileReader.readFile(this, file)) {
            JOptionPane.showMessageDialog(null, "An error occured reading the file.", "Open Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        m_unsavedChanges = false;
        m_lastSavePath = file.getPath();
    }

    /**
     * Get the number of plot points along the primary axis of a 3D plot
     *
     * @deprecated
     * @param serie The series for which plot points are to be determined
     * @return number of primary points
     */
    @Deprecated
    public int getPrimary3DPlotPointCount(int serie) {
        return m_plotPoints3D.get(serie).size();
    }

    /**
     * Get the number of plot points along the secondary axis of a 3D plot
     *
     * @deprecated
     * @param serie The series for which plot points are to be determined
     * @return number of secondary points
     */
    @Deprecated
    public int getSecondary3DPlotPointCount(int serie) {
        return m_plotPoints3D.get(serie).get(0).size();
    }

    /**
     * Get the 3D point of a given series, at given x and y indices
     *
     * @deprecated
     * @param serie The series from which the point is extracted
     * @param x The x index
     * @param y The y index
     * @return The 3D point
     */
    @Deprecated
    public Vector3D get3DPlotPoint(int serie, int x, int y) {
        return m_plotPoints3D.get(serie).get(x).get(y);
    }

    /**
     * Export the project in an Excel readable CSV, showing a file selection
     * dialog
     */
    public void exportExcel() {

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ExcelFileFilter());

        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getPath();

            //make sure the path has the proper stuffix
            if (!path.endsWith(".xls")) {
                path = path + ".xls";
            }

            //create the file descriptor
            File f = new File(path);

            //if the file exists remove it
            if (f.exists()) {
                f.delete();
            }

            HamakerExcelExporter.exportData(this, f);
        }
    }

    /**
     * Recalculate the plot
     *
     * @param listener Listener to update a GUI progress bar
     */
    public final void recalculatePlot(final ComputeListener listener) {

        if (m_plotType == PlotType.Plot2D) {
            m_executor.execute(() -> {
                recalculatePlot2D(listener);
            });

        } else if (m_plotType == PlotType.Plot3D) {
            m_executor.execute(() -> {
                recalculatePlot3D(listener);
            });
        }

    }

    private void recalculatePlot2D(ComputeListener listener) {

        //m_plotPoints.clear();
        ArrayList<ArrayList<Vector3D>> tempPoints = new ArrayList<>();

        double min1 = getSelectedVariable1().getMin();
        double max1 = getSelectedVariable1().getMax();
        m_primaryAxisMin = min1;
        m_primaryAxisMax = max1;

        double total_points = 100.0 / (getSeriesCount() * kPlotPoints);
        int point = 0;

        for (int s = 0; s < getSeriesCount(); s++) {

            double save1 = getSerie(s).getPlotVariableValue(getSelectedVariable1().getID());

            ArrayList<Vector3D> serie = new ArrayList<>();

            for (double v1 = min1; v1 <= max1; v1 += (max1 - min1) / kPlotPoints) {

                //System.out.println("v1:" + String.valueOf(v1));
                getSerie(s).setPlotVariableValue(getSelectedVariable1().getID(), v1);

                double y = 0;
                if (m_plotQuantity.compareTo(PlotQuantity.Potential) == 0) {
                    y = getSerie(s).getInteractionPotential();
                } else if (m_plotQuantity == PlotQuantity.Force_neg) {
                    y = getSerie(s).getInteractionForce();
                } else if (m_plotQuantity == PlotQuantity.Force_pos) {
                    y = -getSerie(s).getInteractionForce();
                } else {
                    System.out.println("Strange error with plot quantity!");
                }
                
                //System.out.println("Data:" + v1 + ", " + y);

                serie.add(new Vector3D(v1, y));

                if (listener != null) {
                    listener.notifyProgressUpdate((int) (point++ * total_points));
                }
            }

            getSerie(s).setPlotVariableValue(getSelectedVariable1().getID(), save1);

            tempPoints.add(serie);

            //do extrema analysis
            getSerie(s).setMinimum("n/A", "n/A");
            getSerie(s).setMaximum("n/A", "n/A");

            int min = -1;
            int max = -1;
            for (int i = (int) (kPlotPoints - 2); i > 0; i--) {

                double pm = serie.get(i - 1).getY();
                double pc = serie.get(i).getY();
                double pc_x = serie.get(i).getX();
                double pp = serie.get(i + 1).getY();

                //check for normal extrema
                if (pm > pc && pp > pc) {
                    getSerie(s).setMinimum(new DecimalFormat("0.00").format(pc_x * getSelectedVariable1().getDisplayUnitFactor()), new DecimalFormat("0.00").format(pc));
                    min = i;
                }

                if (pm < pc && pp < pc) {
                    getSerie(s).setMaximum(new DecimalFormat("0.00").format(pc_x * getSelectedVariable1().getDisplayUnitFactor()), new DecimalFormat("0.00").format(pc));
                    max = i;
                }

                //if none found up to end, check for zero (NaN) extrema
                if (i == 1) {
                    if (min == -1) {
                        //check for extreme (NaN) minimum
                        if (pm < -10000 || (pm != pm && pc < 0)) {
                            getSerie(s).setMinimum(new DecimalFormat("0.00").format(serie.get(0).getX() * getSelectedVariable1().getDisplayUnitFactor()), "-\u221E");
                            min = 0;
                        }
                    }

                    if (max == -1) {
                        //check for extreme (NaN) maximum
                        if (pm > -10000 || (pm != pm && pc > 0)) {
                            getSerie(s).setMaximum(new DecimalFormat("0.00").format(serie.get(0).getX() * getSelectedVariable1().getDisplayUnitFactor()), "\u221E");
                            max = 0;
                        }
                    }
                }
            }
        }

        m_plotPoints2D = tempPoints;

        if (listener != null) {
            listener.notifyComplete();
        }

    }

    private void recalculatePlot3D(ComputeListener listener) {

        double y_min = 0.0;
        double y_max = 0.0;

        //m_plotPoints3D.clear();
        ArrayList<ArrayList<ArrayList<Vector3D>>> tempPoints = new ArrayList<>();

        double total_points = 100.0 / (getSeriesCount() * kPlotPoints * kPlotPoints);
        int point = 0;

        double min1 = getSelectedVariable1().getMin();
        double max1 = getSelectedVariable1().getMax();
        double min2 = getSelectedVariable2().getMin();
        double max2 = getSelectedVariable2().getMax();
        m_primaryAxisMin = min1;
        m_primaryAxisMax = max1;
        m_secondaryAxisMin = min2;
        m_secondaryAxisMax = max2;

        for (int s = 0; s < getSeriesCount(); s++) {

            double save1 = getSerie(s).getPlotVariableValue(getSelectedVariable1().getID());
            double save2 = getSerie(s).getPlotVariableValue(getSelectedVariable2().getID());

            ArrayList<ArrayList<Vector3D>> serie = new ArrayList<>();

            for (double v1 = min1; v1 <= max1; v1 += (max1 - min1) / kPlotPoints) {

                getSerie(s).setPlotVariableValue(getSelectedVariable1().getID(), v1);

                ArrayList<Vector3D> line = new ArrayList<>();

                for (double v2 = min2; v2 <= max2; v2 += (max2 - min2) / kPlotPoints) {

                    getSerie(s).setPlotVariableValue(getSelectedVariable2().getID(), v2);

                    double y = 0;
                    if (m_plotQuantity == PlotQuantity.Potential) {
                        y = getSerie(s).getInteractionPotential();
                    } else if (m_plotQuantity == PlotQuantity.Force_neg) {
                        y = getSerie(s).getInteractionForce();
                    } else if (m_plotQuantity == PlotQuantity.Force_pos) {
                        y = -getSerie(s).getInteractionForce();
                    } else {
                        System.out.print("Strange error with plot quantity!");
                    }

                    y_min = Math.min(y_min, y);
                    y_max = Math.max(y_max, y);

                    line.add(new Vector3D(v1, v2, y));

                    if (listener != null) {
                        listener.notifyProgressUpdate((int) (point++ * total_points));
                    }
                }

                serie.add(line);
            }

            getSerie(s).setPlotVariableValue(getSelectedVariable1().getID(), save1);
            getSerie(s).setPlotVariableValue(getSelectedVariable2().getID(), save2);

            tempPoints.add(serie);

            //in 3D just leave the extrema at n/A
            getSerie(s).setMinimum("n/A", "n/A");
            getSerie(s).setMaximum("n/A", "n/A");
        }

        m_plotPoints3D = tempPoints;

        if (listener != null) {
            listener.notifyComplete();
        }
    }

    /**
     * Return the plot point 2D array
     *
     * @return Array list of all plot points
     */
    public ArrayList<ArrayList<Vector3D>> getPlotPoints2D() {
        return m_plotPoints2D;
    }

    /**
     * Return the plot point 3D array
     *
     * @return Array list of all plot points
     */
    public ArrayList<ArrayList<ArrayList<Vector3D>>> getPlotPoints3D() {
        return m_plotPoints3D;
    }

    /**
     * Return the minimum value of the primary axis
     *
     * @return minimum value of the primary axis
     */
    public double getPrimaryAxisMin() {
        return m_primaryAxisMin;
    }

    /**
     * Return the maximum value of the primary axis
     *
     * @return maximum value of the primary axis
     */
    public double getPrimaryAxisMax() {
        return m_primaryAxisMax;
    }

    /**
     * Return the extent of the primary axis
     *
     * @return extent value of the primary axis
     */
    public double getPrimaryAxisLength() {
        return m_primaryAxisMax - m_primaryAxisMin;
    }

    /**
     * Return the minimum value of the secondary axis
     *
     * @return minimum value of the secondary axis
     */
    public double getSecondaryAxisMin() {
        return m_secondaryAxisMin;
    }

    /**
     * Return the maximum value of the secondary axis
     *
     * @return maximum value of the secondary axis
     */
    public double getSecondaryAxisMax() {
        return m_secondaryAxisMax;
    }

    /**
     * Return the extent of the secondary axis
     *
     * @return extent value of the secondary axis
     */
    public double getSecondaryAxisLength() {
        return m_secondaryAxisMax - m_secondaryAxisMin;
    }

    /**
     * Return the minimum value of the interaction (y) axis
     *
     * @return minimum value of the interaction (y) axis
     */
    public double getInteractionAxisMin() {
        return m_interactionAxisMin;
    }

    /**
     * Return the maximum value of the interaction (y) axis
     *
     * @return maximum value of the interaction (y) axis
     */
    public double getInteractionAxisMax() {
        return m_interactionAxisMax;
    }

    /**
     * Return the extent of the interaction (y) axis
     *
     * @return extent value of the interaction (y) axis
     */
    public double getInteractionAxisLength() {
        return m_interactionAxisMax - m_interactionAxisMin;
    }

    /**
     * Set the minimum value of the interaction (y) axis
     *
     * @param min Minimum value
     */
    public void setInteractionAxisMin(double min) {
        m_interactionAxisMin = min;
    }

    /**
     * Set the maximum value of the interaction (y) axis
     *
     * @param max Minimum value
     */
    public void setInteractionAxisMax(double max) {
        m_interactionAxisMax = max;
    }

    /**
     *
     * @return
     */
    /*public String getMinimumPosition() {
     return getSelectedSerie().getMinimumPosition();
     }*/
    /**
     *
     * @return
     */
    /*public String getMinimumPotential() {
     return getSelectedSerie().getMinimumPotential();
     }*/
    /**
     *
     * @return
     */
    /*public String getMaximumPosition() {
     return getSelectedSerie().getMaximumPosition();
     }*/
    /**
     *
     * @return
     */
    /*public String getMaximumPotential() {
     return getSelectedSerie().getMaximumPotential();
     }*/
    private class Ham2FileFilter extends FileFilter {

        public Ham2FileFilter() {
        }

        @Override
        public String getDescription() {
            return "Hamaker Files (.ham2)";
        }

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".ham2");
        }
    }

    private class ExcelFileFilter extends FileFilter {

        public ExcelFileFilter() {
        }

        @Override
        public String getDescription() {
            return "Excel Files (.xls)";
        }

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".xls");
        }
    }
}
