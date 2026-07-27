/*
 * Steric_Bergstrom.java
 *
 * Created on March 24, 2007, 9:22 PM
 *
 * To change this template, choose Tools | Template Manager
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
 * This class implements the close to hard wall steric model as defined in L.
 * Bergstrom, Journal of the American Ceramic Society, 75(12), 3305-3314, 1992
 *
 * @author uli
 */
public class Steric_Bergstrom extends AbstractInteractionModel implements StericInteractionModel {

    private boolean m_unsavedChanges;
    private double m_molecularVolume;
    private double m_volumeFraction;
    private double m_interactionParam;
    private double m_thickness;
    final private String m_version = "1.0";

    /**
     * Creates a new instance of Steric_Bergstrom
     */
    public Steric_Bergstrom() {

        m_unsavedChanges = false;

        m_molecularVolume = 3.01E-29;
        m_volumeFraction = 0.5;
        m_interactionParam = 0.35;
        m_thickness = 0.5E-9;
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "steric_bergstrom";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Bergstrom";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "L. Bergstrom, Journal of the American Ceramic Society, 75(12), 3305-3314, 1992";
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
        output.write(String.valueOf(m_molecularVolume));
        output.newLine();
        output.write(String.valueOf(m_volumeFraction));
        output.newLine();
        output.write(String.valueOf(m_interactionParam));
        output.newLine();
        output.write(String.valueOf(m_thickness));
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

        //check if there is a version tag in the file
        String version = "";
        input.mark(1024);
        Scanner s = new Scanner(input.readLine());
        if (s.hasNext("Version")) {
            s.skip("Version");
            version = s.next();
        } else {
            //make sure to rewind the input if there was no version tag
            input.reset();
        }

        if (version.equals("")) {
            //use the old unversioned reader code
            m_molecularVolume = Utils.StringToDouble(input.readLine());
            m_volumeFraction = Utils.StringToDouble(input.readLine());
            m_interactionParam = Utils.StringToDouble(input.readLine());
            m_thickness = Utils.StringToDouble(input.readLine());
        } else {
            //use new versioned reader code
            if (version.equals("1.0")) {
                m_molecularVolume = Utils.StringToDouble(input.readLine());
                m_volumeFraction = Utils.StringToDouble(input.readLine());
                m_interactionParam = Utils.StringToDouble(input.readLine());
                m_thickness = Utils.StringToDouble(input.readLine());
            } else {
                //if no reader code found, inform the user/developer
                JOptionPane.showMessageDialog(null, "The effective dispersion model is of version " + m_version + ", which can't be read by Hamaker version " + HamakerInfo.version());
            }
        }
    }

    /**
     * Make a deep copy of the model
     *
     * @return Deep copy of model
     */
    @Override
    public Steric_Bergstrom duplicate() {

        Steric_Bergstrom copy = new Steric_Bergstrom();

        copy.m_unsavedChanges = true;

        copy.m_molecularVolume = m_molecularVolume;
        copy.m_volumeFraction = m_volumeFraction;
        copy.m_interactionParam = m_interactionParam;
        copy.m_thickness = m_thickness;

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

        vars.add(new PlotVariable(idPrefix, namePrefix, "vfrac", "Volume fraction", "-", 0, 1, 1, "0.00"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "thick", "Thickness", "nm", 0, 10E-9, 1E9, "0.0"));

        return vars;
    }

    @Override
    public double getPlotVariableValue(String id) {
        switch (id) {
            case "vfrac":
                return m_volumeFraction;
            case "thick":
                return m_thickness;
            default:
                System.out.println("Bergstrom, unknown plot variable: " + id);
                break;
        }

        return PlotVariable.kUnknownPlotVariableID;
    }

    @Override
    public void setPlotVariableValue(String id, double value) {
        switch (id) {
            case "vfrac":
                m_volumeFraction = value;
                break;
            case "thick":
                m_thickness = value;
                break;
            default:
                System.out.println("Bergstrom, unknown plot variable: " + id);
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
        double k = 1.3807E-23;
        double distance = serie.getDistance();

        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        if (serie.getHeterogeneous()) {

            if (a2 > a1) {
                double temp = a1;
                a1 = a2;
                a2 = temp;
            }
        }

        if (distance > 2 * m_thickness) {
            return 0;
        } else if (distance < m_thickness) {
            return 1E6;
        } else {
            double term1 = Math.PI * a1 * k * serie.getTemperature() / (m_molecularVolume * m_volumeFraction * m_volumeFraction);
            double term2 = (0.5 - m_interactionParam) * (2 * m_thickness - distance) * (2 * m_thickness - distance);

            return 2 * a2 / (a1 + a2) * term1 * term2;
        }
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1, double size2) {
        double k = 1.3807E-23;
        double distance = serie.getDistance();

        double a1 = size1 * 0.5;

        if (distance > 2 * m_thickness) {
            return 0;
        } else if (distance < m_thickness) {
            return 1E6;
        } else {
            double term1 = Math.PI * k * serie.getTemperature() / (m_molecularVolume * m_volumeFraction * m_volumeFraction);
            double term2 = (0.5 - m_interactionParam) * (2 * m_thickness - distance) * (2 * m_thickness - distance);

            return 2 * a1 * term1 * term2;
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
        double k = 1.3807E-23;
        double distance = serie.getDistance();

        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        if (serie.getHeterogeneous()) {

            if (a2 > a1) {
                double temp = a1;
                a1 = a2;
                a2 = temp;
            }
        }

        if (distance > 2 * m_thickness) {
            return 0;
        } else if (distance < m_thickness) {
            return -1E6;
        } else {
            double A1 = 2 * a2 / (a1 + a2);
            double A2 = Math.PI * a1 * k * serie.getTemperature() / (m_molecularVolume * m_volumeFraction * m_volumeFraction);
            double A3 = (0.5 - m_interactionParam);
            double A = A1 * A2 * A3;

            return -2 * A * (2 * m_thickness - distance);
        }
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {
        double k = 1.3807E-23;
        double distance = serie.getDistance();

        double a1 = size1 * 0.5;

        if (distance > 2 * m_thickness) {
            return 0;
        } else if (distance < m_thickness) {
            return -1E6;
        } else {
            double A1 = 2 * a1;
            double A2 = Math.PI * k * serie.getTemperature() / (m_molecularVolume * m_volumeFraction * m_volumeFraction);
            double A3 = (0.5 - m_interactionParam);
            double A = A1 * A2 * A3;

            return -2 * A * (2 * m_thickness - distance);
        }
    }

    private class MoreDialog extends javax.swing.JDialog {

        public MoreDialog() {
            super((javax.swing.JDialog) null, true);
            initComponents();

            getRootPane().setDefaultButton(o_okButton);
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelAction");
            getRootPane().getActionMap().put("cancelAction", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    setVisible(false);
                }
            });
            DecimalFormat format = new DecimalFormat("0.00");
            o_interParam.setText(format.format(m_interactionParam));
            o_volFrac.setText(format.format(m_volumeFraction));
            o_thickness.setText(format.format(m_thickness * 1E9));
            format.applyPattern("0.00E00");
            o_molecVol.setText(format.format(m_molecularVolume));

            setVisible(true);
        }

        private void initComponents() {
            jLabel5 = new javax.swing.JLabel();
            jLabel1 = new javax.swing.JLabel();
            jLabel2 = new javax.swing.JLabel();
            jLabel3 = new javax.swing.JLabel();
            jLabel4 = new javax.swing.JLabel();
            o_volFrac = new javax.swing.JTextField();
            o_molecVol = new javax.swing.JTextField();
            o_interParam = new javax.swing.JTextField();
            o_thickness = new javax.swing.JTextField();
            jLabel6 = new javax.swing.JLabel();
            jLabel7 = new javax.swing.JLabel();
            jLabel8 = new javax.swing.JLabel();
            o_okButton = new javax.swing.JButton();
            o_cancelButton = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setResizable(false);

            jLabel1.setText("Solvent Molecular Volume:");
            jLabel2.setText("Volume Fraction of Adsorbed Polymer in Layer:");
            jLabel3.setText("Adsorbate-Solvent Interaction Parameter:");
            jLabel4.setText("Thickness of Adsorbed Layer:");
            o_volFrac.setText("jFormattedTextField1");
            o_molecVol.setText("jFormattedTextField2");
            o_interParam.setText("jFormattedTextField3");
            o_thickness.setText("jFormattedTextField4");
            jLabel5.setText("<html>m<sup>3</sup></html>");
            jLabel6.setText("-");
            jLabel7.setText("-");
            jLabel8.setText("nm");

            o_okButton.setText("OK");
            o_okButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    o_okButtonActionPerformed(evt);
                }
            });

            o_cancelButton.setText("Cancel");
            o_cancelButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    o_cancelButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addComponent(o_cancelButton)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(o_okButton))
                                    .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel2)
                                                    .addComponent(jLabel1)
                                                    .addComponent(jLabel3)
                                                    .addComponent(jLabel4))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(o_thickness, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                                    .addComponent(o_interParam, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                                    .addComponent(o_molecVol, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                                    .addComponent(o_volFrac, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8))
                            .addContainerGap()));
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(o_molecVol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(o_volFrac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(o_interParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(o_thickness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(o_okButton)
                                    .addComponent(o_cancelButton))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

            pack();
        }

        private void o_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
            setVisible(false);
        }

        private void o_okButtonActionPerformed(java.awt.event.ActionEvent evt) {
            m_interactionParam = Utils.StringToDouble(o_interParam.getText());
            m_volumeFraction = Utils.StringToDouble(o_volFrac.getText());
            m_thickness = 1E-9 * Utils.StringToDouble(o_thickness.getText());
            m_molecularVolume = Utils.StringToDouble(o_molecVol.getText());
            setVisible(false);
            m_unsavedChanges = true;
        }
        // Variables declaration - do not modify
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JLabel jLabel6;
        private javax.swing.JLabel jLabel7;
        private javax.swing.JLabel jLabel8;
        private javax.swing.JButton o_cancelButton;
        private javax.swing.JTextField o_interParam;
        private javax.swing.JTextField o_molecVol;
        private javax.swing.JButton o_okButton;
        private javax.swing.JTextField o_thickness;
        private javax.swing.JTextField o_volFrac;
        // End of variables declaration
    }
}
