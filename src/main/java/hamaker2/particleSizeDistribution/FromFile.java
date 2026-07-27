/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.particleSizeDistribution;

import hamaker2.Utils;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

/**
 * Class implements loading a PSD from a file on disk
 * @author uli
 */
public class FromFile extends AbstractParticleSizeDistribution {

    private String m_fileName;
    private int m_numPoints;
    private double[] m_diameter, m_numberFraction;
    private JButton o_loadButton;
    private JLabel o_nameField;

    /**
     * Initialize the PSD as monodisperse with size 100 nm
     */
    public FromFile() {
        m_version = "1.0";
        m_fileName = "";
        m_numPoints = 1;
        m_diameter = new double[1];
        m_diameter[0] = 100E-9;
        m_numberFraction = new double[1];
        m_numberFraction[0] = 1.0;
        composePanel();
    }

    @Override
    public String id() {
        return "fromfile";
    }

    @Override
    public String name() {
        return "From File";
    }

    @Override
    public String reference() {
        return "Load particle size distribution from data file";
    }

    @Override
    public int numPoints() {
        return m_numPoints;
    }

    /**
     * Return the diameters of this PSD
     * @return array of diameters
     */
    @Override
    public double[] diameters() {
        return m_diameter;
    }
    
    /**
     * Return the radii of this PSD
     * @return array of radii
     */
    @Override
    public double[] radii() {
        double[] radii = new double[m_numPoints];
        for (int i=0;i<m_numPoints;i++) {
            radii[i] = m_diameter[i] * 0.5;
        }
        return radii;
    }

    /**
     * Return the number fractions of the PSD
     * @return array of number fractions
     */
    @Override
    public double[] numberFractions() {
        return m_numberFraction;
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
        FromFile copy = new FromFile();

        copy.m_fileName = m_fileName;
        copy.m_numPoints = m_numPoints;
        copy.m_diameter = new double[m_numPoints];
        copy.m_numberFraction = new double[m_numPoints];
        for (int i = 0; i < m_numPoints; i++) {
            copy.m_diameter[i] = m_diameter[i];
            copy.m_numberFraction[i] = m_numberFraction[i];
        }

        return copy;
    }

    @Override
    public boolean hasParameters() {
        return true;
    }

    @Override
    public void save(BufferedWriter writer) throws IOException {
        writer.write("PSD From file " + m_version);
        writer.newLine();
        writer.write(m_fileName);
        writer.newLine();
        writer.write(String.valueOf(m_numPoints));
        writer.newLine();
        for (int i=0;i<m_numPoints;i++) {
            writer.write(m_diameter[i] + " " + m_numberFraction[i]);
            writer.newLine();
        }
    }

    @Override
    public void load(BufferedReader reader) throws IOException {
        String line;
        
        line = reader.readLine();
        if (!line.startsWith("PSD From file") || !line.endsWith(m_version)) {
            System.out.println("Error in FromFile::load. Version line mismatch!");
            return;
        }
        
        m_fileName = reader.readLine();
        
        m_numPoints = Utils.StringToInt(reader.readLine());
        
        m_diameter = new double[m_numPoints];
        m_numberFraction = new double[m_numPoints];
        
        for (int i=0;i<m_numPoints;i++) {
            line = reader.readLine();
            String[] split = line.split(" ");
            if (split.length != 2) {
                System.out.println("Error in FromFile::load. Line contains != 2 elements");
                return;
            }
            m_diameter[i] = Utils.StringToDouble(split[0]);
            m_numberFraction[i] = Utils.StringToDouble(split[1]);
        }
        
    }

    private void composePanel() {
        o_loadButton = new JButton("Load...");
        o_loadButton.setToolTipText("Load PSD from file");
        o_nameField = new JLabel("");

        m_panel.setLayout(new GridLayout(0, 2));
        m_panel.add(o_loadButton);
        m_panel.add(o_nameField);

        o_loadButton.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e)
            {
                FromFileDialog dialog = new FromFileDialog(FromFile.this);
                m_panel.firePropertyChange("size", 0.0, 1.0);
            }
        }); 

        m_panel.validate();

        savePrefSize();
    }

    @Override
    public void populateValues() {
        o_nameField.setText(m_fileName);
    }

    @Override
    public void retrieveValues() {
    }

    public void setData(String fileName, int numPoints, double[] diameters, double[] numberFraction) {
        m_fileName = fileName;
        m_numPoints = numPoints;
        m_diameter = new double[numPoints];
        m_numberFraction = new double[numPoints];
        for (int i=0;i<numPoints;i++) {
            m_diameter[i] = diameters[i];
            m_numberFraction[i] = numberFraction[i];
        }
    }
}
