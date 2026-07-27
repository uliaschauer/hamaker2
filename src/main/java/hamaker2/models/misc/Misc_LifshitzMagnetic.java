/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.models.misc;

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
public class Misc_LifshitzMagnetic extends AbstractInteractionModel implements MiscInteractionModel {

    private int m_magMatsubaraMax, m_magSMax;
    private ArrayList<Oscillator> m_magnetic1, m_magnetic2, m_magneticM;
    private double m_magTemp;
    private boolean m_unsavedChanges;
    final private String m_version = "1.0";
    final private double kb = 1.3807E-23;
    final private double h = 6.625E-34 / (2 * Math.PI);
    private boolean m_active = false;

    /**
     * Creates a new instance of Misc_LifshitzMagnetic
     */
    public Misc_LifshitzMagnetic() {
        m_magMatsubaraMax = 10;
        m_magSMax = 4;
        m_magnetic1 = new ArrayList<>();
        m_magnetic2 = new ArrayList<>();
        m_magneticM = new ArrayList<>();
        m_magTemp = 300;

        m_unsavedChanges = false;
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "misc_lifshitzMagnetic";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Lifshitz Magnetic";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "Faure B., Langmuir, 27, 8659, (2011)";
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
        Misc_LifshitzMagneticDialog dialog = new Misc_LifshitzMagneticDialog(this);
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

        output.write(String.valueOf(m_magMatsubaraMax));
        output.newLine();
        output.write(String.valueOf(m_magSMax));
        output.newLine();
        output.write(String.valueOf(m_magnetic1.size()));
        output.newLine();
        for (Oscillator m_magnetic11 : m_magnetic1) {
            output.write(String.valueOf(m_magnetic11.getStrength()) + " " + String.valueOf(m_magnetic11.getFrequencyTHz()));
            output.newLine();
        }
        output.write(String.valueOf(m_magnetic2.size()));
        output.newLine();
        for (Oscillator m_magnetic21 : m_magnetic2) {
            output.write(String.valueOf(m_magnetic21.getStrength()) + " " + String.valueOf(m_magnetic21.getFrequencyTHz()));
            output.newLine();
        }
        output.write(String.valueOf(m_magneticM.size()));
        output.newLine();
        for (Oscillator m_magneticM1 : m_magneticM) {
            output.write(String.valueOf(m_magneticM1.getStrength()) + " " + String.valueOf(m_magneticM1.getFrequencyTHz()));
            output.newLine();
        }
        output.write(String.valueOf(m_magTemp));
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

            m_magMatsubaraMax = Utils.StringToInt(input.readLine());
            m_magSMax = Utils.StringToInt(input.readLine());
            int count = Utils.StringToInt(input.readLine());
            m_magnetic1.clear();
            for (int i = 0; i < count; i++) {
                String[] elems = input.readLine().split(" ");
                m_magnetic1.add(new Oscillator(Utils.StringToDouble(elems[0]), Utils.StringToDouble(elems[1])));
            }
            count = Utils.StringToInt(input.readLine());
            m_magnetic2.clear();
            for (int i = 0; i < count; i++) {
                String[] elems = input.readLine().split(" ");
                m_magnetic2.add(new Oscillator(Utils.StringToDouble(elems[0]), Utils.StringToDouble(elems[1])));
            }
            count = Utils.StringToInt(input.readLine());
            m_magneticM.clear();
            for (int i = 0; i < count; i++) {
                String[] elems = input.readLine().split(" ");
                m_magneticM.add(new Oscillator(Utils.StringToDouble(elems[0]), Utils.StringToDouble(elems[1])));
            }
            m_magTemp = Utils.StringToDouble(input.readLine());

        } else {
            //if no reader code found, inform the user/developer
            JOptionPane.showMessageDialog(null, "The Lifshitz magnetic model is of version " + m_version + ", which can't be read by Hamaker version " + HamakerInfo.version());
        }
    }

    /**
     * Make a deep copy of the model
     *
     * @return Deep copy of model
     */
    @Override
    public Misc_LifshitzMagnetic duplicate() {

        Misc_LifshitzMagnetic copy = new Misc_LifshitzMagnetic();

        copy.m_magMatsubaraMax = m_magMatsubaraMax;
        copy.m_magSMax = m_magSMax;
        copy.m_magnetic1.clear();
        copy.m_magnetic2.clear();
        copy.m_magneticM.clear();
        Iterator<Oscillator> i = m_magnetic1.iterator();
        while (i.hasNext()) {
            copy.m_magnetic1.add(i.next().duplicate());
        }
        i = m_magnetic2.iterator();
        while (i.hasNext()) {
            copy.m_magnetic2.add(i.next().duplicate());
        }
        i = m_magneticM.iterator();
        while (i.hasNext()) {
            copy.m_magneticM.add(i.next().duplicate());
        }
        copy.m_magTemp = m_magTemp;

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
    public void valuesFromModel(Misc_LifshitzMagnetic model) {
        m_magMatsubaraMax = model.m_magMatsubaraMax;
        m_magSMax = model.m_magSMax;
        m_magnetic1 = model.m_magnetic1;
        m_magnetic2 = model.m_magnetic2;
        m_magneticM = model.m_magneticM;
        m_magTemp = model.m_magTemp;

        m_unsavedChanges = model.m_unsavedChanges;
    }

    /**
     * Return the relevant magnetic function
     *
     * @param particle 0: medium, 1: particle 1, 2: particle 2
     * @return ArrayList of Oscillators
     * @deprecated
     */
    @Deprecated
    public ArrayList<Oscillator> magFunction(int particle) {
        if (particle == 0) {
            return m_magneticM;
        } else if (particle == 1) {
            return m_magnetic1;
        } else if (particle == 2) {
            return m_magnetic2;
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
    public int magMatsubaraMax() {
        return m_magMatsubaraMax;
    }

    /**
     * Set the maximal Matsubara frequency
     *
     * @param m maximal Matsubara frequency
     * @deprecated
     */
    @Deprecated
    public void setMagMatsubaraMax(int m) {
        m_magMatsubaraMax = m;
    }

    /**
     * Return the maximal sum length
     *
     * @return maximimal sum length
     * @deprecated
     */
    @Deprecated
    public int magSMax() {
        return m_magSMax;
    }

    /**
     * Set the maximal sum length
     *
     * @param m maximal sum length
     * @deprecated
     */
    @Deprecated
    public void setMagSMax(int m) {
        m_magSMax = m;
    }

    /**
     * Return the temperature
     *
     * @return temperature
     * @deprecated
     */
    @Deprecated
    public double magTemp() {
        return m_magTemp;
    }

    /**
     * Set the temperature
     *
     * @param t temperature
     * @deprecated
     */
    @Deprecated
    public void setMagTemp(double t) {
        m_magTemp = t;
    }

    /**
     * Return if the model is active
     * @return Is the model active
     */
    @Override
    public boolean getActive() {
        return m_active;
    }

    /**
     * Set if the model is active
     * @param active Is the model active
     */
    @Override
    public void setActive(boolean active) {
        m_active = active;
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

        if (!m_active) {
            return 0.0;
        }

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

        if (!m_active) {
            return 0.0;
        }

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
        double magHam = 0;

        for (int m = 0; m < m_magMatsubaraMax; m++) {
            double factor = 1.0;
            if (m == 0) {
                factor = 0.5;
            }

            double freq = 4 * Math.PI * Math.PI * kb * temperature * m / h;

            double eps1 = 1;
            double eps2 = 1;
            double eps3 = 1;
            Iterator<Oscillator> i = m_magnetic1.iterator();
            while (i.hasNext()) {
                Oscillator o = i.next();
                eps1 += o.getStrength() / (1 + Math.pow(freq / o.getFrequency_10_15_RadS(), 2));
            }

            i = m_magnetic2.iterator();
            while (i.hasNext()) {
                Oscillator o = i.next();
                eps2 += o.getStrength() / (1 + Math.pow(freq / o.getFrequency_10_15_RadS(), 2));
            }

            i = m_magneticM.iterator();
            while (i.hasNext()) {
                Oscillator o = i.next();
                eps3 += o.getStrength() / (1 + Math.pow(freq / o.getFrequency_10_15_RadS(), 2));
            }

            double delta13 = (eps1 - eps3) / (eps1 + eps3);
            double delta23 = (eps2 - eps3) / (eps2 + eps3);

            for (int s = 1; s < m_magSMax; s++) {
                magHam += factor * Math.pow(delta13 * delta23, s) / Math.pow(s, 3.0);
            }

        }

        magHam *= 3 * kb * temperature * 0.5;

        return magHam;
    }
}
