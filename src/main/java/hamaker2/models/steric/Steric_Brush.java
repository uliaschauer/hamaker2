/*
 * To change this template, choose Tools | Templates
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
 * This class implements the brush steric model as defined in P.G. De Gennes,
 * Advances in Colloid and Interface Science, 27(3), 189-209, 1987
 *
 * @author aschauer
 */
public class Steric_Brush extends AbstractInteractionModel implements StericInteractionModel {

    private boolean m_unsavedChanges;
    private double m_thickness;
    private double m_surfaceDensity;
    private boolean m_hardwall;
    final private String m_version = "1.0";

    /**
     * Creates a new instance of Steric_Brush
     */
    public Steric_Brush() {

        m_unsavedChanges = false;

        m_surfaceDensity = 1 / (1E-9 * 1E-9);
        m_thickness = 2E-9;
        m_hardwall = false;
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "steric_brush";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Brush";
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
        output.write(String.valueOf(m_surfaceDensity));
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
            m_surfaceDensity = Utils.StringToDouble(input.readLine());
            m_thickness = Utils.StringToDouble(input.readLine());
        } else {
            //use new versioned reader code
            if (version.equals("1.0")) {
                m_surfaceDensity = Utils.StringToDouble(input.readLine());
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
    public Steric_Brush duplicate() {

        Steric_Brush copy = new Steric_Brush();

        copy.m_unsavedChanges = true;

        copy.m_surfaceDensity = m_surfaceDensity;
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

        vars.add(new PlotVariable(idPrefix, namePrefix, "steric_brush_density", "Steric(Brush): Surface density", "nm-2", 0, 1 / (0.5E-9 * 0.5E-9), 1E-18, "0.0"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "steric_brush_thick", "Steric(Brush): Thickness", "nm", 0, 10E-9, 1E9, "0.0"));

        return vars;
    }

    @Override
    public double getPlotVariableValue(String id) {
        switch (id) {
            case "steric_brush_density":
                return m_surfaceDensity;
            case "steric_brush_thick":
                return m_thickness;
        }

        return PlotVariable.kUnknownPlotVariableID;
    }

    @Override
    public void setPlotVariableValue(String id, double value) {
        switch (id) {
            case "steric_brush_density":
                m_surfaceDensity = value;
                break;
            case "steric_brush_thick":
                m_thickness = value;
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

    private double rawPotential(double h, double da) {

        double spacing = Math.sqrt(1.0 / m_surfaceDensity);

        return (da * (32.0 / 5.0 * Math.pow(da / h, 5.0 / 4.0) + 4.0 / 7.0 * Math.pow(h / da, 7.0 / 4.0))) / (Math.pow(2, 3.0 / 4.0) * spacing);
    }

    private double interactionPotential_Sphere_Sphere(Serie serie, double size1, double size2) {
        double h = serie.getDistance();
        double da = m_thickness;
        double k = 1.3807E-23;
        double T = serie.getTemperature();

        if (h < 2 * da) {
            return (k * T * (rawPotential(h, da) - rawPotential(2 * da, da)));
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
            return (k * T * (rawPotential(h, da) - rawPotential(2 * da, da)));
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

        double spacing = Math.sqrt(1.0 / m_surfaceDensity);

        if (h < 2 * da) {
            return (-k * T / spacing * (Math.pow(2 * da / h, 9.0 / 4.0) - Math.pow(h / (2 * da), 3.0 / 4.0)));
        } else {
            return 0;
        }
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {
        double h = serie.getDistance();
        double da = m_thickness;
        double k = 1.3807E-23;
        double T = serie.getTemperature();

        double spacing = Math.sqrt(1.0 / m_surfaceDensity);

        if (h < 2 * da) {
            return (-k * T / spacing * (Math.pow(2 * da / h, 9.0 / 4.0) - Math.pow(h / (2 * da), 3.0 / 4.0)));
        } else {
            return 0;
        }
    }

    private class MoreDialog extends javax.swing.JDialog {

        public MoreDialog() {
            super((javax.swing.JDialog) null, true);
            initComponents();

            rootPane.setDefaultButton(o_okButton);
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelAction");
            getRootPane().getActionMap().put("cancelAction", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    setVisible(false);
                }
            });

            DecimalFormat format = new DecimalFormat("0.00");
            o_density.setText(format.format(m_surfaceDensity * 1E-18));
            o_thickness.setText(format.format(m_thickness * 1E9));
            o_hardwall.setSelected(m_hardwall);

            setVisible(true);
        }

        private void initComponents() {

            jLabel1 = new javax.swing.JLabel();
            jLabel2 = new javax.swing.JLabel();
            o_thickness = new javax.swing.JTextField();
            o_density = new javax.swing.JTextField();
            jLabel3 = new javax.swing.JLabel();
            jLabel4 = new javax.swing.JLabel();
            o_hardwall = new javax.swing.JCheckBox();
            o_okButton = new javax.swing.JButton();
            o_cancelButton = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setResizable(false);

            jLabel1.setText("Adsorbed layer thickness:");

            jLabel2.setText("Polymer surface density:");

            o_thickness.setText("");
            o_density.setText("");

            jLabel3.setText("nm");

            jLabel4.setText("<html>nm<sup>-2</sup></html>");

            o_hardwall.setText("Close-range hard-wall");
            o_hardwall.setEnabled(false);

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

            org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                            .addContainerGap()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(o_hardwall)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                            .add(o_cancelButton)
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(o_okButton))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                    .add(layout.createSequentialGroup()
                                                            .add(jLabel1)
                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                            .add(o_thickness, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                                                    .add(layout.createSequentialGroup()
                                                            .add(jLabel2)
                                                            .add(21, 21, 21)
                                                            .add(o_density)))
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(jLabel3)
                                                    .add(jLabel4))))
                            .addContainerGap()));
            layout.setVerticalGroup(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                            .addContainerGap()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(o_thickness, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel1)
                                    .add(jLabel3))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(o_density, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel2)
                                    .add(jLabel4))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(o_hardwall)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(o_okButton)
                                    .add(o_cancelButton))
                            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

            pack();
        }

        private void o_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
            setVisible(false);
        }

        private void o_okButtonActionPerformed(java.awt.event.ActionEvent evt) {
            m_thickness = 1E-9 * Utils.StringToDouble(o_thickness.getText());
            m_surfaceDensity = 1E18 * Utils.StringToDouble(o_density.getText());
            m_hardwall = o_hardwall.isSelected();
            setVisible(false);
            m_unsavedChanges = true;
        }
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JTextField o_density;
        private javax.swing.JButton o_cancelButton;
        private javax.swing.JButton o_okButton;
        private javax.swing.JTextField o_thickness;
        private javax.swing.JCheckBox o_hardwall;
    }
}
