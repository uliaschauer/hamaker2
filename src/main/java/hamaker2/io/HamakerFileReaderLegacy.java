/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.io;

import hamaker2.Medium;
import hamaker2.Particle;
import hamaker2.Project;
import hamaker2.Project.PlotType;
import hamaker2.Serie;
import hamaker2.Utils;
import hamaker2.models.GeometryClass;
import hamaker2.models.dispersion.DispersionInteractionModel;
import hamaker2.models.electrostatic.ElectrostaticInteractionModel;
import hamaker2.models.misc.MiscInteractionModel;
import hamaker2.models.steric.StericInteractionModel;
import hamaker2.particleSizeDistribution.Monodisperse;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements reading of pre 2.3 Hamaker files (i.e. without a
 * version tag on the first line). This implementation was replaced by
 * version-controlled file reading in Hamaker 2.3.
 *
 * @author Uli Aschauer
 */
public class HamakerFileReaderLegacy implements HamakerFileReaderInterface {

    /**
     * Method to read a pre 2.3.0 Hamaker file into the given project
     *
     * @param project The Data object to load data into
     * @param file The File to load data from
     * @return true on success, false on failure
     */
    @Override
    public boolean readFile(Project project, File file) {
        boolean success = true;

        if (file.exists() && file.isFile()) {
            try {
                try (BufferedReader input = new BufferedReader(new java.io.FileReader(file))) {
                    project.setTitle(input.readLine());

                    project.setSelectedSerieIndex(Utils.StringToInt(input.readLine()));

                    switch (Utils.StringToInt(input.readLine())) {
                        case 0:
                            project.setPlotType(PlotType.Plot2D);
                            break;
                        case 1:
                            project.setPlotType(PlotType.Plot3D);
                            break;
                    }
                    project.setSelectedVariable1Index(Utils.StringToInt(input.readLine()));
                    project.setSelectedVariable2Index(Utils.StringToInt(input.readLine()));

                    project.getSelectedVariable1().setMin(Utils.StringToDouble(input.readLine()));
                    project.getSelectedVariable1().setMax(Utils.StringToDouble(input.readLine()));
                    project.getSelectedVariable2().setMin(Utils.StringToDouble(input.readLine()));
                    project.getSelectedVariable2().setMax(Utils.StringToDouble(input.readLine()));
                    project.setInteractionAxisMin(Utils.StringToDouble(input.readLine()));
                    project.setInteractionAxisMax(Utils.StringToDouble(input.readLine()));

                    //remove all series
                    project.deleteAllSeries();

                    int series_count = Utils.StringToInt(input.readLine());
                    for (int i = 0; i < series_count; i++) {
                        if (!input.readLine().equals("***SERIE_START***")) {
                            System.out.println("Error while reading series data: start tag does not match!");
                            success = false;
                        }
                        Serie newSerie = new Serie();
                        if (!loadSerie(newSerie, input)) {
                            success = false;
                        }
                        project.addSerie(newSerie);

                        if (!input.readLine().equals("***SERIE_END***")) {
                            System.out.println("Error while reading series data: end tag does not match!");
                            success = false;
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return success;
    }

    /**
     *
     * @param s The Serie object to load data into
     * @param input The BufferedReader to read from
     * @return true on success, false on failure
     */
    private boolean loadSerie(Serie s, BufferedReader input) {
        try {
            s.setName(input.readLine());
            s.setColor(Color.decode(input.readLine()));
            s.setGridColor(Color.decode(input.readLine()));
            s.setVisible(Boolean.valueOf(input.readLine()));
            s.setDistance(input.readLine());
            s.setTemperature(Utils.StringToDouble(input.readLine()));
            s.setHeterogeneous(Boolean.valueOf(input.readLine()));

            //check if geometry class is written (2.2 onwards)
            input.mark(1024);
            if (input.readLine().equals("***GEOMETRY_CLASS***")) {

                String name = input.readLine();

                for (GeometryClass geoClass : GeometryClass.getList()) {
                    if (geoClass.toString().equals(name)) {
                        s.setSelectedGeometryClass(geoClass);
                    }
                }
            } else {
                input.reset();
            }

            if (!loadParticle(s.getParticle1(), input)) {
                return false;
            }
            if (!loadParticle(s.getParticle2(), input)) {
                return false;
            }
            if (!loadMedium(s.getMedium(), input)) {
                return false;
            }

            s.setSelectedDispersionModel(s.getDispersionModelWithID(input.readLine()));
            int num_disp = Utils.StringToInt(input.readLine());
            for (int i = 0; i < num_disp; i++) {
                if (!input.readLine().equals("***DISPERSION_MODEL_START***")) {
                    System.out.println("Error while reading dispersion model data: start tag does not match!");
                    return false;
                }
                String name = input.readLine();
                for (int j = 0; j < s.getDispersionModelCount(); j++) {
                    DispersionInteractionModel m = s.getDispersionModel(j);
                    if (m.name().equals(name) && m.additionalParameters()) {
                        m.load(input);
                    }
                }

                if (!input.readLine().equals("***DISPERSION_MODEL_END***")) {
                    System.out.println("Error while reading dispersion model data: end tag does not match!");
                    return false;
                }
            }

            s.setSelectedElectrostaticModel(s.getElectrostaticModelWithID(input.readLine()));
            int num_es = Utils.StringToInt(input.readLine());
            for (int i = 0; i < num_es; i++) {
                if (!input.readLine().equals("***ELECTROSTATIC_MODEL_START***")) {
                    System.out.println("Error while reading electrostatic model data: start tag does not match!");
                    return false;
                }
                String name = input.readLine();
                for (int j = 0; j < s.getElectrostaticModelCount(); j++) {
                    ElectrostaticInteractionModel m = s.getElectrostaticModel(j);
                    if (m.name().equals(name) && m.additionalParameters()) {
                        m.load(input);
                    }
                }

                if (!input.readLine().equals("***ELECTROSTATIC_MODEL_END***")) {
                    System.out.println("Error while reading electrostatic model data: end tag does not match!");
                    return false;
                }
            }

            s.setSelectedStericModel(s.getStericModelWithID(input.readLine()));
            int num_ster = Utils.StringToInt(input.readLine());
            for (int i = 0; i < num_ster; i++) {
                if (!input.readLine().equals("***STERIC_MODEL_START***")) {
                    System.out.println("Error while reading steric model data: start tag does not match!");
                    return false;
                }
                String name = input.readLine();
                for (int j = 0; j < s.getStericModelCount(); j++) {
                    StericInteractionModel m = s.getStericModel(j);
                    if (m.name().equals(name) && m.additionalParameters()) {
                        m.load(input);
                    }
                }

                if (!input.readLine().equals("***STERIC_MODEL_END***")) {
                    System.out.println("Error while reading steric model data: end tag does not match!");
                    return false;
                }
            }

            int num_misc = Utils.StringToInt(input.readLine());
            for (int i = 0; i < num_misc; i++) {
                if (!input.readLine().equals("***MISC_MODEL_START***")) {
                    System.out.println("Error while reading misc model data: start tag does not match!");
                    return false;
                }
                String name = input.readLine();
                for (int j = 0; j < s.getMiscModelCount(); j++) {
                    MiscInteractionModel m = s.getMiscModel(j);
                    if (m.name().equals(name) && m.additionalParameters()) {
                        m.load(input);
                    }
                }

                if (!input.readLine().equals("***MISC_MODEL_END***")) {
                    System.out.println("Error while reading misc model data: end tag does not match!");
                    return false;
                }
            }

            s.setNeedsSave(false);

        } catch (IOException ex) {
            Logger.getLogger(HamakerFileReaderLegacy.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;

    }

    /**
     *
     * @param p The Particle object to be loaded
     * @param input The BufferedReader to read from
     * @return true on success, false on failure
     */
    private boolean loadParticle(Particle p, BufferedReader input) {
        try {
            if (!input.readLine().equals("***PARTICLE_START***")) {
                System.out.println("Error while reading particle data: start tag does not match!");
                return false;
            }

            p.setHamakerConstant(Utils.StringToDouble(input.readLine()));
            p.setDensity(Utils.StringToDouble(input.readLine()));
            p.setSelectedSizeDistributionID("size_distribution_monodisperse");
            ((Monodisperse) (p.getSelectedSizeDistribution())).setSize(Utils.StringToDouble(input.readLine()));
            p.setZetaPotential(Utils.StringToDouble(input.readLine()));
            p.setElectrostaticOrigin(Utils.StringToDouble(input.readLine()));

            if (!input.readLine().equals("***PARTICLE_END***")) {
                System.out.println("Error while reading particle data: end tag does not match!");
                return false;
            }

            p.setNeedsSave(false);

        } catch (IOException ex) {
            Logger.getLogger(HamakerFileReaderLegacy.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    /**
     *
     * @param m The Medium object to load data into
     * @param input The BufferedReader to read from
     * @return true on success, false on failure
     */
    private boolean loadMedium(Medium m, BufferedReader input) {
        try {
            if (!input.readLine().equals("***MEDIUM_START***")) {
                System.out.println("Error while reading medium data: start tag does not match!");
                return false;
            }

            m.setDensity(Utils.StringToDouble(input.readLine()));
            m.setDielectricConstant(Utils.StringToDouble(input.readLine()));

            int count = Utils.StringToInt(input.readLine());
            m.deleteAllElectrolyteComponents();
            for (int i = 0; i < count; i++) {
                String label = input.readLine();
                double valence = Utils.StringToDouble(input.readLine());
                double conc = Utils.StringToDouble(input.readLine());

                m.addElectrolyteComponent(label, valence, conc);
            }

            if (!input.readLine().equals("***MEDIUM_END***")) {
                System.out.println("Error while reading medium data: end tag does not match!");
                return false;
            }

            m.setNeedsSave(false);

        } catch (IOException ex) {
            Logger.getLogger(HamakerFileReaderLegacy.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }
}
