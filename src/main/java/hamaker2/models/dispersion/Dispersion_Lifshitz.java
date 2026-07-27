/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.models.dispersion;

import hamaker2.HamakerInfo;
import hamaker2.Serie;
import hamaker2.Utils;
import hamaker2.models.AbstractInteractionModel;
import hamaker2.models.GeometryClass;
import hamaker2.models.Oscillator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * This model implements the Lifshitz dispersion interaction model as defined in
 * Faure B. et al., Langmuir, 27, 8659, (2011)
 *
 * @author uli
 */
public class Dispersion_Lifshitz extends AbstractInteractionModel implements DispersionInteractionModel {

    private int m_dielMatsubaraMax, m_dielSMax;
    private ArrayList<Oscillator> m_dielectric1, m_dielectric2, m_dielectricM;
    private double m_dielTemp;
    private boolean m_unsavedChanges;
    final private String m_version = "1.0";
    final private double kb = 1.3807E-23;
    final private double h = 6.625E-34 / (2 * Math.PI);

    /**
     * Creates a new instance of Dispersion_Lifshitz
     */
    public Dispersion_Lifshitz() {
        m_dielMatsubaraMax = 10;
        m_dielSMax = 4;
        m_dielectric1 = new ArrayList<>();
        m_dielectric1.add(new Oscillator(2.4, 636.62));
        m_dielectric1.add(new Oscillator(16.6, 175.07));
        m_dielectric2 = new ArrayList<>();
        m_dielectric2.add(new Oscillator(2.4, 636.62));
        m_dielectric2.add(new Oscillator(16.6, 175.07));
        m_dielectricM = new ArrayList<>();
        //add typical water values
        m_dielTemp = 300;

        m_unsavedChanges = false;
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "dispersion_lifshitz";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Lifshitz";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "Faure B. et al., Langmuir, 27, 8659, (2011)";
    }

    /**
     * Return the version information for this model
     *
     * @return version string
     */
    @Override
    public String version() {
        return m_version;
    }

    /**
     * Does this model have additional parameters
     *
     * @return additional parameters
     */
    @Override
    public boolean additionalParameters() {
        return true;
    }

    /**
     * Show the more dialog
     */
    @Override
    public void showMoreDialog() {
        Dispersion_LifshitzDialog dialog = new Dispersion_LifshitzDialog(this);
        dialog.setVisible(true);
    }

    /**
     * Return if the model has unsaved parameters
     *
     * @return unsaved parameters
     */
    @Override
    public boolean getNeedsSave() {
        return m_unsavedChanges;
    }

    /**
     * Save the model to disk
     *
     * @param output Output file
     * @throws IOException
     */
    @Override
    public void save(BufferedWriter output) throws IOException {

        output.write("Version " + m_version);
        output.newLine();

        output.write(String.valueOf(m_dielMatsubaraMax));
        output.newLine();
        output.write(String.valueOf(m_dielSMax));
        output.newLine();
        output.write(String.valueOf(m_dielectric1.size()));
        output.newLine();
        for (Oscillator m_dielectric11 : m_dielectric1) {
            output.write(String.valueOf(m_dielectric11.getStrength()) + " " + String.valueOf(m_dielectric11.getFrequencyTHz()));
            output.newLine();
        }
        output.write(String.valueOf(m_dielectric2.size()));
        output.newLine();
        for (Oscillator m_dielectric21 : m_dielectric2) {
            output.write(String.valueOf(m_dielectric21.getStrength()) + " " + String.valueOf(m_dielectric21.getFrequencyTHz()));
            output.newLine();
        }
        output.write(String.valueOf(m_dielectricM.size()));
        output.newLine();
        for (Oscillator m_dielectricM1 : m_dielectricM) {
            output.write(String.valueOf(m_dielectricM1.getStrength()) + " " + String.valueOf(m_dielectricM1.getFrequencyTHz()));
            output.newLine();
        }
        output.write(String.valueOf(m_dielTemp));
        output.newLine();

    }

    /**
     * Load the model from disk
     *
     * @param input Input file
     * @throws IOException
     */
    @Override
    public void load(BufferedReader input) throws IOException {

        Scanner s = new Scanner(input.readLine());

        //read version tag
        String version = "";

        if (s.hasNext("Version")) {
            s.skip("Version");
            version = s.next();
        }

        if (version.equals("1.0")) {

            m_dielMatsubaraMax = Utils.StringToInt(input.readLine());
            m_dielSMax = Utils.StringToInt(input.readLine());
            int count = Utils.StringToInt(input.readLine());
            m_dielectric1.clear();
            for (int i = 0; i < count; i++) {
                String[] elems = input.readLine().split(" ");
                m_dielectric1.add(new Oscillator(Utils.StringToDouble(elems[0]), Utils.StringToDouble(elems[1])));
            }
            count = Utils.StringToInt(input.readLine());
            m_dielectric2.clear();
            for (int i = 0; i < count; i++) {
                String[] elems = input.readLine().split(" ");
                m_dielectric2.add(new Oscillator(Utils.StringToDouble(elems[0]), Utils.StringToDouble(elems[1])));
            }
            count = Utils.StringToInt(input.readLine());
            m_dielectricM.clear();
            for (int i = 0; i < count; i++) {
                String[] elems = input.readLine().split(" ");
                m_dielectricM.add(new Oscillator(Utils.StringToDouble(elems[0]), Utils.StringToDouble(elems[1])));
            }
            m_dielTemp = Utils.StringToDouble(input.readLine());

        } else {
            //if no reader code found, inform the user/developer
            JOptionPane.showMessageDialog(null, "The Lifshitz dispersion model is of version " + m_version + ", which can't be read by Hamaker version " + HamakerInfo.version());
        }
    }

    /**
     * Make a deep copy of the model
     *
     * @return Deep copy of model
     */
    @Override
    public Dispersion_Lifshitz duplicate() {

        Dispersion_Lifshitz copy = new Dispersion_Lifshitz();

        copy.m_dielMatsubaraMax = m_dielMatsubaraMax;
        copy.m_dielSMax = m_dielSMax;
        copy.m_dielectric1.clear();
        copy.m_dielectric2.clear();
        copy.m_dielectricM.clear();
        Iterator<Oscillator> i = m_dielectric1.iterator();
        while (i.hasNext()) {
            copy.m_dielectric1.add(i.next().duplicate());
        }
        i = m_dielectric2.iterator();
        while (i.hasNext()) {
            copy.m_dielectric2.add(i.next().duplicate());
        }
        i = m_dielectricM.iterator();
        while (i.hasNext()) {
            copy.m_dielectricM.add(i.next().duplicate());
        }
        copy.m_dielTemp = m_dielTemp;

        copy.m_unsavedChanges = m_unsavedChanges;

        return copy;
    }

    /**
     * Return the list of all implemented geometry classes
     *
     * @return list of all supported geometry classes
     */
    @Override
    public ArrayList<GeometryClass> ImplementedGeometryClasses() {
        return GeometryClass.makeList(GeometryClass.Sphere_Sphere, GeometryClass.Sphere_Plate);
    }

    /*Temporary functions until dialog is copied into here!*/
    /**
     * Copy in all parameters from another instance of the model
     *
     * @param model Model to be copied in
     * @deprecated
     */
    @Deprecated
    public void valuesFromModel(Dispersion_Lifshitz model) {
        m_dielMatsubaraMax = model.m_dielMatsubaraMax;
        m_dielSMax = model.m_dielSMax;
        m_dielectric1 = model.m_dielectric1;
        m_dielectric2 = model.m_dielectric2;
        m_dielectricM = model.m_dielectricM;
        m_dielTemp = model.m_dielTemp;

        m_unsavedChanges = model.m_unsavedChanges;
    }

    /**
     * Return the relevant dielectric function
     *
     * @param particle 0: medium, 1: particle 1, 2: particle 2
     * @return ArrayList of Oscillators
     * @deprecated
     */
    @Deprecated
    public ArrayList<Oscillator> dielFunction(int particle) {
        if (particle == 0) {
            return m_dielectricM;
        } else if (particle == 1) {
            return m_dielectric1;
        } else if (particle == 2) {
            return m_dielectric2;
        } else {
            return null;
        }
    }

    /**
     * Return the maximal Matsubara frequency
     *
     * @return maximal Matsubara frequency
     * @deprecated
     */
    @Deprecated
    public int dielMatsubaraMax() {
        return m_dielMatsubaraMax;
    }

    /**
     * Set the maximal Matsubara frequency
     *
     * @param m maximal Matsubara frequency
     * @deprecated
     */
    @Deprecated
    public void setDielMatsubaraMax(int m) {
        m_dielMatsubaraMax = m;
    }

    /**
     * Return the maximal sum length
     *
     * @return maximimal sum length
     * @deprecated
     */
    @Deprecated
    public int dielSMax() {
        return m_dielSMax;
    }

    /**
     * Set the maximal sum length
     *
     * @param m maximal sum length
     * @deprecated
     */
    @Deprecated
    public void setDielSMax(int m) {
        m_dielSMax = m;
    }

    /**
     * Return the temperature
     *
     * @return temperature
     * @deprecated
     */
    @Deprecated
    public double dielTemp() {
        return m_dielTemp;
    }

    /**
     * Set the temperature
     *
     * @param t temperature
     * @deprecated
     */
    @Deprecated
    public void setDielTemp(double t) {
        m_dielTemp = t;
    }

    /**
     * Return the interaction potential for a homogeneous series
     *
     * @param serie Series to be computed
     * @param size particle size
     * @return Interaction potential in kB/T
     */
    @Override
    public double interactionPotential(Serie serie, double size) {
        return interactionPotential(serie, size, size);
    }

    /**
     * Return the interaction potential for a heterogeneous series
     *
     * @param serie Series to be computed
     * @param size1 Size of particle 1
     * @param size2 Size of particle 2
     * @return Interaction potential in kB/T
     */
    @Override
    public double interactionPotential(Serie serie, double size1, double size2) {

        if (serie.getSelectedGeometryClass() == GeometryClass.Sphere_Sphere) {
            return interactionPotential_Sphere_Sphere(serie, size1, size2);
        } else if (serie.getSelectedGeometryClass() == GeometryClass.Sphere_Plate) {
            return interactionPotential_Sphere_Plate(serie, size1, size2);
        } else {
            System.out.println("Unknown geometry class");
            return 0;
        }
    }

    private double interactionPotential_Sphere_Sphere(Serie serie, double size1, double size2) {

        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        double D = serie.getDistance();
        double AH = lifshitzHamakerConstant(serie.getTemperature());

        double term1 = 2 * a1 * a2 / (D * D + 2 * a1 * D + 2 * a2 * D);
        double term2 = 2 * a1 * a2 / (D * D + 2 * a1 * D + 2 * a2 * D + 4 * a1 * a2);
        double term3 = Math.log((D * D + 2 * a1 * D + 2 * a2 * D) / (D * D + 2 * a1 * D + 2 * a2 * D + 4 * a1 * a2));

        return -AH / 6.0 * (term1 + term2 + term3);
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1, double size2) {
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        double AH = lifshitzHamakerConstant(serie.getTemperature());

        double term1 = a1 / D;
        double term2 = a1 / (D + 2 * a1);
        double term3 = Math.log(D / (D + 2 * a1));

        return -AH / 6.0 * (term1 + term2 + term3);
    }

    /**
     * Return the interaction force for a homogeneous series
     *
     * @param serie Series to be computed
     * @param size particle size
     * @return Interaction force in kB/(T*nm)
     */
    @Override
    public double interactionForce(Serie serie, double size) {
        return interactionForce(serie, size, size);
    }

    /**
     * Return the interaction potential for a heterogeneous series
     *
     * @param serie Series to be computed
     * @param size1 Size of particle 1
     * @param size2 Size of particle 2
     * @return Interaction force in kB/(T*nm)
     */
    @Override
    public double interactionForce(Serie serie, double size1, double size2) {

        if (serie.getSelectedGeometryClass() == GeometryClass.Sphere_Sphere) {
            return interactionForce_Sphere_Sphere(serie, size1, size2);
        } else if (serie.getSelectedGeometryClass() == GeometryClass.Sphere_Plate) {
            return interactionForce_Sphere_Plate(serie, size1, size2);
        } else {
            System.out.println("Unknown geometry class");
            return 0;
        }
    }

    private double interactionForce_Sphere_Sphere(Serie serie, double size1, double size2) {
        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        double D = serie.getDistance();
        double AH = lifshitzHamakerConstant(serie.getTemperature());

        //i'll use the center-center formular since it's so much easier so
        //convert to center-center formulation
        D += a1 + a2;

        //calculate some reoccuring terms
        double dsq_minus_a1pa2sq = D * D - (a1 + a2) * (a1 + a2);
        double dsq_minus_a1ma2sq = D * D - (a1 - a2) * (a1 - a2);

        double term1 = 2 * D / dsq_minus_a1pa2sq;
        double term2 = 2 * D / dsq_minus_a1ma2sq;
        double term3 = 4 * a1 * a2 * D / (dsq_minus_a1pa2sq * dsq_minus_a1pa2sq);
        double term4 = 4 * a1 * a2 * D / (dsq_minus_a1pa2sq * dsq_minus_a1ma2sq);

        return -AH / 6.0 * (term1 - term2 - term3 - term4);
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        double AH = lifshitzHamakerConstant(serie.getTemperature());

        double term1 = 1.0 / D;
        double term2 = 1.0 / (D + 2 * a1);
        double term3 = a1 / (D * D);
        double term4 = a1 / (D * D + 4 * D * a1 + 4 * a1 * a1);

        return -AH / 6.0 * (term1 - term2 - term3 - term4);
    }

    /**
     * Return Lifshitz model Hamaker constant
     *
     * @param temperature Temperature
     * @return Hamaker constant
     * @deprecated
     */
    @Deprecated
    public double lifshitzHamakerConstant(double temperature) {
        double dielHam = 0;

        for (int m = 0; m < m_dielMatsubaraMax; m++) {
            double factor = 1.0;
            if (m == 0) {
                factor = 0.5;
            }

            double freq = 4 * Math.PI * Math.PI * kb * temperature * m / h;

            double eps1 = 1;
            double eps2 = 1;
            double eps3 = 1;
            Iterator<Oscillator> i = m_dielectric1.iterator();
            while (i.hasNext()) {
                Oscillator o = i.next();
                eps1 += o.getStrength() / (1 + Math.pow(freq / o.getFrequency_10_15_RadS(), 2));
            }

            i = m_dielectric2.iterator();
            while (i.hasNext()) {
                Oscillator o = i.next();
                eps2 += o.getStrength() / (1 + Math.pow(freq / o.getFrequency_10_15_RadS(), 2));
            }

            i = m_dielectricM.iterator();
            while (i.hasNext()) {
                Oscillator o = i.next();
                eps3 += o.getStrength() / (1 + Math.pow(freq / o.getFrequency_10_15_RadS(), 2));
            }

            double delta13 = (eps1 - eps3) / (eps1 + eps3);
            double delta23 = (eps2 - eps3) / (eps2 + eps3);

            for (int s = 1; s < m_dielSMax; s++) {
                dielHam += factor * Math.pow(delta13 * delta23, s) / Math.pow(s, 3.0);
            }

        }

        dielHam *= 3 * kb * temperature * 0.5;

        return dielHam;
    }
}
