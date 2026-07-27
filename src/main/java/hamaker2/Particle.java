/*
 * Particle.java
 *
 * Created on April 4, 2007, 4:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2;

import hamaker2.models.PlotVariable;
import hamaker2.models.PlotVariableProvider;
import hamaker2.particleSizeDistribution.AbstractParticleSizeDistribution;
import hamaker2.particleSizeDistribution.FromFile;
import hamaker2.particleSizeDistribution.LogNormal;
import hamaker2.particleSizeDistribution.Monodisperse;
import hamaker2.particleSizeDistribution.ParticleSizeDistribution;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author uli
 */
public class Particle extends PlotVariableProvider {

    private boolean m_unsavedChanges;
    private double m_hamakerConstant;
    private double m_density;
    private final ArrayList<ParticleSizeDistribution> m_distributions;
    private final ArrayList<ParticleSizeDistribution> m_saveSizeDistributions;
    private ParticleSizeDistribution m_selectedParticleSizeDistribution;
    private ParticleSizeDistribution m_saveSelectedParticleSizeDistribution;
    private double m_zetaPotential;
    private double m_electrostaticOrigin;

    /**
     * Creates a new instance of Particle with default parameters
     */
    public Particle() {
        m_unsavedChanges = false;
        m_hamakerConstant = 1.6E-19;        //1.6E-19 J    <-> J
        m_density = 3997.0;                 //3.997 g/cm3  <-> kg/m3
        m_distributions = particleSizeDistributions();
        m_saveSizeDistributions = particleSizeDistributions();
        m_selectedParticleSizeDistribution = getParticleSizeDistributionWithID("monodisperse");
        m_zetaPotential = 0.050;            //0.5 mV       <-> V
        m_electrostaticOrigin = 0.5E-9;     //0.5 nm       <-> m
    }

    /**
     * Deep copy a given particle
     *
     * @param particle Particle to be copied
     * @return Deep copy of the particle
     */
    public static Particle newInstance(Particle particle) {
        Particle copy = new Particle();
        copy.setHamakerConstant(particle.getHamakerConstant());
        copy.setDensity(particle.getDensity());
        copy.m_distributions.clear();
        Iterator<ParticleSizeDistribution> i = particle.m_distributions.iterator();
        while (i.hasNext()) {
            copy.m_distributions.add(i.next().copy());
        }
        copy.setSelectedSizeDistributionID(particle.getSelectedSizeDistributionID());
        copy.setZetaPotential(particle.getZetaPotential());
        copy.setElectrostaticOrigin(particle.getElectrostaticOrigin());

        return copy;
    }

    /**
     * Return the hamaker constant of the particle
     *
     * @return Hamaker constant
     */
    public double getHamakerConstant() {
        return m_hamakerConstant;
    }

    /**
     * Set the hamaker constant of the particle
     *
     * @param constant Hamaker constant
     */
    public void setHamakerConstant(double constant) {
        m_hamakerConstant = constant;
    }

    /**
     * Return the hamaker constant of the particle in a human readable string
     *
     * @return Human readable string
     */
    public String getHamakerConstantAsText() {
        DecimalFormat format = new DecimalFormat("0.00E00");
        return format.format(m_hamakerConstant);
    }

    /**
     * Set the hamaker constant of the particle from a human readable string
     *
     * @param constant Human readable string
     */
    public void setHamakerConstantFromText(String constant) {
        m_hamakerConstant = Utils.StringToDouble(constant);
    }

    /**
     * Return the density of the particle
     *
     * @return Density
     */
    public double getDensity() {
        return m_density;
    }

    /**
     * Set the density of the particle
     *
     * @param density Density
     */
    public void setDensity(double density) {
        m_density = density;
    }

    /**
     * Return the density of the particle in a human readable string in units of
     * g/cm3
     *
     * @return String of density in g/cm3
     */
    public String getDensityAsText() {
        DecimalFormat format = new DecimalFormat("0.000");
        return format.format(m_density / 1000);
    }

    /**
     * Set the density of the particle from a human readable string in units of
     * g/cm3
     *
     * @param density String of density in g/cm3
     */
    public void setDensityFromText(String density) {
        m_density = Utils.StringToDouble(density) * 1000;
    }

    /**
     * Select a particle size distribution by its ID
     *
     * @param id ID of the particle size distribution
     */
    public void setSelectedSizeDistributionID(String id) {
        Iterator<ParticleSizeDistribution> i = m_distributions.iterator();
        while (i.hasNext()) {
            ParticleSizeDistribution p = i.next();
            if (p.id().equals(id)) {
                m_selectedParticleSizeDistribution = p;
            }
        }
    }

    /**
     * Return the selected particle size distribution
     *
     * @return Selected particle size distribution
     */
    public ParticleSizeDistribution getSelectedSizeDistribution() {
        return m_selectedParticleSizeDistribution;
    }

    /**
     * Set the selected particle size distribution
     *
     * @param distribution Selected particle size distribution
     */
    public void setSelectedSizeDistribution(ParticleSizeDistribution distribution) {
        m_selectedParticleSizeDistribution = distribution;
    }

    /**
     * Return the ID of the selected particle size distribution
     *
     * @return Selected particle size distribution ID
     */
    public String getSelectedSizeDistributionID() {
        return m_selectedParticleSizeDistribution.id();
    }

    /**
     * Return the zeta potential
     *
     * @return zeta potential
     */
    public double getZetaPotential() {
        return m_zetaPotential;
    }

    /**
     * Set the zeta potential
     *
     * @param zeta zeta potential
     */
    public void setZetaPotential(double zeta) {
        m_zetaPotential = zeta;
    }

    /**
     * Return the zeta potential as human readable string in mV
     *
     * @return String of zeta potential in mV
     */
    public String getZetaPotentialAsText() {
        DecimalFormat format = new DecimalFormat("0.0");
        return format.format(m_zetaPotential * 1000);
    }

    /**
     * Set the zeta potential from human readable string in mV
     *
     * @param zeta String of zeta potential in mV
     */
    public void setZetaPotentialFromText(String zeta) {
        m_zetaPotential = Utils.StringToDouble(zeta) / 1000;
    }

    /**
     * Return the origin of the electrostatic plane
     *
     * @return Origin of electrostatic plane
     */
    public double getElectrostaticOrigin() {
        return m_electrostaticOrigin;
    }

    /**
     * Set the origin of the electrostatic plane
     *
     * @param origin Origin of electrostatic plane
     */
    public void setElectrostaticOrigin(double origin) {
        m_electrostaticOrigin = origin;
    }

    /**
     * Return the origin of the electrostatic plane as human readable string in
     * nm
     *
     * @return String of the electrostatic origin in nm
     */
    public String getElectrostaticOriginAsText() {
        DecimalFormat format = new DecimalFormat("0.0");
        return format.format(m_electrostaticOrigin * 1E9);
    }

    /**
     * Set the origin of the electrostatic plane from human readable string in
     * nm
     *
     * @param origin String of the electrostatic origin in nm
     */
    public void setElectrostaticOriginFromText(String origin) {
        m_electrostaticOrigin = Utils.StringToDouble(origin) * 1E-9;
    }

    /**
     * Does the particle need saving to disk
     *
     * @return Unsaved changes
     */
    public boolean getNeedsSave() {
        return m_unsavedChanges;
    }

    /**
     * Set if the particle need saving to disk
     *
     * @param s Unsaved changes
     */
    public void setNeedsSave(boolean s) {
        m_unsavedChanges = s;
    }

    /**
     * Static method to return a list of all particle size distributions
     *
     * @return List of all implemented particle size distribution models
     */
    public static ArrayList<ParticleSizeDistribution> particleSizeDistributions() {
        ArrayList<ParticleSizeDistribution> sizeDistributions = new ArrayList<>();

        sizeDistributions.add(new Monodisperse());
        sizeDistributions.add(new LogNormal());
        sizeDistributions.add(new FromFile());

        return sizeDistributions;
    }

    /**
     * Return the number of particle size distributions that set additional
     * parameters
     *
     * @return number of particle size distributions that set additional
     * parameters
     */
    public int getAdditionalParametersParticleSizeDistributionCount() {
        int count = 0;
        for (ParticleSizeDistribution m_distribution : m_distributions) {
            if (m_distribution.hasParameters()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Return the number of particle size distributions
     *
     * @return umber of particle size distributions
     */
    public int getParticleSizeDistributionCount() {
        return m_distributions.size();
    }

    /**
     * Get the ith particle size distribution
     *
     * @param i Index of particle size distribution
     * @return Particle size distribution
     */
    public ParticleSizeDistribution getParticleSizeDistribution(int i) {
        return m_distributions.get(i);
    }

    /**
     * Get the particle size distribution with a given ID
     *
     * @param id ID of the particle size distribution
     * @return Particle size distribution if found, null otherwise
     */
    private ParticleSizeDistribution getParticleSizeDistributionWithID(String id) {
        Iterator<ParticleSizeDistribution> i = m_distributions.iterator();
        while (i.hasNext()) {
            ParticleSizeDistribution d = i.next();
            if (d.id().equals(id)) {
                return d;
            }
        }

        return null;
    }

    /**
     * Get a list of all particle size distributions of the particle
     *
     * @return List of all particle size distributions
     */
    public ArrayList<ParticleSizeDistribution> getParticleSizeDistributions() {
        return m_distributions;
    }

    /**
     * Store particle size distributions into backup variable (i.e. prior to
     * making reversible modifications)
     */
    public void backupParticleSizeDistributions() {
        m_saveSizeDistributions.clear();
        for (ParticleSizeDistribution m_distribution : m_distributions) {
            m_saveSizeDistributions.add(m_distribution.copy());
        }
        m_saveSelectedParticleSizeDistribution = m_selectedParticleSizeDistribution;
    }

    /**
     * Restore particle size distributions from backup variable (i.e. to revert
     * a modification)
     */
    public void restoreParticleSizeDistributions() {
        m_distributions.clear();
        for (ParticleSizeDistribution m_saveSizeDistribution : m_saveSizeDistributions) {
            m_distributions.add(m_saveSizeDistribution.copy());
        }
        m_selectedParticleSizeDistribution = m_saveSelectedParticleSizeDistribution;
    }

    /**
     * Return a list of plot variables
     *
     * @param namePrefix prefix to be applied to plot variable names (human
     * readable)
     * @param idPrefix prefix to be applied to plot variables ids (machine
     * readable)
     * @return list of plot variables
     */
    @Override
    public ArrayList<PlotVariable> plotVariables(String namePrefix, String idPrefix) {
        ArrayList<PlotVariable> vars = new ArrayList<>();

        vars.add(new PlotVariable(idPrefix, namePrefix, "hamaker", "Hamaker Constant", "J", 1.0E-19, 2.0E-19, 1.0E0, "0.0E0"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "density", "Density", "g/cm3", 1.0E-3, 4.0E-3, 1.0E3, "0.0"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "zeta", "Zeta Potential", "mV", -5.0E-2, 5.0E-2, 1.0E3, "0.0"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "origin", "Electrostatic Origin", "nm", 0.0E-9, 1.0E-9, 1.0E9, "0.0"));

        ArrayList<ParticleSizeDistribution> psd = Particle.particleSizeDistributions();
        Iterator<ParticleSizeDistribution> i = psd.iterator();
        while (i.hasNext()) {
            AbstractParticleSizeDistribution p = (AbstractParticleSizeDistribution) i.next();
            vars.addAll(p.plotVariables(namePrefix + "Size - " + p.name() + " - ", idPrefix + "psd_" + p.id() + "_"));
        }

        return vars;
    }

    /**
     * Return the value of a given plot variable, prefix has been removed
     *
     * @param id id of the plot variable
     * @return Current value of the plot variable
     */
    @Override
    public double getPlotVariableValue(String id) {

        if (id.equals("hamaker")) {
            return m_hamakerConstant;
        } else if (id.equals("density")) {
            return m_density;
        } else if (id.equals("zeta")) {
            return m_zetaPotential;
        } else if (id.equals("origin")) {
            return m_electrostaticOrigin;
        } else if (id.startsWith("psd_")) {
            String distribution_id = id.split("_")[1];
            id = id.replaceFirst("psd_" + distribution_id + "_", "");
            AbstractParticleSizeDistribution p = (AbstractParticleSizeDistribution) getParticleSizeDistributionWithID(distribution_id);
            if (p != null) {
                return p.getPlotVariableValue(id);
            } else {
                System.out.println("Failed to find particle size distribution with ID: " + distribution_id);
            }

        } else {
            System.out.println("Unknown plot variable: " + id);
        }

        return 0.0;
    }

    /**
     * Set the value of a given plot variable, prefix has been removed
     *
     * @param id id of the plot variable
     * @param value New value of the plot variable
     */
    @Override
    public void setPlotVariableValue(String id, double value) {
        if (id.equals("hamaker")) {
            m_hamakerConstant = value;
        } else if (id.equals("density")) {
            m_density = value;
        } else if (id.equals("zeta")) {
            m_zetaPotential = value;
        } else if (id.equals("origin")) {
            m_electrostaticOrigin = value;
        } else if (id.startsWith("psd_")) {
            String distribution_id = id.split("_")[1];
            id = id.replaceFirst("psd_" + distribution_id + "_", "");
            AbstractParticleSizeDistribution p = (AbstractParticleSizeDistribution) getParticleSizeDistributionWithID(distribution_id);
            if (p != null) {
                p.setPlotVariableValue(id, value);
            } else {
                System.out.println("Failed to find particle size distribution with ID: " + distribution_id);
            }
        } else {
            System.out.println("Unknown plot variable: " + id);
        }
    }
}
