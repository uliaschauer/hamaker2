/*
 * Serie.java
 *
 * Created on March 30, 2007, 7:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2;

import hamaker2.models.*;
import hamaker2.models.dispersion.*;
import hamaker2.models.electrostatic.*;
import hamaker2.models.misc.*;
import hamaker2.models.steric.*;
import hamaker2.particleSizeDistribution.ParticleSizeDistribution;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JOptionPane;

/**
 * This class implements all data handling related to a single data series.
 *
 * @author uli
 */
public final class Serie extends PlotVariableProvider {

    private boolean m_unsavedChanges;
    private String m_name;
    private Color m_color, m_gridColor;
    private boolean m_visible;
    private final ArrayList<DispersionInteractionModel> m_dispersionInteractionModels;
    private DispersionInteractionModel m_selectedDispersionModel;
    private final ArrayList<ElectrostaticInteractionModel> m_electrostaticInteractionModels;
    private ElectrostaticInteractionModel m_selectedElectrostaticModel;
    private final ArrayList<StericInteractionModel> m_stericInteractionModels;
    private StericInteractionModel m_selectedStericModel;
    private ArrayList<MiscInteractionModel> m_miscInteractionModels;
    private double m_distance;
    private double m_temperature;
    private boolean m_heterogeneous;
    private Particle m_particle1, m_particle2;
    private Medium m_medium;

    /**
     * The Boltzmann constant
     */
    final public double kB = 1.3806503E-23;
    private Stability m_stability;
    private boolean m_showStability;
    private GeometryClass m_geometryClass;
    private final String m_minimum[];
    private final String m_maximum[];
    private Yodel m_yodel;

    /**
     * Creates a new series with default parameters
     */
    public Serie() {
        m_unsavedChanges = false;

        m_name = "Untitled Serie";
        m_color = new Color(0.0f, 1.0f, 0.0f, 1.0f);
        m_gridColor = new Color(0.6f, 0.6f, 0.6f, 1.0f);
        m_visible = true;

        //load the dispersion models
        m_dispersionInteractionModels = dispersionModels();
        m_selectedDispersionModel = getDispersionModelWithID("dispersion_nonretarded");

        //load the electrostatic models
        m_electrostaticInteractionModels = electrostaticModels();
        m_selectedElectrostaticModel = getElectrostaticModelWithID("electrostatic_HFF");

        //load the steric models
        m_stericInteractionModels = stericModels();
        m_selectedStericModel = getStericModelWithID("steric_none");

        //load the misc models
        m_miscInteractionModels = miscModels();

        //make sure no two models have the same ID
        checkModelIDs();

        m_distance = 4E-9;
        m_temperature = 300.0;
        m_heterogeneous = false;

        m_particle1 = new Particle();
        m_particle2 = new Particle();
        m_medium = new Medium();

        m_stability = new Stability();
        m_showStability = false;

        m_geometryClass = GeometryClass.Sphere_Sphere;

        m_minimum = new String[2];
        m_maximum = new String[2];

        m_yodel = new Yodel();
    }

    /**
     * Creates a duplicate (deep copy) of the given series
     *
     * @param serie The series to be deep copied
     * @return The deep copy of the input series
     */
    public static Serie newInstance(Serie serie) {
        Serie copy = new Serie();
        copy.setName(serie.getName());
        copy.setColor(serie.getColor());
        copy.setVisible(serie.getVisible());
        copy.m_dispersionInteractionModels.clear();
        for (int i = 0; i < serie.getDispersionModelCount(); i++) {
            DispersionInteractionModel dupl = (DispersionInteractionModel) serie.getDispersionModel(i).duplicate();
            copy.m_dispersionInteractionModels.add(dupl);
            if (dupl.id().equals(serie.m_selectedDispersionModel.id())) {
                copy.setSelectedDispersionModel(dupl);
            }
        }
        //copy.setSelectedDispersionModel(serie.m_selectedDispersionModel);

        copy.m_electrostaticInteractionModels.clear();
        for (int i = 0; i < serie.getElectrostaticModelCount(); i++) {
            ElectrostaticInteractionModel dupl = (ElectrostaticInteractionModel) serie.getElectrostaticModel(i).duplicate();
            copy.m_electrostaticInteractionModels.add(dupl);
            if (dupl.id().equals(serie.m_selectedElectrostaticModel.id())) {
                copy.setSelectedElectrostaticModel(dupl);
            }
        }
        //copy.setSelectedElectrostaticModel(serie.m_selectedElectrostaticModel);

        copy.m_stericInteractionModels.clear();
        for (int i = 0; i < serie.getStericModelCount(); i++) {
            StericInteractionModel dupl = (StericInteractionModel) serie.getStericModel(i).duplicate();
            copy.m_stericInteractionModels.add(dupl);
            if (dupl.id().equals(serie.m_selectedStericModel.id())) {
                copy.setSelectedStericModel(dupl);
            }
        }
        //copy.setSelectedStericModel(serie.m_selectedStericModel);

        copy.m_miscInteractionModels.clear();
        for (int i = 0; i < serie.getMiscModelCount(); i++) {
            copy.m_miscInteractionModels.add((MiscInteractionModel) serie.getMiscModel(i).duplicate());
        }

        copy.setTemperature(serie.getTemperature());
        copy.setDistance(serie.getDistanceAsText());
        copy.setHeterogeneous(serie.getHeterogeneous());

        copy.setParticle1(Particle.newInstance(serie.getParticle1()));
        copy.setParticle2(Particle.newInstance(serie.getParticle2()));
        copy.setMedium(Medium.newInstance(serie.getMedium()));

        copy.m_stability = Stability.newInstance(serie.m_stability);
        copy.setShowStabilty(serie.getShowStability());

        copy.m_yodel = Yodel.newInstance(serie.m_yodel);

        copy.m_unsavedChanges = true;

        return copy;
    }

    /**
     * Load all the classes in the plugins folder, which implement the interface
     * given by the class_name parameter
     *
     * @param Name of the interface class to be loaded. Used to differentiate
     * between the different plugin models
     * @return
     */
    static ArrayList<InteractionModel> getPlugins(String class_name) {

        ArrayList<InteractionModel> plugins = new ArrayList<>();

        File plugins_dir = new File("plugins");

        if (plugins_dir.exists() && plugins_dir.isDirectory()) {

            String[] files = plugins_dir.list();
            for (String file : files) {
                try {
                    if (file.endsWith(".class")) {
                        ClassLoader loader = new PluginClassLoader(plugins_dir);
                        Class c = loader.loadClass(file.substring(0, file.indexOf(".")));
                        Class[] intf = c.getInterfaces();
                        for (Class intf1 : intf) {
                            if (intf1.getName().equals(class_name)) {
                                InteractionModel model = (InteractionModel) c.newInstance();
                                plugins.add(model);
                            }
                        }
                    } else if (file.endsWith(".jar")) {
                        //loop over everything in jar file
                        JarFile jarFile = new JarFile("plugins/" + file);
                        Enumeration e = jarFile.entries();
                        while (e.hasMoreElements()) {
                            JarEntry je = (JarEntry) e.nextElement();
                            //extract class name and load the class for this element of jar file
                            String className = je.getName();
                            if (className.endsWith(".class")) {
                                className = className.substring(0, je.getName().indexOf("."));
                                className = className.replace('/', '.');
                                URL[] urls = {new URL("jar:file:plugins/" + file + "!/")};
                                URLClassLoader loader = URLClassLoader.newInstance(urls);
                                Class c = loader.loadClass(className);
                                //inspect interfaces and add them if it matches the requested one
                                Class[] intf = c.getInterfaces();
                                for (Class intf1 : intf) {
                                    if (intf1.getName().equals(class_name)) {
                                        InteractionModel model = (InteractionModel) c.newInstance();
                                        plugins.add(model);
                                    }
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException ex) {
                }
            }
        }

        return plugins;
    }

    /**
     * Private routine to check that no two models have the id. So far it just
     * displays a warning but does continue.
     */
    private void checkModelIDs() {

        ArrayList<InteractionModel> allModels = new ArrayList<>();

        allModels.addAll(m_dispersionInteractionModels);
        allModels.addAll(m_electrostaticInteractionModels);
        allModels.addAll(m_stericInteractionModels);
        allModels.addAll(m_miscInteractionModels);

        for (InteractionModel model1 : allModels) {
            int count = 0;
            String id1 = model1.id();

            for (InteractionModel model2 : allModels) {
                String id2 = model2.id();

                if (id1.equals(id2)) {
                    count++;

                    if (count > 1) {
                        JOptionPane.showMessageDialog(null,
                                "Duplicate model IDs detected: " + id1 + " and " + id2 + ".\nThis will likely lead to problems! Please check your plugins folder!",
                                "Duplicate Model ID",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

        }

    }

    /**
     * Static routine to create the array of all built-in dispersion models as
     * well as the one provided by plugins.
     *
     * @return ArrayList containing all dispersion models
     */
    static ArrayList<DispersionInteractionModel> dispersionModels() {
        ArrayList<DispersionInteractionModel> models = new ArrayList<>();

        models.add(new Dispersion_None());
        models.add(new Dispersion_NonRetarded());
        models.add(new Dispersion_Gregory());
        models.add(new Dispersion_Vincent());
        models.add(new Dispersion_Effective());
        models.add(new Dispersion_Lifshitz());

        //Load Plugins
        ArrayList<InteractionModel> plugins = getPlugins("hamaker2.models.dispersion.DispersionInteractionModel");
        for (InteractionModel plugin : plugins) {
            models.add((DispersionInteractionModel) plugin);
        }

        return models;
    }

    /**
     * Static routine to create the array of all built-in electrostatic models
     * as well as the one provided by plugins.
     *
     * @return ArrayList containing all electrostatic models
     */
    static ArrayList<ElectrostaticInteractionModel> electrostaticModels() {
        ArrayList<ElectrostaticInteractionModel> models = new ArrayList<>();

        models.add(new Electrostatic_None());
        models.add(new Electrostatic_HFF());
        models.add(new Electrostatic_LSA());

        //Load Plugins
        ArrayList<InteractionModel> plugins = getPlugins("hamaker2.models.electrostatic.ElectrostaticInteractionModel");
        for (InteractionModel plugin : plugins) {
            models.add((ElectrostaticInteractionModel) plugin);
        }

        return models;
    }

    /**
     * Static routine to create the array of all built-in steric models as well
     * as the one provided by plugins.
     *
     * @return ArrayList containing all steric models
     */
    static ArrayList<StericInteractionModel> stericModels() {
        ArrayList<StericInteractionModel> models = new ArrayList<>();

        models.add(new Steric_None());
        models.add(new Steric_Bergstrom());
        //models.add(new Steric_Brush());    //Brush is deactivated for now
        //models.add(new Steric_Mushroom()); // Mushroom is deactivated for now

        //Load Plugins
        ArrayList<InteractionModel> plugins = getPlugins("hamaker2.models.steric.StericInteractionModel");
        for (InteractionModel plugin : plugins) {
            models.add((StericInteractionModel) plugin);
        }

        return models;
    }

    /**
     * Static routine to create the array of all built-in miscellaneous models
     * as well as the one provided by plugins.
     *
     * @return ArrayList containing all misc models
     */
    static ArrayList<MiscInteractionModel> miscModels() {
        ArrayList<MiscInteractionModel> models = new ArrayList<>();
        models.add(new Misc_LifshitzMagnetic());

        //Load Plugins
        ArrayList<InteractionModel> plugins = getPlugins("hamaker2.models.misc.MiscInteractionModel");
        for (InteractionModel plugin : plugins) {
            models.add((MiscInteractionModel) plugin);
        }

        return models;
    }

    /**
     * Retrieve the name of the Series
     *
     * @return Series name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Return a string representation of the series. For now that is just the
     * name
     *
     * @return The name of the series
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Set the series name
     *
     * @param name New name for the series
     */
    public void setName(String name) {
        m_name = name;
        m_unsavedChanges = true;
    }

    /**
     * Retrieve the series line color
     *
     * @return Java Color object
     */
    public Color getColor() {
        return m_color;
    }

    /**
     * Set the series line color
     *
     * @param color The new Java Color object for the series
     */
    public void setColor(Color color) {
        m_color = color;
        m_unsavedChanges = true;
    }

    /**
     * Retrieve the series grid color (used only for 3D plots)
     *
     * @return The Java Color object for the grid color
     */
    public Color getGridColor() {
        return m_gridColor;
    }

    /**
     * Set the grid color (used only in 3D plots) for the series
     *
     * @param color The new Java Color object to be used to draw grid-lines
     */
    public void setGridColor(Color color) {
        m_gridColor = color;
        m_unsavedChanges = true;
    }

    /**
     * Retrieve the visibility of the Series
     *
     * @return Series visibility
     */
    public boolean getVisible() {
        return m_visible;
    }

    /**
     * Set the Series visibility
     *
     * @param visible Boolean indicating if the series is visible
     */
    public void setVisible(boolean visible) {
        m_visible = visible;
        m_unsavedChanges = true;
    }

    /**
     * Filter all existing dispersion models and return only those implementing
     * the selected geometry class
     *
     * @param models The ArrayList of models to be filtered
     * @return A new ArrayList with only the valid models
     */
    private ArrayList<DispersionInteractionModel> validDispersionModels(ArrayList<DispersionInteractionModel> models) {
        ArrayList<DispersionInteractionModel> list = new ArrayList<>();

        for (DispersionInteractionModel model : models) {
            if (model.ImplementedGeometryClasses().contains(m_geometryClass)) {
                list.add(model);
            }
        }

        return list;
    }

    /**
     * Filter all existing electrostatic models and return only those
     * implementing the selected geometry class
     *
     * @param models The ArrayList of models to be filtered
     * @return A new ArrayList with only the valid models
     */
    private ArrayList<ElectrostaticInteractionModel> validElectrostaticModels(ArrayList<ElectrostaticInteractionModel> models) {
        ArrayList<ElectrostaticInteractionModel> list = new ArrayList<>();

        for (ElectrostaticInteractionModel model : models) {
            if (model.ImplementedGeometryClasses().contains(m_geometryClass)) {
                list.add(model);
            }
        }

        return list;
    }

    /**
     * Filter all existing steric models and return only those implementing the
     * selected geometry class
     *
     * @param models The ArrayList of models to be filtered
     * @return A new ArrayList with only the valid models
     */
    private ArrayList<StericInteractionModel> validStericModels(ArrayList<StericInteractionModel> models) {
        ArrayList<StericInteractionModel> list = new ArrayList<>();

        for (StericInteractionModel model : models) {
            if (model.ImplementedGeometryClasses().contains(m_geometryClass)) {
                list.add(model);
            }
        }

        return list;
    }

    //////////////////////////////////////////////////////////////
    // Dispersion models
    //////////////////////////////////////////////////////////////
    
    /**
     * Retrieve a list of all dispersion models
     *
     * @return ArrayList containing all dispersion models
     */
    public ArrayList<DispersionInteractionModel> getDispersionModels() {
        return m_dispersionInteractionModels;
    }

    /**
     * Return the number of dispersion models
     *
     * @return Dispersion model count
     */
    public int getDispersionModelCount() {
        return validDispersionModels(m_dispersionInteractionModels).size();
    }

    /**
     * Return the dispersion model at a given index
     *
     * @param i The index of the model
     * @return DispersionInteractionModel at the given index
     */
    public DispersionInteractionModel getDispersionModel(int i) {
        return (DispersionInteractionModel) validDispersionModels(m_dispersionInteractionModels).get(i);
    }

    /**
     * Retrieve a dispersion model identified by it's ID
     *
     * @param id The model id
     * @return First DispersionInteractionModel that matches the id. Null
     * otherwise.
     */
    public DispersionInteractionModel getDispersionModelWithID(String id) {
        for (DispersionInteractionModel model : m_dispersionInteractionModels) {
            if (model.id().equals(id)) {
                return model;
            }
        }

        return null;
    }

    /**
     * Retrieve the selected dispersion model
     *
     * @return The currently selected model
     */
    public DispersionInteractionModel getSelectedDispersionModel() {
        return m_selectedDispersionModel;
    }

    /**
     * Retrieve the ID of the currently selected dispersion model
     *
     * @return ID of the currently selected dispersion model
     */
    public String getSelectedDispersionModelID() {
        return m_selectedDispersionModel.id();
    }

    /**
     * Set the currently selected dispersion model
     *
     * @param model The model to be set to the selected model
     */
    public void setSelectedDispersionModel(DispersionInteractionModel model) {
        m_selectedDispersionModel = model;
        m_unsavedChanges = true;
    }

    /**
     * Return the number of dispersion models, which have additional parameters
     *
     * @return Number of models having additional parameters.
     */
    public int getAdditionalParametersDispersionModelCount() {
        int count = 0;
        for (DispersionInteractionModel m_dispersionInteractionModel : m_dispersionInteractionModels) {
            if (m_dispersionInteractionModel.additionalParameters()) {
                count++;
            }
        }

        return count;
    }
    
    //////////////////////////////////////////////////////////////
    //electrostatic models
    //////////////////////////////////////////////////////////////
    
    /**
     * Retrieve a list of all electrostatic models
     *
     * @return List of all electrostatic models
     */
    public ArrayList<ElectrostaticInteractionModel> getElectrostaticModels() {
        return m_electrostaticInteractionModels;
    }

    /**
     * Return the number of electrostatic models
     *
     * @return The number of electrostatic models
     */
    public int getElectrostaticModelCount() {
        return validElectrostaticModels(m_electrostaticInteractionModels).size();
    }

    /**
     * Return the electrostatic model at a given index
     *
     * @param i Index of the electrostatic model
     * @return Electrostatic model at given index
     */
    public ElectrostaticInteractionModel getElectrostaticModel(int i) {
        return (ElectrostaticInteractionModel) validElectrostaticModels(m_electrostaticInteractionModels).get(i);
    }

    /**
     * Return the electrostatic model with given ID
     *
     * @param id ID of the electrostatic model
     * @return Electrostatic model with given ID
     */
    public ElectrostaticInteractionModel getElectrostaticModelWithID(String id) {
        for (ElectrostaticInteractionModel model : m_electrostaticInteractionModels) {
            if (model.id().equals(id)) {
                return model;
            }
        }

        return null;
    }

    /**
     * Return the selected electrostatic model
     *
     * @return Selected electrostatic model
     */
    public ElectrostaticInteractionModel getSelectedElectrostaticModel() {
        return m_selectedElectrostaticModel;
    }

    /**
     * Return the ID of the selected electrostatic model
     *
     * @return ID of the selected electrostatic model
     */
    public String getSelectedElectrostaticModelID() {
        return m_selectedElectrostaticModel.id();
    }

    /**
     * Select the given electrostatic model
     *
     * @param model Model to be selected
     */
    public void setSelectedElectrostaticModel(ElectrostaticInteractionModel model) {
        m_selectedElectrostaticModel = model;
        m_unsavedChanges = true;
    }

    /**
     * Return the number of electrostatic models with additional parameter
     * dialogs
     *
     * @return Number of models
     */
    public int getAdditionalParametersElectrostaticModelCount() {
        int count = 0;
        for (ElectrostaticInteractionModel m_electrostaticInteractionModel : m_electrostaticInteractionModels) {
            if (m_electrostaticInteractionModel.additionalParameters()) {
                count++;
            }
        }

        return count;
    }

    //////////////////////////////////////////////////////////////
    //steric models
    //////////////////////////////////////////////////////////////
    
    /**
     * Return the list of all steric models
     * @return List of all steric models
     */
    public ArrayList<StericInteractionModel> getStericModels() {
        return m_stericInteractionModels;
    }

    /**
     * Return the number of steric models
     * @return Number of steric models
     */
    public int getStericModelCount() {
        return validStericModels(m_stericInteractionModels).size();
    }

    /**
     * Return the steric model with given index
     * @param i Index of the model
     * @return Steric model at given index
     */
    public StericInteractionModel getStericModel(int i) {
        return (StericInteractionModel) validStericModels(m_stericInteractionModels).get(i);
    }

    /**
     * Return the steric model with given ID
     * @param id ID of the model
     * @return Steric model with given index
     */
    public StericInteractionModel getStericModelWithID(String id) {
        for (StericInteractionModel model : m_stericInteractionModels) {
            if (model.id().equals(id)) {
                return model;
            }
        }

        return null;
    }

    /**
     * Return the selected steric model
     * @return Selected steric model
     */
    public StericInteractionModel getSelectedStericModel() {
        return m_selectedStericModel;
    }

    /**
     * Return the id of the selected steric model
     * @return ID of the selected steric model
     */
    public String getSelectedStericModelID() {
        return m_selectedStericModel.id();
    }

    /**
     * Set the selected steric model
     * @param model Model to be selected
     */
    public void setSelectedStericModel(StericInteractionModel model) {
        m_selectedStericModel = model;
        m_unsavedChanges = true;
    }

    /**
     * Return the number of steric models that have additional parameters
     * @return Number of steric models with additional parameters
     */
    public int getAdditionalParametersStericModelCount() {
        int count = 0;
        for (StericInteractionModel m_stericInteractionModel : m_stericInteractionModels) {
            if (m_stericInteractionModel.additionalParameters()) {
                count++;
            }
        }

        return count;
    }

    //////////////////////////////////////////////////////////////
    //misc models
    //////////////////////////////////////////////////////////////
    
    /**
     * Return the list of all misc models
     * @return List of all misc models
     */
    public ArrayList<MiscInteractionModel> getMiscModels() {
        return m_miscInteractionModels;
    }

    /**
     * Return the number of misc models
     * @return Number of misc models
     */
    public int getMiscModelCount() {
        return m_miscInteractionModels.size();
    }

    /**
     * Return the misc model at the given index
     * @param index Index of the misc model
     * @return Misc model at given index
     */
    public MiscInteractionModel getMiscModel(int index) {
        return (MiscInteractionModel) m_miscInteractionModels.get(index);
    }

    /**
     * Return the misc model with given ID
     * @param id The ID of the model
     * @return Misc model with given ID
     */
    public MiscInteractionModel getMiscModelWithID(String id) {
        for (MiscInteractionModel model : m_miscInteractionModels) {
            if (model.id().equals(id)) {
                return model;
            }
        }

        return null;
    }

    /**
     * Set the misc models to the given list
     * @param models List of misc models
     */
    public void setMiscModels(ArrayList<MiscInteractionModel> models) {
        m_miscInteractionModels = models;
    }

    /**
     * Return the number of misc models with additional parameters
     * @return Number of misc models with additional parameters
     */
    public int getAdditionalParametersMiscModelCount() {
        int count = 0;
        for (MiscInteractionModel m_miscInteractionModel : m_miscInteractionModels) {
            if (m_miscInteractionModel.additionalParameters()) {
                count++;
            }
        }

        return count;
    }

    ///////////////////////////////////////////////////////////////
    // Other parameters
    ///////////////////////////////////////////////////////////////
    
    /**
     * Return the interparticle distance in m
     * @return Distance in nm
     */
    public double getDistance() {
        return m_distance;
    }

    /**
     * Return the interparticle distance as a formatted string in nm
     * @return Distance string in nm
     */
    public String getDistanceAsText() {
        DecimalFormat format = new DecimalFormat("0.000");
        return format.format(m_distance * 1E9);
    }

    /**
     * Set interparticle distance from text in nm
     * @param distance Distance in nm
     */
    public void setDistance(String distance) {
        m_distance = Utils.StringToDouble(distance) * 1E-9;
        m_unsavedChanges = true;
    }

    /**
     * Return the temperature in K
     * @return Temperature in K
     */
    public double getTemperature() {
        return m_temperature;
    }

    /**
     * Return the temperature in K as a formatted string
     * @return Temperature in K
     */
    public String getTemperatureAsText() {
        DecimalFormat format = new DecimalFormat("0.000");
        return format.format(m_temperature);
    }

    /**
     * Set the temperature
     * @param temp Temperature in K
     */
    public void setTemperature(double temp) {
        m_temperature = temp;
        m_unsavedChanges = true;
    }

    /**
     * Set the temperature from a string in K
     * @param temp Temperature string in K
     */
    public void setTemperature(String temp) {
        m_temperature = Utils.StringToDouble(temp);
        m_unsavedChanges = true;
    }

    /**
     * Return if the series is heterogeneous
     * @return Is the series heterogeneous
     */
    public boolean getHeterogeneous() {
        return m_heterogeneous;
    }

    /**
     * Set if the series is heterogeneous
     * @param heterogeneous Is the series heterogeneous
     */
    public void setHeterogeneous(boolean heterogeneous) {
        m_heterogeneous = heterogeneous;
        m_unsavedChanges = true;
    }

    /**
     * Return the first body
     * @return first body
     */
    public Particle getParticle1() {
        return m_particle1;
    }

    /**
     * Set the first body
     * @param particle First body
     */
    public void setParticle1(Particle particle) {
        m_particle1 = particle;
    }

    /**
     * Return the second body
     * @return Second body
     */
    public Particle getParticle2() {
        return m_particle2;
    }

    /**
     * Set the second body
     * @param particle Second body
     */
    public void setParticle2(Particle particle) {
        m_particle2 = particle;
    }

    /**
     * Return the dispersion medium
     * @return DIspersion medium
     */
    public Medium getMedium() {
        return m_medium;
    }

    /**
     * Set the dispersion medium
     * @param medium
     */
    public void setMedium(Medium medium) {
        m_medium = medium;
    }

    /**
     * Return if the series needs saving
     * @return Does series need saving
     */
    public boolean getNeedsSave() {
        if (m_unsavedChanges) {
            return m_unsavedChanges;
        }
        if (m_particle1.getNeedsSave()) {
            return true;
        }
        if (m_particle2.getNeedsSave()) {
            return true;
        }
        if (m_medium.getNeedsSave()) {
            return true;
        }
        for (int i = 0; i < getDispersionModelCount(); i++) {
            if (getDispersionModel(i).getNeedsSave()) {
                return true;
            }
        }
        for (int i = 0; i < getElectrostaticModelCount(); i++) {
            if (getElectrostaticModel(i).getNeedsSave()) {
                return true;
            }
        }
        for (int i = 0; i < getStericModelCount(); i++) {
            if (getStericModel(i).getNeedsSave()) {
                return true;
            }
        }
        for (int i = 0; i < getMiscModelCount(); i++) {
            if (getMiscModel(i).getNeedsSave()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set if the series needs saving
     * @param s
     */
    public void setNeedsSave(boolean s) {
        m_unsavedChanges = s;
    }

    /**
     * Return the stability calculator
     * @return Stability calculator
     */
    public Stability getStability() {
        return m_stability;
    }

    /**
     * Return the barrier required for colloidal stability
     * @return Required barrier
     */
    public double getRequiredBarrier() {
        return m_stability.getBarrier(m_particle1, m_particle2, m_medium, kB, m_temperature);
    }

    /**
     * Return if the stability line is to be shown
     * @return Show stability line
     */
    public boolean getShowStability() {
        return m_showStability;
    }

    /**
     * Set if the stability line is to be shown
     * @param stability Is the stability line to be shown
     */
    public void setShowStabilty(boolean stability) {
        m_showStability = stability;
    }

    /**
     * Return the interaction potential for this series at current parameters
     * @return Interaction potential
     */
    public double getInteractionPotential() {
        double interaction = 0.0;

        if (!m_heterogeneous) {

            double total_fraction = 0;

            ParticleSizeDistribution d = m_particle1.getSelectedSizeDistribution();
            int count = d.numPoints();
            double diameters[] = d.diameters();
            double fractions[] = d.numberFractions();

            for (int i = 0; i < count; i++) {

                double size = diameters[i];
                double fraction = fractions[i];

                total_fraction += fraction;

                interaction += fraction * getSelectedDispersionModel().interactionPotential(this, size);
                interaction += fraction * getSelectedElectrostaticModel().interactionPotential(this, size);
                interaction += fraction * getSelectedStericModel().interactionPotential(this, size);

                for (MiscInteractionModel m_miscInteractionModel : m_miscInteractionModels) {
                    if (m_miscInteractionModel.getActive()) {
                        interaction += fraction * ((MiscInteractionModel) m_miscInteractionModel).interactionPotential(this, size);
                    }
                }
            }

            interaction /= total_fraction;

        } else {

            double total_fraction = 0;

            ParticleSizeDistribution d1 = m_particle1.getSelectedSizeDistribution();
            int count1 = d1.numPoints();
            double diameters1[] = d1.diameters();
            double fractions1[] = d1.numberFractions();

            ParticleSizeDistribution d2 = m_particle2.getSelectedSizeDistribution();
            int count2 = d2.numPoints();
            double diameters2[] = d2.diameters();
            double fractions2[] = d2.numberFractions();

            for (int i1 = 0; i1 < count1; i1++) {

                double size1 = diameters1[i1];
                double fraction1 = fractions1[i1];

                for (int i2 = 0; i2 < count2; i2++) {

                    double size2 = diameters2[i2];
                    double fraction2 = fractions2[i2];

                    total_fraction += fraction1 * fraction2;

                    interaction += fraction1 * fraction2 * getSelectedDispersionModel().interactionPotential(this, size1, size2);
                    interaction += fraction1 * fraction2 * getSelectedElectrostaticModel().interactionPotential(this, size1, size2);
                    interaction += fraction1 * fraction2 * getSelectedStericModel().interactionPotential(this, size1, size2);

                    for (MiscInteractionModel m_miscInteractionModel : m_miscInteractionModels) {
                        if (m_miscInteractionModel.getActive()) {
                            interaction += fraction1 * fraction2 * ((MiscInteractionModel) m_miscInteractionModel).interactionPotential(this, size1, size2);
                        }
                    }
                }
            }

            interaction /= total_fraction;

        }

        interaction /= kB * m_temperature;

        return interaction;
    }

    /**
     * Return the interaction force for this series at current parameters
     * @return Interaction force
     */
    public double getInteractionForce() {
        double interaction = 0.0;

        if (!m_heterogeneous) {

            double total_fraction = 0;

            ParticleSizeDistribution d = m_particle1.getSelectedSizeDistribution();
            int count = d.numPoints();
            double diameters[] = d.diameters();
            double fractions[] = d.numberFractions();

            for (int i = 0; i < count; i++) {

                double size = diameters[i];
                double fraction = fractions[i];

                total_fraction += fraction;

                interaction += fraction * getSelectedDispersionModel().interactionForce(this, size);
                interaction += fraction * getSelectedElectrostaticModel().interactionForce(this, size);
                interaction += fraction * getSelectedStericModel().interactionForce(this, size);

                for (MiscInteractionModel m_miscInteractionModel : m_miscInteractionModels) {
                    if (m_miscInteractionModel.getActive()) {
                        interaction += fraction * ((MiscInteractionModel) m_miscInteractionModel).interactionForce(this, size);
                    }
                }
            }

            interaction /= total_fraction;

        } else {

            double total_fraction = 0;

            ParticleSizeDistribution d1 = m_particle1.getSelectedSizeDistribution();
            int count1 = d1.numPoints();
            double diameters1[] = d1.diameters();
            double fractions1[] = d1.numberFractions();

            ParticleSizeDistribution d2 = m_particle2.getSelectedSizeDistribution();
            int count2 = d2.numPoints();
            double diameters2[] = d2.diameters();
            double fractions2[] = d2.numberFractions();

            for (int i1 = 0; i1 < count1; i1++) {

                double size1 = diameters1[i1];
                double fraction1 = fractions1[i1];

                for (int i2 = 0; i2 < count2; i2++) {

                    double size2 = diameters2[i2];
                    double fraction2 = fractions2[i2];

                    total_fraction += fraction1 * fraction2;

                    interaction += fraction1 * fraction2 * getSelectedDispersionModel().interactionForce(this, size1, size2);
                    interaction += fraction1 * fraction2 * getSelectedElectrostaticModel().interactionForce(this, size1, size2);
                    interaction += fraction1 * fraction2 * getSelectedStericModel().interactionForce(this, size1, size2);

                    for (MiscInteractionModel m_miscInteractionModel : m_miscInteractionModels) {
                        if (m_miscInteractionModel.getActive()) {
                            interaction += fraction1 * fraction2 * ((MiscInteractionModel) m_miscInteractionModel).interactionForce(this, size1, size2);
                        }
                    }
                }
            }

            interaction /= total_fraction;

        }

        interaction /= kB * m_temperature;
        interaction *= 1E-9;

        return interaction;
    }

    /**
     * Return the average harmonic radius for this series
     * @return Average harmonic radius
     */
    public double averageHarmonicRadius() {
        double radius = 0.0;

        if (!m_heterogeneous) {

            double total_fraction = 0;

            ParticleSizeDistribution d = m_particle1.getSelectedSizeDistribution();
            int count = d.numPoints();
            double diameters[] = d.diameters();
            double fractions[] = d.numberFractions();

            for (int i = 0; i < count; i++) {

                double size = diameters[i];
                double fraction = fractions[i];

                total_fraction += fraction;
                radius += fraction * 0.5 * size;
            }

            radius /= total_fraction;

        } else {

            double total_fraction = 0;

            ParticleSizeDistribution d1 = m_particle1.getSelectedSizeDistribution();
            int count1 = d1.numPoints();
            double diameters1[] = d1.diameters();
            double fractions1[] = d1.numberFractions();

            ParticleSizeDistribution d2 = m_particle2.getSelectedSizeDistribution();
            int count2 = d2.numPoints();
            double diameters2[] = d2.diameters();
            double fractions2[] = d2.numberFractions();

            for (int i1 = 0; i1 < count1; i1++) {

                double size1 = diameters1[i1];
                double fraction1 = fractions1[i1];

                for (int i2 = 0; i2 < count2; i2++) {

                    double size2 = diameters2[i2];
                    double fraction2 = fractions2[i2];

                    total_fraction += fraction1 * fraction2;
                    radius += fraction1 * fraction2 * (size1 * size2 / (size1 + size2));
                }
            }

            radius /= total_fraction;
        }

        return radius;
    }

    ////////////////////////////////////////////////////////////////////////
    // Geometry class handling
    ////////////////////////////////////////////////////////////////////////
    
    /**
     * Return the selected geometry class
     * @return Selected geometry class
     */
    public GeometryClass getSelectedGeometryClass() {
        return m_geometryClass;
    }

    /**
     * Set the selected geometry class
     * @param geometry Selected geometry class
     */
    public void setSelectedGeometryClass(GeometryClass geometry) {

        //validate the models with respect to the geometry class - otherwise set to None
        if (!getSelectedDispersionModel().ImplementedGeometryClasses().contains(geometry)) {
            setSelectedDispersionModel(m_dispersionInteractionModels.get(0));
        }

        if (!getSelectedElectrostaticModel().ImplementedGeometryClasses().contains(geometry)) {
            setSelectedElectrostaticModel(m_electrostaticInteractionModels.get(0));
        }

        if (!getSelectedStericModel().ImplementedGeometryClasses().contains(geometry)) {
            setSelectedStericModel(m_stericInteractionModels.get(0));
        }

        for (int i = 0; i < getMiscModelCount(); i++) {
            if (!getMiscModel(i).ImplementedGeometryClasses().contains(geometry)) {
                getMiscModel(i).setActive(false);
            }
        }

        m_geometryClass = geometry;
    }

    ////////////////////////////////////////////////////////////////////////
    // Extrema handling
    ////////////////////////////////////////////////////////////////////////
    
    /**
     * Return the position of the minimum
     * @return Minimum position
     */
    public String getMinimumPosition() {
        return m_minimum[0];
    }

    /**
     * Return the potential or force at the minimum
     * @return Minimum potential
     */
    public String getMinimumPotentialOrForce() {
        return m_minimum[1];
    }

    /**
     * Return the position of the maximum
     * @return Maximum position
     */
    public String getMaximumPosition() {
        return m_maximum[0];
    }

    /**
     * Return the potential or force at the maximum
     * @return
     */
    public String getMaximumPotentialOrForce() {
        return m_maximum[1];
    }

    /**
     * Set the minimum position and potential or force.
     * @param position Position of the minimum
     * @param potential Potential or force of the minimum
     */
    public void setMinimum(String position, String potential) {
        m_minimum[0] = position;
        m_minimum[1] = potential;
    }

    /**
     * Set the maximum position and potential or force.
     * @param position Position of the minimum
     * @param potential Potential or force of the minimum
     */
    public void setMaximum(String position, String potential) {
        m_maximum[0] = position;
        m_maximum[1] = potential;
    }

    ////////////////////////////////////////////////////////////////////////
    // Plot variable handling
    ////////////////////////////////////////////////////////////////////////

    
    /**
     * Return a list of all plot variables prefixed with given parameters
     * @param namePrefix Prefix to append to human readable names
     * @param idPrefix Prefix to append to machine parseable IDs
     * @return List of all plot variables
     */
    @Override
    public ArrayList<PlotVariable> plotVariables(String namePrefix, String idPrefix) {

        namePrefix = "";
        idPrefix = "serie_";

        ArrayList<PlotVariable> vars = new ArrayList<>();

        vars.add(new PlotVariable(idPrefix, namePrefix, "distance", "Distance", "nm", 0.0, 15E-9, 1E9, "0.0"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "temperature", "Temperature", "K", 273.0, 373.0, 1.0, "0.0"));

        //medium
        vars.addAll(m_medium.plotVariables(namePrefix + "Medium - ", idPrefix + "medium_"));

        //particle 1
        vars.addAll(m_particle1.plotVariables(namePrefix + "Body 1 - ", idPrefix + "body1_"));

        //particle 2
        vars.addAll(m_particle2.plotVariables(namePrefix + "Body 2 - ", idPrefix + "body2_"));

        for (DispersionInteractionModel dispModel : dispersionModels()) {
            AbstractInteractionModel m = (AbstractInteractionModel) dispModel;
            vars.addAll(m.plotVariables(namePrefix + "Dispersion - " + m.name() + " - ", idPrefix + m.id() + "_"));
        }

        for (ElectrostaticInteractionModel elecModel : electrostaticModels()) {
            AbstractInteractionModel m = (AbstractInteractionModel) elecModel;
            vars.addAll(m.plotVariables(namePrefix + "Electrostatic - " + m.name() + " - ", idPrefix + m.id() + "_"));
        }

        for (StericInteractionModel sterModel : stericModels()) {
            AbstractInteractionModel m = (AbstractInteractionModel) sterModel;
            vars.addAll(m.plotVariables(namePrefix + "Steric - " + m.name() + " - ", idPrefix + m.id() + "_"));
        }

        for (MiscInteractionModel miscModel : miscModels()) {
            AbstractInteractionModel m = (AbstractInteractionModel) miscModel;
            vars.addAll(m.plotVariables(namePrefix + "Misc - " + m.name() + " - ", idPrefix + m.id() + "_"));
        }

        //sort before returning
        Collections.sort(vars, new Comparator<PlotVariable>() {
            @Override
            public int compare(PlotVariable p1, PlotVariable p2) {
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });

        return vars;
    }

    /**
     * Return the current value of a plot variable
     * @param id ID of the plot variable (prefix removed)
     * @return Value of the plot variable
     */
    @Override
    public double getPlotVariableValue(String id) {

        if (id.startsWith("serie_")) {
            id = id.replaceFirst("serie_", "");
        } else {
            System.out.println("ID should have started with serie_ but did not");
        }

        if (id.startsWith("medium_")) {
            return m_medium.getPlotVariableValue(id.replaceFirst("medium_", ""));
        } else if (id.startsWith("body1_")) {
            return m_particle1.getPlotVariableValue(id.replaceFirst("body1_", ""));
        } else if (id.startsWith("body2_")) {
            return m_particle2.getPlotVariableValue(id.replaceFirst("body2_", ""));

        } else if (id.startsWith("dispersion_")) {
            String model_id = "dispersion_" + id.split("_")[1];
            id = id.replaceFirst(model_id + "_", "");
            AbstractInteractionModel m = (AbstractInteractionModel) getDispersionModelWithID(model_id);
            if (m != null) {
                return m.getPlotVariableValue(id);
            } else {
                System.out.println("Failed to find model with ID: " + model_id);
            }

        } else if (id.startsWith("electrostatic_")) {
            String model_id = "electrostatic_" + id.split("_")[1];
            id = id.replaceFirst(model_id + "_", "");
            AbstractInteractionModel m = (AbstractInteractionModel) getElectrostaticModelWithID(model_id);
            if (m != null) {
                return m.getPlotVariableValue(id);
            } else {
                System.out.println("Failed to find model with ID: " + model_id);
            }

        } else if (id.startsWith("steric_")) {
            String model_id = "steric_" + id.split("_")[1];
            id = id.replaceFirst(model_id + "_", "");
            AbstractInteractionModel m = (AbstractInteractionModel) getStericModelWithID(model_id);
            if (m != null) {
                return m.getPlotVariableValue(id);
            } else {
                System.out.println("Failed to find model with ID: " + model_id);
            }

        } else if (id.startsWith("misc_")) {
            String model_id = "misc_" + id.split("_")[1];
            id = id.replaceFirst(model_id + "_", "");
            AbstractInteractionModel m = (AbstractInteractionModel) getMiscModelWithID(model_id);
            if (m != null) {
                return m.getPlotVariableValue(id);
            } else {
                System.out.println("Failed to find model with ID: " + model_id);
            }

        } else if (id.equals("distance")) {
            return m_distance;
        } else if (id.equals("temperature")) {
            return m_temperature;
        } else {
            System.out.println("Unknown plot variable: " + id);
        }

        return PlotVariable.kUnknownPlotVariableID;
    }

    /**
     * Set the value of a plot variable
     * @param id ID of the plot variable (prefix removed)
     * @param value Value of the plot variable
     */
    @Override
    public void setPlotVariableValue(String id, double value) {

        if (id.startsWith("serie_")) {
            id = id.replaceFirst("serie_", "");
        } else {
            System.out.println("ID should have started with serie_ but did not");
        }

        if (id.startsWith("medium_")) {
            m_medium.setPlotVariableValue(id.replaceFirst("medium_", ""), value);
        } else if (id.startsWith("body1_")) {
            m_particle1.setPlotVariableValue(id.replaceFirst("body1_", ""), value);
        } else if (id.startsWith("body2_")) {
            m_particle2.setPlotVariableValue(id.replaceFirst("body2_", ""), value);

        } else if (id.startsWith("dispersion_")) {
            String model_id = "dispersion_" + id.split("_")[1];
            id = id.replaceFirst(model_id + "_", "");
            AbstractInteractionModel m = (AbstractInteractionModel) getDispersionModelWithID(model_id);
            if (m != null) {
                m.setPlotVariableValue(id, value);
            } else {
                System.out.println("Failed to find model with ID: " + model_id);
            }

        } else if (id.startsWith("electrostatic_")) {
            String model_id = "electrostatic_" + id.split("_")[1];
            id = id.replaceFirst(model_id + "_", "");
            AbstractInteractionModel m = (AbstractInteractionModel) getElectrostaticModelWithID(model_id);
            if (m != null) {
                m.setPlotVariableValue(id, value);
            } else {
                System.out.println("Failed to find model with ID: " + model_id);
            }

        } else if (id.startsWith("steric_")) {
            String model_id = "steric_" + id.split("_")[1];
            id = id.replaceFirst(model_id + "_", "");
            AbstractInteractionModel m = (AbstractInteractionModel) getStericModelWithID(model_id);
            if (m != null) {
                m.setPlotVariableValue(id, value);
            } else {
                System.out.println("Failed to find model with ID: " + model_id);
            }

        } else if (id.startsWith("misc_")) {
            String model_id = "misc_" + id.split("_")[1];
            id = id.replaceFirst(model_id + "_", "");
            AbstractInteractionModel m = (AbstractInteractionModel) getMiscModelWithID(model_id);
            if (m != null) {
                m.setPlotVariableValue(id, value);
            } else {
                System.out.println("Failed to find model with ID: " + model_id);
            }

        } else if (id.equals("distance")) {
            m_distance = value;
        } else if (id.equals("temperature")) {
            m_temperature = value;
        } else {
            System.out.println("Unknown plot variable: " + id);
        }
    }

    /**
     * Return the YODEL calculator
     * @return YODEL calculator
     */
    public Yodel getYodel() {
        return m_yodel;
    }

    /**
     * Return the maximum attractive force
     * @return Maximum attractive force
     */
    /*public double maxAttractiveForce() {
        double saveSeparation = m_distance;

        //grid search
        double maxForceSeparation = -1;
        double maxForce = 1E20;
        for (double d = 0; d <= 100; d += 1) {
            m_distance = d * 1E-9;
            double force = getInteractionForce();
            if (force < maxForce) {
                maxForce = force;
                maxForceSeparation = d;
            }
        }
        //System.out.println("Grid     : " + String.valueOf(maxForce) + " @ " + String.valueOf(maxForceSeparation));

        //bisection search
        double left = maxForceSeparation - 1;
        double right = maxForceSeparation + 1;

        for (int iteration = 0; iteration < 100; iteration++) {
            m_distance = left * 1E-9;
            double force_left = getInteractionForce();
            m_distance = right * 1E-9;
            double force_right = getInteractionForce();
            double middle = (left + right) / 2.0;

            if (force_left < force_right) { //left wins
                right = middle;
            } else { //right wins
                left = middle;
            }
        }

        maxForceSeparation = (left + right) / 2.0;
        m_distance = maxForceSeparation * 1E-9;
        maxForce = getInteractionForce();
        System.out.println("Bisection: " + String.valueOf(maxForce) + " @ " + String.valueOf(maxForceSeparation));

        m_distance = saveSeparation;

        return -maxForce;
    }*/
    
    /**
     * Return the maximum attractive force
     * @return Maximum attractive force
     */
    public double maxAttractiveSeparation() {
        double saveSeparation = m_distance;

        //grid search
        double maxForceSeparation = -1;
        double maxForce = 1E20;
        for (double d = 0; d <= 100; d += 1) {
            m_distance = d * 1E-9;
            double force = getInteractionForce();
            if (force < maxForce) {
                maxForce = force;
                maxForceSeparation = d;
            }
        }
        //System.out.println("Grid     : " + String.valueOf(maxForce) + " @ " + String.valueOf(maxForceSeparation));

        //bisection search
        double left = maxForceSeparation - 1;
        double right = maxForceSeparation + 1;

        for (int iteration = 0; iteration < 100; iteration++) {
            m_distance = left * 1E-9;
            double force_left = getInteractionForce();
            m_distance = right * 1E-9;
            double force_right = getInteractionForce();
            double middle = (left + right) / 2.0;

            if (force_left < force_right) { //left wins
                right = middle;
            } else { //right wins
                left = middle;
            }
        }

        maxForceSeparation = (left + right) / 2.0;
        //System.out.println("Bisection: " + String.valueOf(maxForce) + " @ " + String.valueOf(maxForceSeparation));

        m_distance = saveSeparation;

        return maxForceSeparation * 1E-9;
    }
}
