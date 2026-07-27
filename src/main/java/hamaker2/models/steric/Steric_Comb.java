/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.models.steric;

import hamaker2.*;
import hamaker2.models.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * This class implements the comb steric model as defined in R.J. Flatt et al.
 * ,Langmuir, 25(2), 845-855, 2009
 *
 * @author uli
 */
public class Steric_Comb extends AbstractInteractionModel implements StericInteractionModel {

    private boolean m_unsavedChanges;
    private int m_p, m_n;
    private double m_ap, m_an, m_xi, m_coverage;
    final private String m_version = "1.0";

    /**
     * Creates a new instance of Steric_Comb
     */
    public Steric_Comb() {

        m_unsavedChanges = false;
        m_p = 10;
        m_n = 10;
        m_ap = 0.36E-9;
        m_an = 0.25E-9;
        m_xi = 0.37;
        m_coverage = 0.8;
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "steric_comb";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Comb";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "R.J. Flatt et al. ,Langmuir, 25(2), 845-855, 2009";
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
        MoreDialog dialog = new MoreDialog();
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
        output.write(String.valueOf(m_p));
        output.newLine();
        output.write(String.valueOf(m_n));
        output.newLine();
        output.write(String.valueOf(m_ap));
        output.newLine();
        output.write(String.valueOf(m_an));
        output.newLine();
        output.write(String.valueOf(m_xi));
        output.newLine();
        output.write(String.valueOf(m_coverage));
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

        String version = "";
        Scanner s = new Scanner(input.readLine());
        if (s.hasNext("Version")) {
            s.skip("Version");
            version = s.next();
        } else {
            JOptionPane.showMessageDialog(null, "The comb steric model does not contain a version tag and  can't be read");
        }

        //use versioned reader code
        if (version.equals("1.0")) {
            m_p = Utils.StringToInt(input.readLine());
            m_n = Utils.StringToInt(input.readLine());
            m_ap = Utils.StringToDouble(input.readLine());
            m_an = Utils.StringToDouble(input.readLine());
            m_xi = Utils.StringToDouble(input.readLine());
            m_coverage = Utils.StringToDouble(input.readLine());
        } else {
            //if no reader code found, inform the user/developer
            JOptionPane.showMessageDialog(null, "The comb steric model is of version " + m_version + ", which can't be read by Hamaker version " + HamakerInfo.version());
        }
    }

    /**
     * Make a deep copy of the model
     *
     * @return Deep copy of model
     */
    @Override
    public Steric_Comb duplicate() {

        Steric_Comb copy = new Steric_Comb();

        copy.m_unsavedChanges = true;

        copy.m_p = m_p;
        copy.m_n = m_n;
        copy.m_ap = m_ap;
        copy.m_an = m_an;
        copy.m_xi = m_xi;
        copy.m_coverage = m_coverage;

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

    @Override
    public ArrayList<PlotVariable> plotVariables(String namePrefix, String idPrefix) {

        ArrayList<PlotVariable> vars = new ArrayList<>();
        vars.add(new PlotVariable(namePrefix, idPrefix, "p", "P", "-", 0, 1E6, 1, "0"));
        vars.add(new PlotVariable(namePrefix, idPrefix, "n", "N", "-", 0, 1E6, 1, "0"));
        vars.add(new PlotVariable(namePrefix, idPrefix, "ap", "<html>a<sub>p</sub></html>", "nm", 0, 200E-9, 1E9, "0.00"));
        vars.add(new PlotVariable(namePrefix, idPrefix, "an", "<html>a<sub>n</sub></html>", "nm", 0, 200E-9, 1E9, "0.00"));
        vars.add(new PlotVariable(namePrefix, idPrefix, "xi", "Flory parameter", "-", 0, 100, 1, "0.000"));
        vars.add(new PlotVariable(namePrefix, idPrefix, "cover", "Surface coverage", "-", 0, 1.0, 1, "0.00"));

        return vars;
    }

    @Override
    public double getPlotVariableValue(String id) {

        switch (id) {
            case "steric_comb_p":
                return m_p;
            case "steric_comb_n":
                return m_n;
            case "steric_comb_ap":
                return m_ap;
            case "steric_comb_an":
                return m_an;
            case "steric_comb_xi":
                return m_xi;
            case "steric_comb_cover":
                return m_coverage;
        }

        return PlotVariable.kUnknownPlotVariableID;
    }

    @Override
    public void setPlotVariableValue(String id, double value) {
        switch (id) {
            case "steric_comb_p":
                m_p = (int) value;
                break;
            case "steric_comb_n":
                m_n = (int) value;
                break;
            case "steric_comb_ap":
                m_ap = value;
                break;
            case "steric_comb_an":
                m_an = value;
                break;
            case "steric_comb_xi":
                m_xi = value;
                break;
            case "steric_comb_cover":
                m_coverage = value;
                break;
        }
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
        double r1 = size1 * 0.5;
        double r2 = size2 * 0.5;

        double T = serie.getTemperature();
        double k = serie.kB;
        double d = serie.getDistance();

        double reduced_radius = r1 * r2 / (r1 + r2);
        double p_pow = Math.pow(m_p, -29.0 / 30.0);
        double n_pow = Math.pow(m_n, -13.0 / 30.0);

        double alpha = Math.PI * Math.pow(2, -3.0 / 10.0) * Math.pow(m_ap, 5.0 / 3.0) * m_an * Math.pow((1 - 2 * m_xi) * m_ap / m_an, 2.0 / 15.0);

        double prefact = 2 * Math.PI * k * T * reduced_radius * p_pow * n_pow / alpha;

        double rac = Math.pow(2 * Math.sqrt(2) * (1 - 2 * m_xi) * m_ap / m_an, 1.0 / 5.0) * m_ap * Math.pow(m_p, 7.0 / 10.0) * Math.pow(m_n, -1.0 / 10.0);

        double term1 = -9 * Math.pow(d, 5.0 / 3.0) / 10.0;
        double term2 = 5 * d * Math.pow(rac, 2.0 / 3.0) / Math.pow(2, 1.0 / 3.0);
        double term3 = 2 * Math.pow(2, 2.0 / 3.0) * Math.pow(d, 5.0 / 3.0) * Math.pow(rac / d, 5.0 / 3.0) * Math.log(d);

        double shift_term = 2.0 / 5.0 * Math.pow(2, 2.0 / 3.0) * Math.pow(rac, 5.0 / 3.0) * (-8 + 5 * Math.log(2 * rac));

        if (d > 2 * rac) {
            return 0.0;
        } else {
            return m_coverage * prefact * (term1 + term2 - term3 + shift_term);
        }
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1, double size2) {
        double r1 = size1 * 0.5;

        double T = serie.getTemperature();
        double k = serie.kB;
        double d = serie.getDistance();

        double p_pow = Math.pow(m_p, -29.0 / 30.0);
        double n_pow = Math.pow(m_n, -13.0 / 30.0);

        double alpha = Math.PI * Math.pow(2, -3.0 / 10.0) * Math.pow(m_ap, 5.0 / 3.0) * m_an * Math.pow((1 - 2 * m_xi) * m_ap / m_an, 2.0 / 15.0);

        double prefact = 2 * Math.PI * k * T * r1 * p_pow * n_pow / alpha;

        double rac = Math.pow(2 * Math.sqrt(2) * (1 - 2 * m_xi) * m_ap / m_an, 1.0 / 5.0) * m_ap * Math.pow(m_p, 7.0 / 10.0) * Math.pow(m_n, -1.0 / 10.0);

        double term1 = -9 * Math.pow(d, 5.0 / 3.0) / 10.0;
        double term2 = 5 * d * Math.pow(rac, 2.0 / 3.0) / Math.pow(2, 1.0 / 3.0);
        double term3 = 2 * Math.pow(2, 2.0 / 3.0) * Math.pow(d, 5.0 / 3.0) * Math.pow(rac / d, 5.0 / 3.0) * Math.log(d);

        double shift_term = 2.0 / 5.0 * Math.pow(2, 2.0 / 3.0) * Math.pow(rac, 5.0 / 3.0) * (-8 + 5 * Math.log(2 * rac));

        if (d > 2 * rac) {
            return 0.0;
        } else {
            return m_coverage * prefact * (term1 + term2 - term3 + shift_term);
        }
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
        double r1 = size1 * 0.5;
        double r2 = size2 * 0.5;

        double T = serie.getTemperature();
        double k = serie.kB;
        double d = serie.getDistance();

        double reduced_radius = r1 * r2 / (r1 + r2);
        double p_pow = Math.pow(m_p, -29.0 / 30.0);
        double n_pow = Math.pow(m_n, -13.0 / 30.0);

        double alpha = Math.PI * Math.pow(2, -3.0 / 10.0) * Math.pow(m_ap, 5.0 / 3.0) * m_an * Math.pow((1 - 2 * m_xi) * m_ap / m_an, 2.0 / 15.0);

        double prefact = 2 * Math.PI * k * T * reduced_radius * p_pow * n_pow / alpha;

        double rac = Math.pow(2 * Math.sqrt(2) * (1 - 2 * m_xi) * m_ap / m_an, 1.0 / 5.0) * m_ap * Math.pow(m_p, 7.0 / 10.0) * Math.pow(m_n, -1.0 / 10.0);

        double term1 = 5.0 / Math.pow(2, 1.0 / 3.0) * Math.pow(rac, 2.0 / 3.0);
        double term2 = 0.5 * Math.pow(d, -1.0 / 3.0);
        double term3 = 3 * d + 4 * Math.pow(2, 2.0 / 3.0) * rac * Math.pow(rac / d, 2.0 / 3.0);

        if (d > 2 * rac) {
            return 0.0;
        } else {
            return m_coverage * prefact * (term1 - term2 * term3);
        }
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {
        double r1 = size1 * 0.5;

        double T = serie.getTemperature();
        double k = serie.kB;
        double d = serie.getDistance();

        double p_pow = Math.pow(m_p, -29.0 / 30.0);
        double n_pow = Math.pow(m_n, -13.0 / 30.0);

        double alpha = Math.PI * Math.pow(2, -3.0 / 10.0) * Math.pow(m_ap, 5.0 / 3.0) * m_an * Math.pow((1 - 2 * m_xi) * m_ap / m_an, 2.0 / 15.0);

        double prefact = 2 * Math.PI * k * T * r1 * p_pow * n_pow / alpha;

        double rac = Math.pow(2 * Math.sqrt(2) * (1 - 2 * m_xi) * m_ap / m_an, 1.0 / 5.0) * m_ap * Math.pow(m_p, 7.0 / 10.0) * Math.pow(m_n, -1.0 / 10.0);

        double term1 = 5.0 / Math.pow(2, 1.0 / 3.0) * Math.pow(rac, 2.0 / 3.0);
        double term2 = 0.5 * Math.pow(d, -1.0 / 3.0);
        double term3 = 3 * d + 4 * Math.pow(2, 2.0 / 3.0) * rac * Math.pow(rac / d, 2.0 / 3.0);

        if (d > 2 * rac) {
            return 0.0;
        } else {
            return m_coverage * prefact * (term1 - term2 * term3);
        }
    }

    private class MoreDialog extends javax.swing.JDialog {

        public MoreDialog() {
            super((javax.swing.JDialog) null, true);
            initComponents();

            getRootPane().setDefaultButton(o_ok);
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelAction");
            getRootPane().getActionMap().put("cancelAction", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    setVisible(false);
                }
            });

            //fill in values
            DecimalFormat format = new DecimalFormat("0");
            o_n.setText(format.format(m_n));
            o_p.setText(format.format(m_p));
            format.applyPattern("0.00");
            o_an.setText(format.format(m_an * 1E9));
            o_ap.setText(format.format(m_ap * 1E9));
            o_coverage.setText(format.format(m_coverage));
            format.applyPattern("0.000");
            o_xi.setText(format.format(m_xi));

            setVisible(true);
        }

        private void initComponents() {

            javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
            o_an = new javax.swing.JTextField();
            o_ap = new javax.swing.JTextField();
            o_n = new javax.swing.JTextField();
            o_p = new javax.swing.JTextField();
            o_xi = new javax.swing.JTextField();
            o_coverage = new javax.swing.JTextField();
            javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
            javax.swing.JLabel jLabel12 = new javax.swing.JLabel();
            o_ok = new javax.swing.JButton();
            o_cancel = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            jLabel1.setText("Number of monomers in side chain (P):");
            jLabel2.setText("Number of backbone monomers (N):");
            jLabel3.setText("<html>Size of side chain monomers (a<sub>P</sub>):</html>");
            jLabel4.setText("<html>Size of backbone monomers (a<sub>N</sub>):</html>");
            jLabel5.setText("Flory parameter (xi):");
            jLabel6.setText("Surface coverage:");

            o_an.setText("jFormattedTextField1");
            o_an.setMinimumSize(new java.awt.Dimension(200, 28));
            o_ap.setText("jFormattedTextField2");
            o_ap.setMinimumSize(new java.awt.Dimension(200, 28));
            o_n.setText("jFormattedTextField3");
            o_n.setMinimumSize(new java.awt.Dimension(200, 28));
            o_p.setText("jFormattedTextField4");
            o_p.setMinimumSize(new java.awt.Dimension(200, 28));
            o_xi.setText("jFormattedTextField5");
            o_xi.setMinimumSize(new java.awt.Dimension(200, 28));
            o_coverage.setText("jFormattedTextField6");
            o_coverage.setMinimumSize(new java.awt.Dimension(200, 28));

            jLabel7.setText("(-)");
            jLabel8.setText("(nm)");
            jLabel9.setText("(-)");
            jLabel10.setText("(-)");
            jLabel11.setText("(nm)");
            jLabel12.setText("(-)");

            o_ok.setText("OK");
            o_ok.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    o_okActionPerformed(evt);
                }
            });

            o_cancel.setText("Cancel");
            o_cancel.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    o_cancelActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                            .addContainerGap()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                                    .add(layout.createSequentialGroup()
                                                            .add(jLabel1)
                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .add(o_p, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                                            .add(jLabel2)
                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .add(o_n, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                                            .add(jLabel3)
                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                            .add(o_ap, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(jLabel9)
                                                    .add(jLabel10)
                                                    .add(jLabel11)))
                                    .add(layout.createSequentialGroup()
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(jLabel4)
                                                    .add(jLabel5)
                                                    .add(jLabel6))
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(layout.createSequentialGroup()
                                                            .add(o_xi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                            .add(jLabel12))
                                                    .add(layout.createSequentialGroup()
                                                            .add(o_an, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                            .add(jLabel8))
                                                    .add(layout.createSequentialGroup()
                                                            .add(o_coverage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                            .add(jLabel7))
                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                                            .add(o_cancel)
                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                            .add(o_ok)))))
                            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

            layout.linkSize(new java.awt.Component[]{o_an, o_ap, o_coverage, o_n, o_p, o_xi}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

            layout.setVerticalGroup(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                            .addContainerGap()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel1)
                                    .add(o_p, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel9))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel2)
                                    .add(o_n, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel10))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel3)
                                    .add(o_ap, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel11))
                            .add(12, 12, 12)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel4)
                                    .add(o_an, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel8))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel5)
                                    .add(o_xi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel12))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel6)
                                    .add(o_coverage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel7))
                            .add(18, 18, 18)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(o_ok)
                                    .add(o_cancel))
                            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

            pack();
        }

        private void o_okActionPerformed(java.awt.event.ActionEvent evt) {
            //extract values
            m_n = Utils.StringToInt(o_n.getText());
            m_p = Utils.StringToInt(o_p.getText());
            m_an = Utils.StringToDouble(o_an.getText()) * 1E-9;
            m_ap = Utils.StringToDouble(o_ap.getText()) * 1E-9;
            m_coverage = Utils.StringToDouble(o_coverage.getText());
            m_xi = Utils.StringToDouble(o_xi.getText());
            setVisible(false);
            m_unsavedChanges = true;
        }

        private void o_cancelActionPerformed(java.awt.event.ActionEvent evt) {
            setVisible(false);
        }
        // Variables declaration 
        private javax.swing.JTextField o_an;
        private javax.swing.JTextField o_ap;
        private javax.swing.JButton o_cancel;
        private javax.swing.JTextField o_coverage;
        private javax.swing.JTextField o_n;
        private javax.swing.JButton o_ok;
        private javax.swing.JTextField o_p;
        private javax.swing.JTextField o_xi;
    }
}
