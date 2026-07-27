/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.models.dispersion;

/**
 *
 * @author aschauer
 */
import hamaker2.*;
import hamaker2.models.AbstractInteractionModel;
import hamaker2.models.GeometryClass;
import hamaker2.models.PlotVariable;
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
 * Implementation of the effective Hamaker constant interaction model as defined
 * in Russel, W.B., Saville, D.A., and Schowalter, W.R. Colloidal
 * Dispersions, Cambridge Press, (1985)
 *
 * @author asulrich
 */
public class Dispersion_Effective extends AbstractInteractionModel implements DispersionInteractionModel {

    private double m_epsilon_particle1, m_epsilon_particle2;
    private double m_refractive_particle1, m_refractive_particle2, m_refractive_medium;
    private boolean m_unsavedChanges;
    final private String m_version = "1.0";

    /**
     * Creates a new instance of Dispersion_Effective
     */
    public Dispersion_Effective() {
        m_epsilon_particle1 = 9.1;
        m_refractive_particle1 = 1.765;
        m_epsilon_particle2 = 9.1;
        m_refractive_particle2 = 1.765;
        m_refractive_medium = 1.334;
        m_unsavedChanges = false;
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "dispersion_effective";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Effective";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "Russel, W.B., Saville, D.A., and Schowalter, W.R. Colloidal Dispersions, Cambridge Press, (1985)";
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
        output.write(String.valueOf(m_epsilon_particle1));
        output.newLine();
        output.write(String.valueOf(m_epsilon_particle2));
        output.newLine();
        output.write(String.valueOf(m_refractive_particle1));
        output.newLine();
        output.write(String.valueOf(m_refractive_particle2));
        output.newLine();
        output.write(String.valueOf(m_refractive_medium));
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
            //there was a bug in a previous version of hamaker
            //the effective model params were not written
            //to counter this i check here if data was written or not
            //ugly but about as good as i can fix it
            input.mark(1024);
            if (!input.readLine().equals("***DISPERSION_MODEL_END***")) {
                //reset to read this line
                input.reset();

                m_epsilon_particle1 = Utils.StringToDouble(input.readLine());
                m_epsilon_particle2 = Utils.StringToDouble(input.readLine());
                m_refractive_particle1 = Utils.StringToDouble(input.readLine());
                m_refractive_particle2 = Utils.StringToDouble(input.readLine());
                m_refractive_medium = Utils.StringToDouble(input.readLine());
            } else {
                //also reset so that the terminal is read correctly
                input.reset();
            }
        } else {
            //use the new versioned reader code
            if (version.equals("1.0")) {

                m_epsilon_particle1 = Utils.StringToDouble(input.readLine());
                m_epsilon_particle2 = Utils.StringToDouble(input.readLine());
                m_refractive_particle1 = Utils.StringToDouble(input.readLine());
                m_refractive_particle2 = Utils.StringToDouble(input.readLine());
                m_refractive_medium = Utils.StringToDouble(input.readLine());

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
    public Dispersion_Effective duplicate() {

        Dispersion_Effective copy = new Dispersion_Effective();

        copy.m_epsilon_particle1 = m_epsilon_particle1;
        copy.m_epsilon_particle2 = m_epsilon_particle2;
        copy.m_refractive_particle1 = m_refractive_particle1;
        copy.m_refractive_particle2 = m_refractive_particle2;
        copy.m_refractive_medium = m_refractive_medium;
        copy.m_unsavedChanges = true;

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
     * Return the list of plot variables
     * @param namePrefix prefix to be applied to names
     * @param idPrefix prefix to be applied to IDs
     * @return list of plot variables
     */
    @Override
    public ArrayList<PlotVariable> plotVariables(String namePrefix, String idPrefix) {
        ArrayList<PlotVariable> vars = new ArrayList<>();

        vars.add(new PlotVariable(idPrefix, namePrefix, "eps_1", "Dielectric (Body 1)", "-", 1.0, 10.0, 1.0, "0.00"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "eps_2", "Dielectric (Body 2)", "-", 1.0, 10.0, 1.0, "0.00"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "n_1", "Refractive (Body 1)", "-", 1.0, 2.0, 1.0, "0.00"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "n_2", "Refractive (Body 2)", "-", 1.0, 2.0, 1.0, "0.00"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "n_m", "Refractive (Medium)", "-", 1.0, 2.0, 1.0, "0.00"));

        return vars;
    }

    @Override
    public double getPlotVariableValue(String id) {
        switch (id) {
            case "eps_1":
                return m_epsilon_particle1;
            case "eps_2":
                return m_epsilon_particle2;
            case "n_1":
                return m_refractive_particle1;
            case "n_2":
                return m_refractive_particle2;
            case "n_m":
                return m_refractive_medium;
            default:
                System.out.println("Effective, unknown plot variable: " + id);
                break;
        }

        return PlotVariable.kUnknownPlotVariableID;
    }

    @Override
    public void setPlotVariableValue(String id, double value) {
        switch (id) {
            case "eps_1":
                m_epsilon_particle1 = value;
                break;
            case "eps_2":
                m_epsilon_particle2 = value;
                break;
            case "n_1":
                m_refractive_particle1 = value;
                break;
            case "n_2":
                m_refractive_particle2 = value;
                break;
            case "n_m":
                m_refractive_medium = value;
                break;
            default:
                System.out.println("Effective, unknown plot variable: " + id);
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
        double AH;
        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(effectiveHamakerConstant(serie, D, 1) * effectiveHamakerConstant(serie, D, 2));
        } else {
            AH = effectiveHamakerConstant(serie, D, 1);
        }

        double term1 = 2 * a1 * a2 / (D * D + 2 * a1 * D + 2 * a2 * D);
        double term2 = 2 * a1 * a2 / (D * D + 2 * a1 * D + 2 * a2 * D + 4 * a1 * a2);
        double term3 = Math.log((D * D + 2 * a1 * D + 2 * a2 * D) / (D * D + 2 * a1 * D + 2 * a2 * D + 4 * a1 * a2));

        return -AH / 6.0 * (term1 + term2 + term3);
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1, double size2) {
        double AH;
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(effectiveHamakerConstant(serie, D, 1) * effectiveHamakerConstant(serie, D, 2));
        } else {
            AH = effectiveHamakerConstant(serie, D, 1);
        }

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
        double AH;
        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        double D = serie.getDistance();

        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(effectiveHamakerConstant(serie, D, 1) * effectiveHamakerConstant(serie, D, 2));
        } else {
            AH = effectiveHamakerConstant(serie, D, 1);
        }

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
        double AH;
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(effectiveHamakerConstant(serie, D, 1) * effectiveHamakerConstant(serie, D, 2));
        } else {
            AH = effectiveHamakerConstant(serie, D, 1);
        }

        double term1 = 1.0 / D;
        double term2 = 1.0 / (D + 2 * a1);
        double term3 = a1 / (D * D);
        double term4 = a1 / (D * D + 4 * D * a1 + 4 * a1 * a1);

        return -AH / 6.0 * (term1 - term2 - term3 - term4);
    }
    
    private double effectiveHamakerConstant(Serie serie, double D, int particle) {
        double h = 6.625E-34 / (2 * Math.PI);
        double c = 299800000;
        double kb = 1.3807E-23;
        double omega = 2.01E16;

        double pE0;
        double pN;
        if (particle == 1) {
            pE0 = m_epsilon_particle1;
            pN = m_refractive_particle1;
        } else if (particle == 2) {
            pE0 = m_epsilon_particle2;
            pN = m_refractive_particle2;
        } else {
            pE0 = 0.0;
            pN = 0.0;
            System.err.println("Invalid particle id in Dispersion_Effective::effectiveHamakerConstant");
        }

        double mE0 = serie.getMedium().getDielectricConstant();
        double mN = m_refractive_medium;

        double term1 = 3.0 / 4.0 * kb * serie.getTemperature()
                * Math.pow((pE0 - mE0) / (pE0 + mE0), 2.0);
        double term2num = 3.0 * h * omega
                * Math.pow(Math.pow(pN, 2.0) - Math.pow(mN, 2.0), 2.0);

        double term2denum = 16.0 * Math.sqrt(2.0)
                * Math.pow(Math.pow(pN, 2.0) + Math.pow(mN, 2.0), 1.5);

        double term3 = Math.PI * mN / (4.0 * Math.sqrt(2.0))
                * Math.sqrt(Math.pow(pN, 2.0) + Math.pow(mN, 2.0)) * D * omega
                / c;

        //System.out.println(String.valueOf(D) + "\t" + String.valueOf(term1 + term2num / term2denum * Math.pow(1 + Math.pow(term3, 1.5), -2.0 / 3.0)));
        return term1
                + term2num / term2denum * Math.pow(1 + Math.pow(term3, 1.5), -2.0 / 3.0);
    }

    /* More dialog */
    private class MoreDialog extends javax.swing.JDialog {

        public MoreDialog() {
            super((java.awt.Frame) null, true);
            initComponents();

            rootPane.setDefaultButton(o_saveButton);
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelAction");
            getRootPane().getActionMap().put("cancelAction", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    setVisible(false);
                }
            });

            o_medium_refractive.setText(new DecimalFormat("0.00").format(m_refractive_medium));
            o_particle1_dielectric.setText(new DecimalFormat("0.00").format(m_epsilon_particle1));
            o_particle2_dielectric.setText(new DecimalFormat("0.00").format(m_epsilon_particle2));
            o_particle1_refractive.setText(new DecimalFormat("0.00").format(m_refractive_particle1));
            o_particle2_refractive.setText(new DecimalFormat("0.00").format(m_refractive_particle2));

            setVisible(true);
        }

        private void initComponents() {
            jLabel1 = new javax.swing.JLabel();
            jLabel2 = new javax.swing.JLabel();
            jLabel3 = new javax.swing.JLabel();
            jLabel4 = new javax.swing.JLabel();
            jLabel5 = new javax.swing.JLabel();
            o_particle1_dielectric = new javax.swing.JTextField();
            o_particle2_dielectric = new javax.swing.JTextField();
            o_medium_refractive = new javax.swing.JTextField();
            o_particle1_refractive = new javax.swing.JTextField();
            o_particle2_refractive = new javax.swing.JTextField();
            o_cancelButton = new javax.swing.JButton();
            o_saveButton = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setResizable(false);

            jLabel1.setText("Dielectric constant:");
            jLabel2.setText("Refractive index:");
            jLabel3.setText("Medium");
            jLabel4.setText("Particle 1");
            jLabel5.setText("Particle 2");

            o_cancelButton.setText("Cancel");
            o_cancelButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    o_cancelButtonActionPerformed(evt);
                }
            });

            o_saveButton.setText("Save");
            o_saveButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    o_saveButtonActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                            .addContainerGap()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(layout.createSequentialGroup()
                                            .add(o_cancelButton)
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(o_saveButton))
                                    .add(layout.createSequentialGroup()
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(jLabel1)
                                                    .add(jLabel2))
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(jLabel3)
                                                    .add(o_medium_refractive, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(jLabel4)
                                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                                            .add(org.jdesktop.layout.GroupLayout.LEADING, o_particle1_refractive, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                                            .add(org.jdesktop.layout.GroupLayout.LEADING, o_particle1_dielectric, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(jLabel5)
                                                    .add(o_particle2_dielectric, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, o_particle2_refractive, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
            layout.setVerticalGroup(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                            .addContainerGap()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel4)
                                    .add(jLabel5)
                                    .add(jLabel3))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(o_particle1_dielectric, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(o_particle2_dielectric, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel1))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(o_particle1_refractive, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(o_particle2_refractive, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(o_medium_refractive, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel2))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(o_saveButton)
                                    .add(o_cancelButton))
                            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

            pack();
        }

        private void o_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
            setVisible(false);
        }

        private void o_saveButtonActionPerformed(java.awt.event.ActionEvent evt) {

            m_refractive_medium = Double.parseDouble(o_medium_refractive.getText());
            m_epsilon_particle1 = Double.parseDouble(o_particle1_dielectric.getText());
            m_epsilon_particle2 = Double.parseDouble(o_particle2_dielectric.getText());
            m_refractive_particle1 = Double.parseDouble(o_particle1_refractive.getText());
            m_refractive_particle2 = Double.parseDouble(o_particle2_refractive.getText());

            setVisible(false);
            m_unsavedChanges = true;
        }
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JButton o_cancelButton;
        private javax.swing.JTextField o_medium_refractive;
        private javax.swing.JTextField o_particle1_dielectric;
        private javax.swing.JTextField o_particle1_refractive;
        private javax.swing.JTextField o_particle2_dielectric;
        private javax.swing.JTextField o_particle2_refractive;
        private javax.swing.JButton o_saveButton;
    }
}
