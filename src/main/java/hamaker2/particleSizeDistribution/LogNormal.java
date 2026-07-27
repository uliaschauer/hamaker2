/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.particleSizeDistribution;

import hamaker2.HamakerInfo;
import hamaker2.Utils;
import hamaker2.models.PlotVariable;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Class implements a log-normal PSD
 * @author asulrich
 */
public class LogNormal extends AbstractParticleSizeDistribution {

    private double m_median, m_spread;
    JTextField o_modeField;
    JTextField o_spreadField;
    private final double kInvsqrt2pi = 1.0 / Math.sqrt(2.0 * Math.PI);

    /**
     * Construct a log-normal PSD with median 600 nm and spread 1.2
     */
    public LogNormal() {
        this(600E-9, 1.2);
    }

    /**
     * Construct a log-normal PSD with given median and spread
     * @param mode Median of the PSD
     * @param spread Spread of the PSD
     */
    public LogNormal(double mode, double spread) {
        m_version = "1.0";
        m_median = mode;
        m_spread = spread;
        composePanel();
    }

    @Override
    public String id() {
        return "lognormal";
    }

    @Override
    public String name() {
        return "Log-normal";
    }

    @Override
    public String reference() {
        return "Log-normal size distribution";
    }

    @Override
    public int numPoints() {
        return 64;
    }

    /**
     * Return the diameters of this PSD
     * @return array of diameters
     */
    @Override
    public double[] diameters() {
        int count = numPoints();
        double[] diameters = new double[count];

        double mu = m_median;
        double min = Math.max(0.0, mu / (m_spread * m_spread * m_spread));
        double max = mu * (m_spread * m_spread * m_spread);

        for (int i = 0; i < count; i++) {
            diameters[i] = min + i * (max - min) / (count - 1);
        }

        return diameters;
    }

    /**
     * Return the radii of this PSD
     * @return array of radii
     */
    @Override
    public double[] radii() {
        double[] radii = diameters();
        for (int i = 0; i < radii.length; i++) {
            radii[i] *= 0.5;
        }
        return radii;
    }

    /**
     * Return the number fractions of the PSD
     * @return array of number fractions
     */
    @Override
    public double[] numberFractions() {

        //this was the first implementation, later substrututed by the one below
        
        /*int count = numPoints();
        double[] numberFractions = new double[count];

        double sum = 0.0;

        double mu = m_median;
        //double sigma = Math.log(m_spread);
        //double m = mu * Math.exp(0.5 * sigma * sigma);
        //double s = Math.sqrt(Math.exp(2.0 * mu + sigma * sigma) * (2.0 * Math.exp(sigma) - 1.0));
        double min = Math.max(0.0, mu / (m_spread * m_spread * m_spread));
        double max = mu * (m_spread * m_spread * m_spread);

        for (int i = 0; i < count; i++) {

            double diameter = min + i * (max - min) / (count - 1);

            double term1 = Math.log(diameter / m_median) / Math.log(m_spread);
            double exp_term = -0.5 * term1 * term1;

            numberFractions[i] = kInvsqrt2pi / Math.log(m_spread) / diameter
                    * Math.exp(exp_term);

            sum += numberFractions[i];
        }

        //normalize
        for (int i = 0; i < count; i++) {
            numberFractions[i] /= sum;
        }

        return numberFractions;*/
        
        int count = numPoints();
        double[] numberFractions = new double[count];
        
        double sum = 0.0;
        
        double[] diameters = diameters();
        
        for (int i = 0; i < count; i++) {
            
            double d = diameters[i];
            
            double ln_d = Math.log(d);
            double ln_m = Math.log(m_median);
            double ln_s = Math.log(m_spread);
            double ln_d_m_m2 = Math.pow(ln_d - ln_m, 2.0);
            double ln_s_s = ln_s * ln_s;
            
            numberFractions[i] = kInvsqrt2pi / ln_s * Math.exp(-0.5 * ln_d_m_m2 / ln_s_s);
      
            sum += numberFractions[i];
        }

        //normalize
        for (int i = 0; i < count; i++) {
            numberFractions[i] /= sum;
        }
        
        return numberFractions;

    }

    /**
     * Return the volume fractions of the PSD
     * @return array of volume fractions
     */
    @Override
    public double[] volumeFractions() {
        return numberToVolumeFraction();
    }

    @Override
    public ParticleSizeDistribution copy() {
        LogNormal copy = new LogNormal();
        copy.m_median = m_median;
        copy.m_spread = m_spread;
        return copy;
    }

    @Override
    public boolean hasParameters() {
        return true;
    }

    @Override
    public void save(BufferedWriter writer) throws IOException {
        writer.write("Version " + m_version);
        writer.newLine();
        writer.write(String.valueOf(m_median));
        writer.newLine();
        writer.write(String.valueOf(m_spread));
        writer.newLine();
    }

    @Override
    public void load(BufferedReader reader) throws IOException {
        //check if there is a version tag in the file
        String version = "";
        reader.mark(1024);
        Scanner s = new Scanner(reader.readLine());
        if (s.hasNext("Version")) {
            s.skip("Version");
            version = s.next();
        } else {
            //make sure to rewind the input if there was no version tag
            reader.reset();
        }

        if (version.equals("1.0")) {
            m_median = Utils.StringToDouble(reader.readLine());
            m_spread = Utils.StringToDouble(reader.readLine());
        } else {
            //if no reader code found, inform the user/developer
            JOptionPane.showMessageDialog(null, "The log-normal particle size distribution is of version " + m_version + ", which can't be read by Hamaker version " + HamakerInfo.version());
        }
    }

    @Override
    public ArrayList<PlotVariable> plotVariables(String namePrefix, String idPrefix) {
        ArrayList<PlotVariable> vars = new ArrayList<>();

        vars.add(new PlotVariable(idPrefix, namePrefix, "median", "Diameter", "nm", 5.0E-8, 5.0E-7, 1.0E9, "0.0"));
        vars.add(new PlotVariable(idPrefix, namePrefix, "spread", "Spread", "-", 0.5, 3.0, 1.0, "0.0"));

        return vars;
    }

    @Override
    public double getPlotVariableValue(String id) {
        switch (id) {
            case "median":
                return m_median;
            case "spread":
                return m_spread;
            default:
                System.out.println("LogNormal, unknown plot variable: " + id);
                return PlotVariable.kUnknownPlotVariableID;

        }
    }

    @Override
    public void setPlotVariableValue(String id, double value) {
        switch (id) {
            case "median":
                m_median = value;
                break;
            case "spread":
                m_spread = value;
                break;
            default:
                System.out.println("LogNormal, unknown plot variable: " + id);
        }
    }

    private void composePanel() {

        o_modeField = new JTextField("");
        o_spreadField = new JTextField("");

        m_panel.setLayout(new GridLayout(0, 3));
        m_panel.add(new JLabel("Distribution mode:"));
        m_panel.add(o_modeField);
        m_panel.add(new JLabel("nm"));
        m_panel.add(new JLabel("Distribution spread:"));
        m_panel.add(o_spreadField);
        m_panel.add(new JLabel("nm"));

        o_modeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            void update() {
                retrieveMedian();
                m_panel.firePropertyChange("size", 0.0, 1.0);
            }
        });

        o_spreadField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            void update() {
                retrieveSpread();
                m_panel.firePropertyChange("size", 0.0, 1.0);
            }
        });

        m_panel.validate();

        savePrefSize();
    }

    @Override
    public void populateValues() {
        o_modeField.setText(new DecimalFormat("0.0").format(m_median * 1E9));
        o_spreadField.setText(new DecimalFormat("0.0").format(m_spread));
    }

    @Override
    public void retrieveValues() {
        retrieveMedian();
        retrieveSpread();
    }

    /**
     *
     */
    public void retrieveMedian() {
        m_median = Utils.StringToDouble(o_modeField.getText()) * 1E-9;
    }

    /**
     *
     */
    public void retrieveSpread() {
        m_spread = Utils.StringToDouble(o_spreadField.getText());
    }

}
