/*
 * Medium.java
 *
 * Created on April 4, 2007, 4:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2;

import hamaker2.models.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class to store the electrolyte composition and other properties of the
 * dispersion medium
 *
 * @author uli
 */
public class Medium extends PlotVariableProvider {

    private boolean m_unsavedChanges;

    private double m_density;
    private double m_dielectricConstant;
    private ArrayList<electrolyteComponent> m_electrolyte;

    /**
     * Creates a new instance of Medium initializing it as a solution of 0.01 M
     * Cl ions
     */
    public Medium() {
        m_unsavedChanges = false;

        m_density = 1000;                       //kg/m3    <-> g/cm3
        m_dielectricConstant = 78.54;           //no unit
        m_electrolyte = new ArrayList<>();
        m_electrolyte.add(new electrolyteComponent("Cl-", -1.0, 0.01));
    }

    /**
     * Deep copy medium object
     *
     * @param medium Object to be deep copied
     * @return The deep copy
     */
    public static Medium newInstance(Medium medium) {
        Medium copy = new Medium();

        copy.setDensity(medium.getDensity());
        copy.setDielectricConstant(medium.getDielectricConstant());
        copy.deleteAllElectrolyteComponents();
        for (int i = 0; i < medium.getNumElectrolyteComponents(); i++) {
            copy.addElectrolyteComponent(medium.getElectrolyteComponentLabel(i), medium.getElectrolyteComponentValence(i), medium.getElectrolyteComponentConcentration(i));
        }

        return copy;
    }

    /**
     * Return the density of the dispersion medium
     *
     * @return Density of the dispersion medium
     */
    public double getDensity() {
        return m_density;
    }

    /**
     * Set the density of the dispersion medium
     *
     * @param dens Density to be set
     */
    public void setDensity(double dens) {
        m_density = dens;
    }

    /**
     * Return density of the dispersion medium as a human readable string in
     * units of g/cm3
     *
     * @return String with density in g/cm3
     */
    public String getDensityAsText() {
        DecimalFormat format = new DecimalFormat("0.000");
        return format.format(m_density / 1000);
    }

    /**
     * Set density of the dispersion medium from a human readable string in
     * units of g/cm3
     *
     * @param dens The density string (g/cm3)
     */
    public void setDensityFromText(String dens) {
        m_density = Utils.StringToDouble(dens) * 1000;
    }

    /**
     * Return the dielectric constant of the medium
     *
     * @return Dielectric constant
     */
    public double getDielectricConstant() {
        return m_dielectricConstant;
    }

    /**
     * Set the dielectric constant of the medium
     *
     * @param eps Dielectric constant
     */
    public void setDielectricConstant(double eps) {
        m_dielectricConstant = eps;
    }

    /**
     * Return the dielectric constant of the medium as a human readable string
     *
     * @return Dielectric constant string
     */
    public String getDielectricConstantAsText() {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(m_dielectricConstant);
    }

    /**
     * Set dielectric constant of the dispersion medium from a human readable
     * string
     *
     * @param eps Dielectric constant string
     */
    public void setDielectricConstantFromText(String eps) {
        m_dielectricConstant = Utils.StringToDouble(eps);
    }

    /**
     * Return the number of ionic species in the electrolyte
     *
     * @return number of ionic species
     */
    public int getNumElectrolyteComponents() {
        return m_electrolyte.size();
    }

    /**
     * Return the label of the ith electrolyte species
     *
     * @param i Index of the species
     * @return Label of the species
     */
    public String getElectrolyteComponentLabel(int i) {
        return m_electrolyte.get(i).m_label;
    }

    /**
     * Return the charge of the ith electrolyte species
     *
     * @param i Index of the species
     * @return Charge of the species
     */
    public double getElectrolyteComponentValence(int i) {
        return m_electrolyte.get(i).m_valence;
    }

    /**
     * Return the concentration of the ith electrolyte species
     *
     * @param i Index of the species
     * @return Concentration of the species
     */
    public double getElectrolyteComponentConcentration(int i) {
        return m_electrolyte.get(i).m_concentration;
    }

    /**
     * Set the label of the ith electrolyte species
     *
     * @param i Index of the species
     * @param label Label of the species
     */
    public void setElectrolyteComponentLabel(int i, String label) {
        m_electrolyte.get(i).m_label = label;
    }

    /**
     * Set the charge of the ith electrolyte species
     *
     * @param i Index of the species
     * @param valence Charge of the species
     */
    public void setElectrolyteComponentValence(int i, double valence) {
        m_electrolyte.get(i).m_valence = valence;
    }

    /**
     * Set the concentration of the ith electrolyte species
     *
     * @param i Index of the species
     * @param conc Concentration of the species
     */
    public void setElectrolyteComponentConcentration(int i, double conc) {
        m_electrolyte.get(i).m_concentration = conc;
    }

    /**
     * Add a default electrolyte species 0.01 M H+
     */
    public void addElectrolyteComponent() {
        m_electrolyte.add(new electrolyteComponent());
    }

    /**
     * Add a new electrolyte species with given label, charge and concentration
     *
     * @param label New species label
     * @param valence New species charge
     * @param conc New species concentration
     */
    public void addElectrolyteComponent(String label, double valence, double conc) {
        m_electrolyte.add(new electrolyteComponent(label, valence, conc));
    }

    /**
     * Remove electrolyte species at given index
     *
     * @param i index to be deleted
     */
    public void deleteElectrolyteComponent(int i) {
        m_electrolyte.remove(i);
    }

    /**
     * Remove all electrolyte species
     */
    public void deleteAllElectrolyteComponents() {
        m_electrolyte.clear();
    }

    /**
     * Return the Debye length at a given temperature
     *
     * @param temp Temperature
     * @return Debye length
     */
    public double debye(double temp) {
        double epsilon_0 = 8.8541E-12;
        double k = 1.3807E-23;
        double e = 1.6022E-19;
        double Na = 6.022E23;

        double Ic = 0.0;
        for (int i = 0; i < getNumElectrolyteComponents(); i++) {
            double c = getElectrolyteComponentConcentration(i);
            double z = getElectrolyteComponentValence(i);
            Ic += c * z * z;
        }
        Ic *= 0.5;

        return Math.sqrt(m_dielectricConstant * epsilon_0 * k * temp / (2 * e * e * Ic * 1000 * Na));
    }

    /**
     * Return if the medium has been changed and changes need to be written to
     * disk
     *
     * @return Unsaved changes
     */
    public boolean getNeedsSave() {
        return m_unsavedChanges;
    }

    /**
     * Set the unsaved changes status
     *
     * @param s Unsaved changes
     */
    public void setNeedsSave(boolean s) {
        m_unsavedChanges = s;
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
        vars.add(new PlotVariable(idPrefix, namePrefix, "density", "Density", "g/cm<sup>3</sup>", 100, 2000, 1000, "0.000"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "dielect", "Dielectric", "-", 1, 100, 1, "0.0"));

        Iterator<electrolyteComponent> i = m_electrolyte.iterator();
        while (i.hasNext()) {

            electrolyteComponent e = i.next();

            vars.add(new PlotVariable(idPrefix, namePrefix, "vale_" + e.m_label, "Valence " + e.m_label, "e", -4.0, 4.0, 1.0, "0.0"));
            vars.add(new PlotVariable(idPrefix, namePrefix, "conc_" + e.m_label, "Concentration " + e.m_label, "M", 1.0E-3, 1.0E-1, 1.0, "0.000"));
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
        if (id.equals("density")) {
            return m_density;
        } else if (id.equals("dielect")) {
            return m_dielectricConstant;
        } else if (id.startsWith("vale_")) {
            String label = id.replaceFirst("vale_", "");
            return getElectrolyteComponentWithLabel(label).m_valence;
        } else if (id.startsWith("conc_")) {
            String label = id.replaceFirst("conc_", "");
            return getElectrolyteComponentWithLabel(label).m_concentration;
        } else {
            System.out.println("Medium, unknown plot variable: " + id);
            return PlotVariable.kUnknownPlotVariableID;
        }
    }

    /**
     * Set the value of a given plot variable, prefix has been removed
     *
     * @param id id of the plot variable
     * @param value New value of the plot variable
     */
    @Override
    public void setPlotVariableValue(String id, double value) {

        if (id.equals("density")) {
            m_density = value;
        } else if (id.equals("dielect")) {
            m_dielectricConstant = value;
        } else if (id.startsWith("vale_")) {
            String label = id.replaceFirst("vale_", "");
            getElectrolyteComponentWithLabel(label).m_valence = value;
        } else if (id.startsWith("conc_")) {
            String label = id.replaceFirst("conc_", "");
            getElectrolyteComponentWithLabel(label).m_concentration = value;
        } else {
            System.out.println("Medium, unknown plot variable: " + id);
        }
    }

    /**
     * Return a given electrolyte species identified by its label
     *
     * @param label label of the plot variable
     * @return Electrolyte species if found, null otherwise
     */
    private electrolyteComponent getElectrolyteComponentWithLabel(String label) {
        Iterator<electrolyteComponent> i = m_electrolyte.iterator();
        while (i.hasNext()) {
            electrolyteComponent e = i.next();
            if (e.m_label.equals(label)) {
                return e;
            }
        }

        System.out.println("Failed to find electrolyte component with label: " + label);

        return null;
    }

    /*
     * Private class to store the electrolate species
     */
    private class electrolyteComponent {

        public String m_label;
        public double m_valence;
        public double m_concentration;

        public electrolyteComponent() {
            m_label = "H+";
            m_valence = 1.0;
            m_concentration = 0.01;
        }

        public electrolyteComponent(String label, double valence, double conc) {
            m_label = label;
            m_valence = valence;
            m_concentration = conc;
        }
    }
}
