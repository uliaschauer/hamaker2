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
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Class that implements a simple monodisperse size distribution
 * @author asulrich
 */
public class Monodisperse extends AbstractParticleSizeDistribution {

    private double m_diameter;
    private JFormattedTextField o_diameterField;
    
    /**
     * Construct a monodisperse distribution with radius 100 nm
     */
    public Monodisperse() {
        this(100E-9);
    }
    
    /**
     * Construct a monodisperse distribution with the given radius
     * @param size
     */
    public Monodisperse(double size) {
        m_version = "1.0";
        m_diameter = size;
        composePanel();
    }
    
    /**
     * Method provided for compatibility with old file reader version 
     * @param size
     */
    public void setSize(double size) {
        m_diameter = size;
    }
        
    @Override
    public String id() {
        return "monodisperse";
    }

    @Override
    public String name() {
        return "Monodisperse";
    }
    
    @Override
    public String reference() {
        return "Size distribution with single size";
    }

    @Override
    public int numPoints() {
        return 5;
    }

    /**
     * Return the diameters of this PSD
     * @return array of diameters
     */
    @Override
    public double[] diameters() {
        double[] diameters = new double[5];
        diameters[0] = m_diameter - 1E-9;
        diameters[1] = m_diameter;
        diameters[2] = m_diameter;
        diameters[3] = m_diameter;
        diameters[4] = m_diameter + 1E-9;
        return diameters;
    }
    
    /**
     * Return the radii of this PSD
     * @return array of radii
     */
    @Override
    public double[] radii() {
        double[] radii = new double[5];
        radii[0] = (m_diameter - 1E-9) * 0.5;
        radii[1] = m_diameter * 0.5;
        radii[2] = m_diameter * 0.5;
        radii[3] = m_diameter * 0.5;
        radii[4] = (m_diameter + 1E-9) * 0.5;
        return radii;
    }

    /**
     * Return the number fractions of the PSD
     * @return array of number fractions
     */
    @Override
    public double[] numberFractions() {
        double[] numberFractions = new double[5];
        numberFractions[0] = 0.0;
        numberFractions[1] = 0.0;
        numberFractions[2] = 1.0;
        numberFractions[3] = 0.0;
        numberFractions[4] = 0.0;
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
        Monodisperse copy = new Monodisperse();
        copy.m_diameter = m_diameter;
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
        writer.write(String.valueOf(m_diameter));
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
            m_diameter = Utils.StringToDouble(reader.readLine());
        } else {
            //if no reader code found, inform the user/developer
            JOptionPane.showMessageDialog(null, "The monodisperse particle size distribution is of version " + m_version + ", which can't be read by Hamaker version " + HamakerInfo.version());
        }

    }
    
    @Override
    public ArrayList<PlotVariable> plotVariables(String namePrefix, String idPrefix) {
        ArrayList<PlotVariable> vars = new ArrayList<>();
        
        vars.add(new PlotVariable(idPrefix, namePrefix, "diameter", "Diameter", "nm", 5.0E-8, 5.0E-7, 1.0E9, "0.0"));
        
        return vars;
    }


    @Override
    public double getPlotVariableValue(String id) {
        if (id.equals("diameter")) {
            return m_diameter;
        } else {
            System.out.println("Monodisperse, unknown plot variable: " + id);
        }

        return PlotVariable.kUnknownPlotVariableID;
    }

    @Override
    public void setPlotVariableValue(String id, double value) {
        if (id.equals("diameter")) {
            m_diameter = value;
        } else {
            System.out.println("Monodisperse, unknown plot variable: " + id);
        }
    }

    private void composePanel() {
        o_diameterField = new JFormattedTextField();

        m_panel.setLayout(new GridLayout(0, 3));
        m_panel.add(new JLabel("Particle Diamater:"));
        m_panel.add(o_diameterField);
        m_panel.add(new JLabel("nm"));
        
        o_diameterField.getDocument().addDocumentListener(new DocumentListener() {
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
                System.out.println("HERE3");
                update();
            }
            
            void update() {
                retrieveValues();
                m_panel.firePropertyChange("size", 0.0, 1.0);
            }
        
        });
        
        m_panel.validate();
        
        savePrefSize();
    }
        
    @Override
    public void populateValues() {
        o_diameterField.setText(new DecimalFormat("0.0").format(m_diameter * 1E9));
    }

    @Override
    public void retrieveValues() {
        m_diameter = Utils.StringToDouble(o_diameterField.getText()) * 1E-9;
    }

    @Override
    public double getRv(double fraction) {
        return m_diameter * 0.5;
    }

    @Override
    public double getDv(double fraction) {
        return m_diameter;
    }

    @Override
    public double getRvmean() {
        return m_diameter * 0.5;
    }

    @Override
    public double getDvmean() {
        return m_diameter;
    }

}
