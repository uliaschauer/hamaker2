/*
 * Electrostatic_LSA.java
 *
 * Created on March 24, 2007, 9:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2.models.electrostatic;

import hamaker2.*;
import hamaker2.models.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Implements the linear standard approximation electrostatic model as defined
 * in G.M. Bell, Journal of Colloid and Interface Science, 33(3), 335-359, 1970
 *
 * @author uli
 */
public class Electrostatic_LSA extends AbstractInteractionModel implements ElectrostaticInteractionModel {

    final private String m_version = "1.0";
    private boolean m_constantPot;
    private boolean m_unsavedChanges;

    /**
     * Creates a new instance of Electrostatic_LSA
     */
    public Electrostatic_LSA() {
        m_constantPot = false;
        m_unsavedChanges = false;
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "electrostatic_LSA";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Linear Superposition Approximation";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "G.M. Bell, Journal of Colloid and Interface Science, 33(3), 335-359, 1970";
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
        if (m_constantPot) {
            output.write("1");
        } else {
            output.write("0");
        }
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
        m_constantPot = input.readLine().startsWith("1");
    }

    /**
     * Make a deep copy of the model
     *
     * @return Deep copy of model
     */
    @Override
    public Electrostatic_LSA duplicate() {

        Electrostatic_LSA copy = new Electrostatic_LSA();
        copy.m_constantPot = m_constantPot;

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
        double epsilon = serie.getMedium().getDielectricConstant();
        double epsilon_0 = 8.8541E-12;
        double k = 1.3807E-23;
        double T = serie.getTemperature();
        double e = 1.6022E-19;
        double Na = 6.022E23;

        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        double zeta1, zeta2;
        double ds1, ds2;
        if (serie.getHeterogeneous()) {
            zeta1 = serie.getParticle1().getZetaPotential();
            zeta2 = serie.getParticle2().getZetaPotential();
            ds1 = serie.getParticle1().getElectrostaticOrigin();
            ds2 = serie.getParticle2().getElectrostaticOrigin();
        } else {
            zeta1 = serie.getParticle1().getZetaPotential();
            zeta2 = serie.getParticle1().getZetaPotential();
            ds1 = serie.getParticle1().getElectrostaticOrigin();
            ds2 = serie.getParticle1().getElectrostaticOrigin();
        }

        double separation = serie.getDistance();
        if (m_constantPot && separation < ds1 + ds2) {
            separation = ds1 + ds2;
        }

        double Ic = 0.0;
        for (int i = 0; i < serie.getMedium().getNumElectrolyteComponents(); i++) {
            double c = serie.getMedium().getElectrolyteComponentConcentration(i);
            double z = serie.getMedium().getElectrolyteComponentValence(i);
            Ic += c * z * z;
        }
        Ic *= 0.5;

        double debye_huckel = Math.sqrt(epsilon * epsilon_0 * k * T / (2 * e * e * Ic * 1000 * Na));
        double kappa = 1.0 / debye_huckel;

        double phi1 = zeta1 * Math.exp(kappa * ds1);
        double phi2 = zeta2 * Math.exp(kappa * ds2);

        double prefactor = 4 * Math.PI * epsilon * epsilon_0 * a1 * a2 / (a1 + a2 + separation);
        double term1 = phi1 * phi2 * Math.exp(-kappa * separation);

        return prefactor * term1;
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1, double size2) {

        double epsilon = serie.getMedium().getDielectricConstant();
        double epsilon_0 = 8.8541E-12;
        double k = 1.3807E-23;
        double T = serie.getTemperature();
        double e = 1.6022E-19;
        double Na = 6.022E23;

        double a1 = size1 * 0.5;
        double zeta1, zeta2;
        double ds1, ds2;
        if (serie.getHeterogeneous()) {
            zeta1 = serie.getParticle1().getZetaPotential();
            zeta2 = serie.getParticle2().getZetaPotential();
            ds1 = serie.getParticle1().getElectrostaticOrigin();
            ds2 = serie.getParticle2().getElectrostaticOrigin();
        } else {
            zeta1 = serie.getParticle1().getZetaPotential();
            zeta2 = serie.getParticle1().getZetaPotential();
            ds1 = serie.getParticle1().getElectrostaticOrigin();
            ds2 = serie.getParticle1().getElectrostaticOrigin();
        }

        double separation = serie.getDistance();
        if (m_constantPot && separation < ds1 + ds2) {
            separation = ds1 + ds2;
        }

        double Ic = 0.0;
        for (int i = 0; i < serie.getMedium().getNumElectrolyteComponents(); i++) {
            double c = serie.getMedium().getElectrolyteComponentConcentration(i);
            double z = serie.getMedium().getElectrolyteComponentValence(i);
            Ic += c * z * z;
        }
        Ic *= 0.5;

        double debye_huckel = Math.sqrt(epsilon * epsilon_0 * k * T / (2 * e * e * Ic * 1000 * Na));
        double kappa = 1.0 / debye_huckel;

        double phi1 = zeta1 * Math.exp(kappa * ds1);
        double phi2 = zeta2 * Math.exp(kappa * ds2);

        double prefactor = 4 * Math.PI * epsilon * epsilon_0 * a1;
        double term1 = phi1 * phi2 * Math.exp(-kappa * separation);

        return prefactor * term1;
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

        double epsilon = serie.getMedium().getDielectricConstant();
        double epsilon_0 = 8.8541E-12;
        double k = 1.3807E-23;
        double T = serie.getTemperature();
        double e = 1.6022E-19;
        double Na = 6.022E23;

        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        double zeta1, zeta2;
        double ds1, ds2;
        if (serie.getHeterogeneous()) {
            zeta1 = serie.getParticle1().getZetaPotential();
            zeta2 = serie.getParticle2().getZetaPotential();
            ds1 = serie.getParticle1().getElectrostaticOrigin();
            ds2 = serie.getParticle2().getElectrostaticOrigin();
        } else {
            zeta1 = serie.getParticle1().getZetaPotential();
            zeta2 = serie.getParticle1().getZetaPotential();
            ds1 = serie.getParticle1().getElectrostaticOrigin();
            ds2 = serie.getParticle1().getElectrostaticOrigin();
        }

        double separation = serie.getDistance();
        if (m_constantPot && separation < ds1 + ds2) {
            separation = ds1 + ds2;
        }

        double Ic = 0.0;
        for (int i = 0; i < serie.getMedium().getNumElectrolyteComponents(); i++) {
            double c = serie.getMedium().getElectrolyteComponentConcentration(i);
            double z = serie.getMedium().getElectrolyteComponentValence(i);
            Ic += c * z * z;
        }
        Ic *= 0.5;

        double debye_huckel = Math.sqrt(epsilon * epsilon_0 * k * T / (2 * e * e * Ic * 1000 * Na));
        double kappa = 1.0 / debye_huckel;

        double phi1 = zeta1 * Math.exp(kappa * ds1);
        double phi2 = zeta2 * Math.exp(kappa * ds2);

        double g = 4 * Math.PI * epsilon * epsilon_0 * a1 * a2;
        double h = phi1 * phi2;

        double term1 = -g * h * kappa * Math.exp(-kappa * separation) / (separation + a1 + a2);
        double term2 = -g * h * Math.exp(-kappa * separation) / ((separation + a1 + a2) * (separation + a1 + a2));

        return term1 + term2;
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {

        double epsilon = serie.getMedium().getDielectricConstant();
        double epsilon_0 = 8.8541E-12;
        double k = 1.3807E-23;
        double T = serie.getTemperature();
        double e = 1.6022E-19;
        double Na = 6.022E23;

        double a1 = size1 * 0.5;
        double zeta1, zeta2;
        double ds1, ds2;
        if (serie.getHeterogeneous()) {
            zeta1 = serie.getParticle1().getZetaPotential();
            zeta2 = serie.getParticle2().getZetaPotential();
            ds1 = serie.getParticle1().getElectrostaticOrigin();
            ds2 = serie.getParticle2().getElectrostaticOrigin();
        } else {
            zeta1 = serie.getParticle1().getZetaPotential();
            zeta2 = serie.getParticle1().getZetaPotential();
            ds1 = serie.getParticle1().getElectrostaticOrigin();
            ds2 = serie.getParticle1().getElectrostaticOrigin();
        }

        double separation = serie.getDistance();
        if (m_constantPot && separation < ds1 + ds2) {
            separation = ds1 + ds2;
        }

        double Ic = 0.0;
        for (int i = 0; i < serie.getMedium().getNumElectrolyteComponents(); i++) {
            double c = serie.getMedium().getElectrolyteComponentConcentration(i);
            double z = serie.getMedium().getElectrolyteComponentValence(i);
            Ic += c * z * z;
        }
        Ic *= 0.5;

        double debye_huckel = Math.sqrt(epsilon * epsilon_0 * k * T / (2 * e * e * Ic * 1000 * Na));
        double kappa = 1.0 / debye_huckel;

        double phi1 = zeta1 * Math.exp(kappa * ds1);
        double phi2 = zeta2 * Math.exp(kappa * ds2);

        double g = 4 * Math.PI * epsilon * epsilon_0 * a1;
        double h = phi1 * phi2;

        return -g * h * kappa * Math.exp(-kappa * separation);
    }

    private class MoreDialog extends javax.swing.JDialog {

        public MoreDialog() {
            super((java.awt.Frame) null, true);
            initComponents();

            rootPane.setDefaultButton(o_okButton);
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelAction");
            getRootPane().getActionMap().put("cancelAction", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    setVisible(false);
                }
            });

            //set GUI values
            o_constPot.setSelected(m_constantPot);
            o_variablePot.setSelected(!m_constantPot);

            setVisible(true);
        }

        private void initComponents() {
            o_boundaryCond = new javax.swing.ButtonGroup();
            javax.swing.JLabel o_description = new javax.swing.JLabel();
            o_variablePot = new javax.swing.JRadioButton();
            o_constPot = new javax.swing.JRadioButton();
            o_okButton = new javax.swing.JButton();
            o_cancelButton = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            o_description.setText("Boundary condition for overlapping shear planes:");

            o_boundaryCond.add(o_variablePot);
            o_boundaryCond.add(o_constPot);

            o_variablePot.setText("Variable potential (Default, best for bare particles)");
            o_constPot.setText("Constant potential (Best for adsorbed polyelectrolytes)");
            o_okButton.setText("OK");
            o_cancelButton.setText("Cancel");

            o_okButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    o_okButtonActionPerformed(evt);
                }
            });
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
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(o_description)
                                    .addGroup(layout.createSequentialGroup()
                                            .addGap(6, 6, 6)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addComponent(o_cancelButton)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(o_okButton))
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(o_constPot)
                                                            .addComponent(o_variablePot)))))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(o_description)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(o_variablePot)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(o_constPot)
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(o_okButton)
                                    .addComponent(o_cancelButton))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
        }

        private void o_okButtonActionPerformed(java.awt.event.ActionEvent evt) {
            m_constantPot = o_constPot.isSelected();
            m_unsavedChanges = true;

            setVisible(false);
        }

        private void o_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
            setVisible(false);
        }

        private javax.swing.JButton o_okButton;
        private javax.swing.JButton o_cancelButton;
        private javax.swing.ButtonGroup o_boundaryCond;
        private javax.swing.JRadioButton o_constPot;
        private javax.swing.JRadioButton o_variablePot;
    }
}
