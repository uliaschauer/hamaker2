/*
 * ParticleDialog.java
 *
 * Created on April 4, 2007, 2:49 PM
 */
package hamaker2;

import hamaker2.custom_ui.TooltipComboRenderer;
import hamaker2.custom_ui.TooltipComboBoxModel;
import hamaker2.particleSizeDistribution.ParticleSizeDistribution;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author uli
 */
public class ParticleDialog extends javax.swing.JDialog {

    /**
     * Enum that defines the quantity on the x axis in a PSD plot
     */
    public enum XAxisQuantity {

        /**
         * Plot the radius
         */
        kXAxis_Radius("radius", "Radius"),
        /**
         * Plot the diameter
         */
        kXAxis_Diameter("diameter", "Diameter");

        private final String m_id, m_name;

        XAxisQuantity(String id, String name) {
            m_id = id;
            m_name = name;
        }

        /**
         * Return the ID
         *
         * @return ID
         */
        public String getId() {
            return m_id;
        }

        /**
         * Return the human readable name
         *
         * @return Name
         */
        public String getName() {
            return m_name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    /**
     * Enum that defines the quantity on the x axis in a PSD plot
     */
    public enum YAxisQuantity {

        /**
         * Plot the number fraction
         */
        kYAxis_Number("number", "Number fraction", "Num."),
        /**
         * Plot the volume fraction
         */
        kYAxis_Volume("volume", "Volume fraction", "Vol."),
        /**
         * Plot the cumulative number faction
         */
        kYAxis_CummNumber("cumm_number", "Cummulative number fraction", "Cumm. Num."),
        /**
         * Plot the cumulative volume faction
         */
        kYAxis_CummVolume("cumm_volume", "Cummulative volume fraction", "Cumm. Vol.");

        private final String m_id, m_name, m_short;

        YAxisQuantity(String id, String name, String shortName) {
            m_id = id;
            m_name = name;
            m_short = shortName;
        }

        /**
         * Return the ID
         *
         * @return ID
         */
        public String getId() {
            return m_id;
        }

        /**
         * Return the long name
         *
         * @return Long name
         */
        public String getName() {
            return m_name;
        }

        /**
         * Return the short name
         *
         * @return Short name
         */
        public String getShort() {
            return m_short;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    /**
     * Creates new form ParticleDialog
     *
     * @param parent Parent to display this dialog over
     * @param modal is the dialog modal
     */
    public ParticleDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        m_particleToEdit = null;
        m_hamakerConstantDialog = new HamakerConstantDialog(null, true);
        //m_sizeDistributionModel = new ParticleSizeComboBoxModel();

        m_sizePlotData = new org.jfree.data.xy.XYSeriesCollection();

        initComponents();

        getRootPane().setDefaultButton(o_okButton);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelAction");
        getRootPane().getActionMap().put("cancelAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
                m_particleToEdit = null;
            }
        });

    }

    /**
     * Show the dialog for the given particle
     *
     * @param particle Particle to be edited
     */
    public void showForParticle(Particle particle) {
        m_particleToEdit = particle;
        m_particleToEdit.backupParticleSizeDistributions();

        o_hamakerConstant.setText(m_particleToEdit.getHamakerConstantAsText());
        o_density.setText(m_particleToEdit.getDensityAsText());
        o_zetaPotential.setText(m_particleToEdit.getZetaPotentialAsText());
        o_electrostaticOrigin.setText(m_particleToEdit.getElectrostaticOriginAsText());

        o_particleSizeDistribution.setModel(new TooltipComboBoxModel<>(m_particleToEdit.getParticleSizeDistributions().toArray(m_sizeDistributionArray)));

        m_particleToEdit.getSelectedSizeDistribution().populateValues();
        o_particleSizePanel.removeAll();
        o_particleSizePanel.setLayout(new CardLayout());
        for (int i = 0; i < m_particleToEdit.getParticleSizeDistributionCount(); i++) {

            o_particleSizePanel.add(m_particleToEdit.getParticleSizeDistribution(i).panel(), m_particleToEdit.getParticleSizeDistribution(i).id());
            m_particleToEdit.getParticleSizeDistribution(i).panel().addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    updateSizePlot();
                }
            });

            if (m_particleToEdit.getParticleSizeDistribution(i).equals(m_particleToEdit.getSelectedSizeDistribution())) {
                m_particleToEdit.getParticleSizeDistribution(i).setPrefSize();
                m_particleToEdit.getParticleSizeDistribution(i).panel().setVisible(true);
            } else {
                m_particleToEdit.getParticleSizeDistribution(i).panel().setVisible(false);
                m_particleToEdit.getParticleSizeDistribution(i).setZeroSize();
            }
        }
        ((CardLayout) o_particleSizePanel.getLayout()).show(o_particleSizePanel, m_particleToEdit.getSelectedSizeDistribution().id());
        pack();

        //display correct data in sizePlot
        updateSizePlot();

        //select the proper size distribution in menu
        o_particleSizeDistribution.setSelectedItem(particle.getSelectedSizeDistribution());

        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel o_zetaPotentialUnit = new javax.swing.JLabel();
        javax.swing.JLabel o_hamakerConstantUnit = new javax.swing.JLabel();
        javax.swing.JLabel o_zetaPoteltialPlaneLabel = new javax.swing.JLabel();
        javax.swing.JLabel o_zetaPotentialLabel = new javax.swing.JLabel();
        javax.swing.JLabel o_densityLabel = new javax.swing.JLabel();
        javax.swing.JLabel o_particleSizeLabel = new javax.swing.JLabel();
        o_hamakerListButton = new javax.swing.JButton();
        javax.swing.JLabel o_zetaPotentialPlaneUnit = new javax.swing.JLabel();
        o_zetaPotential = new javax.swing.JTextField();
        o_electrostaticOrigin = new javax.swing.JTextField();
        javax.swing.JLabel o_hamakerConstantLabel = new javax.swing.JLabel();
        javax.swing.JLabel o_densityUnit = new javax.swing.JLabel();
        o_density = new javax.swing.JTextField();
        o_hamakerConstant = new javax.swing.JTextField();
        o_okButton = new javax.swing.JButton();
        o_cancelButton = new javax.swing.JButton();
        o_particleSizePanel = new javax.swing.JPanel();
        o_particleSizeDistribution = new javax.swing.JComboBox<ParticleSizeDistribution>();
        o_sizePlotPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        o_sizePlotY = new javax.swing.JComboBox<YAxisQuantity>();
        jLabel2 = new javax.swing.JLabel();
        o_sizePlotX = new javax.swing.JComboBox<XAxisQuantity>();
        o_sizePlot = createChartPanel();

        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setModal(true);
        setName("Define Particle"); // NOI18N
        setResizable(false);

        o_zetaPotentialUnit.setText("mV");

        o_hamakerConstantUnit.setText("J");

        o_zetaPoteltialPlaneLabel.setText("Zeta Potential Plane:");

        o_zetaPotentialLabel.setText("Zeta Potential:");

        o_densityLabel.setText("Density:");

        o_particleSizeLabel.setText("Particle Size:");

        o_hamakerListButton.setText("List...");
        o_hamakerListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                o_hamakerListButtonActionPerformed(evt);
            }
        });

        o_zetaPotentialPlaneUnit.setText("nm");

        o_zetaPotential.setText("JTextField4");

        o_electrostaticOrigin.setText("JTextField5");

        o_hamakerConstantLabel.setText("Hamaker Constant:");

        o_densityUnit.setText("<html>g/cm<sup>3</sup></html>");

        o_density.setText("JTextField2");

        o_hamakerConstant.setText("JTextField1");

        o_okButton.setText("OK");
        o_okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                o_okButtonActionPerformed(evt);
            }
        });

        o_cancelButton.setText("Cancel");
        o_cancelButton.setDefaultCapable(false);
        o_cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                o_cancelButtonActionPerformed(evt);
            }
        });

        o_particleSizePanel.setBackground(new java.awt.Color(102, 102, 102));

        org.jdesktop.layout.GroupLayout o_particleSizePanelLayout = new org.jdesktop.layout.GroupLayout(o_particleSizePanel);
        o_particleSizePanel.setLayout(o_particleSizePanelLayout);
        o_particleSizePanelLayout.setHorizontalGroup(
            o_particleSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 372, Short.MAX_VALUE)
        );
        o_particleSizePanelLayout.setVerticalGroup(
            o_particleSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 160, Short.MAX_VALUE)
        );

        o_particleSizeDistribution.setRenderer(new TooltipComboRenderer<ParticleSizeDistribution>());
        o_particleSizeDistribution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                o_particleSizeDistributionActionPerformed(evt);
            }
        });

        o_sizePlotPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Plot");

        o_sizePlotY.setModel(new DefaultComboBoxModel<ParticleDialog.YAxisQuantity>(ParticleDialog.YAxisQuantity.values()));
        o_sizePlotY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                o_sizePlotYActionPerformed(evt);
            }
        });

        jLabel2.setText("vs");

        o_sizePlotX.setModel(new DefaultComboBoxModel<ParticleDialog.XAxisQuantity>(ParticleDialog.XAxisQuantity.values()));
        o_sizePlotX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                o_sizePlotXActionPerformed(evt);
            }
        });

        o_sizePlot.setBackground(new java.awt.Color(255, 255, 255));

        org.jdesktop.layout.GroupLayout o_sizePlotLayout = new org.jdesktop.layout.GroupLayout(o_sizePlot);
        o_sizePlot.setLayout(o_sizePlotLayout);
        o_sizePlotLayout.setHorizontalGroup(
            o_sizePlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        o_sizePlotLayout.setVerticalGroup(
            o_sizePlotLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 178, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout o_sizePlotPanelLayout = new org.jdesktop.layout.GroupLayout(o_sizePlotPanel);
        o_sizePlotPanel.setLayout(o_sizePlotPanelLayout);
        o_sizePlotPanelLayout.setHorizontalGroup(
            o_sizePlotPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(o_sizePlotPanelLayout.createSequentialGroup()
                .add(4, 4, 4)
                .add(o_sizePlotPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(o_sizePlot, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(o_sizePlotPanelLayout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(o_sizePlotY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(o_sizePlotX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(0, 0, Short.MAX_VALUE)))
                .add(4, 4, 4))
        );
        o_sizePlotPanelLayout.setVerticalGroup(
            o_sizePlotPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(o_sizePlotPanelLayout.createSequentialGroup()
                .add(4, 4, 4)
                .add(o_sizePlotPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(o_sizePlotY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(o_sizePlotX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(4, 4, 4)
                .add(o_sizePlot, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(4, 4, 4))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(o_cancelButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(o_okButton))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, o_zetaPotentialLabel)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, o_zetaPoteltialPlaneLabel))
                                .add(6, 6, 6)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(o_zetaPotential, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .add(o_electrostaticOrigin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(o_zetaPotentialUnit)
                            .add(o_zetaPotentialPlaneUnit))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, o_sizePlotPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, o_particleSizePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, o_hamakerConstantLabel)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, o_densityLabel)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, o_particleSizeLabel))
                                .add(6, 6, 6)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(o_hamakerConstant, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .add(o_density, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .add(o_particleSizeDistribution, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, o_densityUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                        .add(o_hamakerConstantUnit)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(o_hamakerListButton)))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(6, 6, 6)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(o_hamakerConstantLabel)
                    .add(o_hamakerConstant, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(o_hamakerConstantUnit)
                    .add(o_hamakerListButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(o_densityLabel)
                    .add(o_density, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(o_densityUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(o_particleSizeLabel)
                    .add(o_particleSizeDistribution, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(o_particleSizePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(o_sizePlotPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(o_zetaPotentialLabel)
                    .add(o_zetaPotential, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(o_zetaPotentialUnit))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(o_zetaPoteltialPlaneLabel)
                    .add(o_electrostaticOrigin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(o_zetaPotentialPlaneUnit))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(o_okButton)
                    .add(o_cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private JPanel createChartPanel() {
        JFreeChart chart = org.jfree.chart.ChartFactory.createXYLineChart(
                null, //title
                null, //x axis label
                null, //y axis label
                m_sizePlotData, //data 
                org.jfree.chart.plot.PlotOrientation.VERTICAL, //orientation
                false, //legend
                true, //tootips
                false //URLs
        );
        chart.getPlot().setBackgroundPaint(Color.white);

        org.jfree.chart.ChartPanel panel = new org.jfree.chart.ChartPanel(chart,
                200,
                200,
                50,
                50,
                400,
                200,
                false,
                false,
                false,
                false,
                false,
                false);

        return panel;
    }

    private void o_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_o_cancelButtonActionPerformed
        setVisible(false);
        m_particleToEdit.restoreParticleSizeDistributions();
        m_particleToEdit = null;
    }//GEN-LAST:event_o_cancelButtonActionPerformed

    private void o_okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_o_okButtonActionPerformed

        m_particleToEdit.setHamakerConstantFromText(o_hamakerConstant.getText());
        m_particleToEdit.setDensityFromText(o_density.getText());
        m_particleToEdit.getSelectedSizeDistribution().retrieveValues();
        m_particleToEdit.setZetaPotentialFromText(o_zetaPotential.getText());
        m_particleToEdit.setElectrostaticOriginFromText(o_electrostaticOrigin.getText());

        setVisible(false);
        m_particleToEdit = null;
    }//GEN-LAST:event_o_okButtonActionPerformed

    private void o_hamakerListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_o_hamakerListButtonActionPerformed
        m_hamakerConstantDialog.showForParticle(m_particleToEdit);

        o_hamakerConstant.setText(m_particleToEdit.getHamakerConstantAsText());
    }//GEN-LAST:event_o_hamakerListButtonActionPerformed

    private void o_particleSizeDistributionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_o_particleSizeDistributionActionPerformed

        m_particleToEdit.setSelectedSizeDistribution((ParticleSizeDistribution) o_particleSizeDistribution.getSelectedItem());

        //make corresponding UI elements visible at their proper size
        for (int i = 0; i < m_particleToEdit.getParticleSizeDistributionCount(); i++) {
            if (m_particleToEdit.getParticleSizeDistribution(i).equals(m_particleToEdit.getSelectedSizeDistribution())) {
                m_particleToEdit.getParticleSizeDistribution(i).setPrefSize();
                m_particleToEdit.getParticleSizeDistribution(i).panel().setVisible(true);
            } else {
                m_particleToEdit.getParticleSizeDistribution(i).panel().setVisible(false);
                m_particleToEdit.getParticleSizeDistribution(i).setZeroSize();
            }
        }

        //populate correspinding UI elements with data
        m_particleToEdit.getSelectedSizeDistribution().populateValues();

        //update the size plot
        updateSizePlot();

        //finally make visible
        ((CardLayout) o_particleSizePanel.getLayout()).show(o_particleSizePanel, m_particleToEdit.getSelectedSizeDistribution().id());

        //and repack the whole layout
        pack();
    }//GEN-LAST:event_o_particleSizeDistributionActionPerformed

    private void o_sizePlotYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_o_sizePlotYActionPerformed
        updateSizePlot();
    }//GEN-LAST:event_o_sizePlotYActionPerformed

    private void o_sizePlotXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_o_sizePlotXActionPerformed
        updateSizePlot();
    }//GEN-LAST:event_o_sizePlotXActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton o_cancelButton;
    private javax.swing.JTextField o_density;
    private javax.swing.JTextField o_electrostaticOrigin;
    private javax.swing.JTextField o_hamakerConstant;
    private javax.swing.JButton o_hamakerListButton;
    private javax.swing.JButton o_okButton;
    private javax.swing.JComboBox<ParticleSizeDistribution> o_particleSizeDistribution;
    private javax.swing.JPanel o_particleSizePanel;
    private javax.swing.JPanel o_sizePlot;
    private javax.swing.JPanel o_sizePlotPanel;
    private javax.swing.JComboBox<XAxisQuantity> o_sizePlotX;
    private javax.swing.JComboBox<YAxisQuantity> o_sizePlotY;
    private javax.swing.JTextField o_zetaPotential;
    // End of variables declaration//GEN-END:variables
    Particle m_particleToEdit;
    HamakerConstantDialog m_hamakerConstantDialog;
    private final ParticleSizeDistribution[] m_sizeDistributionArray = new ParticleSizeDistribution[1];
    org.jfree.data.xy.XYSeriesCollection m_sizePlotData;

    private void updateSizePlot() {
        org.jfree.chart.ChartPanel p = (org.jfree.chart.ChartPanel) o_sizePlot;

        //update x axis on chart
        XAxisQuantity x = o_sizePlotX.getItemAt(o_sizePlotX.getSelectedIndex());
        p.getChart().getXYPlot().getDomainAxis().setLabel(x.getName() + " (nm)");

        //update y axis on chart
        YAxisQuantity y = o_sizePlotY.getItemAt(o_sizePlotY.getSelectedIndex());
        p.getChart().getXYPlot().getRangeAxis().setLabel(y.getShort() + " (-)");

        //get new data
        m_sizePlotData.removeAllSeries();
        if (m_particleToEdit != null) {

            XYSeries newSeries = new XYSeries("");

            double[] cx, cy;
            if (x.equals(XAxisQuantity.kXAxis_Radius)) {
                cx = m_particleToEdit.getSelectedSizeDistribution().radii();
            } else if (x.equals(XAxisQuantity.kXAxis_Diameter)) {
                cx = m_particleToEdit.getSelectedSizeDistribution().diameters();
            } else {
                System.out.println("Unknown x axis");
                return;
            }

            //convert m to nm
            for (int i = 0; i < cx.length; i++) {
                cx[i] *= 1E9;
            }

            if (y.equals(YAxisQuantity.kYAxis_Volume)) {
                cy = m_particleToEdit.getSelectedSizeDistribution().volumeFractions();
            } else if (y.equals(YAxisQuantity.kYAxis_Number)) {
                cy = m_particleToEdit.getSelectedSizeDistribution().numberFractions();
            } else if (y.equals(YAxisQuantity.kYAxis_CummVolume)) {
                cy = m_particleToEdit.getSelectedSizeDistribution().cumulativeVolumeFraction();
            } else if (y.equals(YAxisQuantity.kYAxis_CummNumber)) {
                cy = m_particleToEdit.getSelectedSizeDistribution().cumulativeNumberFraction();
            } else {
                System.out.println("Unknown y axis");
                return;
            }

            for (int i = 0; i < m_particleToEdit.getSelectedSizeDistribution().numPoints(); i++) {
                newSeries.add(cx[i], cy[i]);
            }

            m_sizePlotData.addSeries(newSeries);
        }
        o_sizePlot.repaint();
    }

}
