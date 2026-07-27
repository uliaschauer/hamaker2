/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.models.steric;

import hamaker2.*;
import hamaker2.models.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * This class implements the mushroom steric model as defined in P.G. De Gennes,
 * Advances in Colloid and Interface Science, 27(3), 189-209, 1987
 *
 * @author aschauer
 */
public class Steric_Mushroom extends AbstractInteractionModel implements StericInteractionModel {

    private boolean m_unsavedChanges;
    private double m_thickness;
    final private String m_version = "1.0";

    /**
     * Creates a new instance of Steric_Mushroom
     */
    public Steric_Mushroom() {

        m_unsavedChanges = false;

        m_thickness = 2E-9;

    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "steric_mushroom";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Mushroom";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "P.G. De Gennes, Advances in Colloid and Interface Science, 27(3), 189-209, 1987";
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
            m_thickness = Utils.StringToDouble(input.readLine());
        } else {
            //use new versioned reader code
            if (version.equals("1.0")) {
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
    public Steric_Mushroom duplicate() {

        Steric_Mushroom copy = new Steric_Mushroom();

        copy.m_unsavedChanges = true;

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
        vars.add(new PlotVariable(idPrefix, namePrefix, "steric_mushroom_anchor", "Steric(Mushroom): Anchor Point Spacing", "nm", 0, 100E-9, 1E9, "0.0"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "steric_mushroom_thick", "Steric(Mushroom): Thickness", "nm", 0, 10E-9, 1E9, "0.0"));

        return vars;
    }

    @Override
    public double getPlotVariableValue(String id) {

        if (id.equals("steric_mushroom_thick")) {
            return m_thickness;
        }

        return PlotVariable.kUnknownPlotVariableID;
    }

    @Override
    public void setPlotVariableValue(String id, double value) {
        if (id.equals("steric_mushroom_thick")) {
            m_thickness = value;
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
        double h = serie.getDistance();
        double da = m_thickness;
        double k = 1.3807E-23;
        double T = serie.getTemperature();

        if (h < da) {
            return (k * T * Math.pow(da / h, 5.0 / 3.0));
        } else {
            return 0;
        }
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1, double size2) {
        double h = serie.getDistance();
        double da = m_thickness;
        double k = 1.3807E-23;
        double T = serie.getTemperature();

        if (h < 2 * da) {
            return (k * T * Math.pow(da / h, 5.0 / 3.0));
        } else {
            return 0;
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
        double h = serie.getDistance();
        double da = m_thickness;
        double k = 1.3807E-23;
        double T = serie.getTemperature();

        if (h < da) {
            return (-k * T / da * (Math.pow(da / h, 8.0 / 3.0)));
        } else {
            return 0;
        }
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {
        double h = serie.getDistance();
        double da = m_thickness;
        double k = 1.3807E-23;
        double T = serie.getTemperature();

        if (h < 2 * da) {
            return (-k * T / da * (Math.pow(da / h, 8.0 / 3.0)));
        } else {
            return 0;
        }
    }

    private class MoreDialog extends javax.swing.JDialog {

        public MoreDialog() {
            super((javax.swing.JDialog) null, true);
            initComponents();

            DecimalFormat format = new DecimalFormat("0.00");
            o_thickness.setText(format.format(m_thickness * 1E9));

            setVisible(true);
        }

        private void initComponents() {

            jLabel1 = new javax.swing.JLabel();
            o_thickness = new javax.swing.JFormattedTextField();
            jLabel3 = new javax.swing.JLabel();
            o_okButton = new javax.swing.JButton();
            o_cancelButton = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            jLabel1.setText("Adsorbed layer thickness:");
            o_thickness.setText("jFormattedTextField1");
            jLabel3.setText("nm");

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
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel1)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(o_thickness, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(o_thickness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(o_okButton)
                                    .addComponent(o_cancelButton))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
        }

        private void o_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
            setVisible(false);
        }

        private void o_okButtonActionPerformed(java.awt.event.ActionEvent evt) {
            m_thickness = 1E-9 * Utils.StringToDouble(o_thickness.getText());
            setVisible(false);
            m_unsavedChanges = true;
        }

        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JButton o_cancelButton;
        private javax.swing.JButton o_okButton;
        private javax.swing.JFormattedTextField o_thickness;
    }
}
