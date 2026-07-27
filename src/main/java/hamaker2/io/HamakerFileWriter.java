package hamaker2.io;

import hamaker2.HamakerInfo;
import hamaker2.Medium;
import hamaker2.Particle;
import hamaker2.Project;
import hamaker2.Serie;
import hamaker2.Utils;
import hamaker2.Yodel;
import hamaker2.models.dispersion.DispersionInteractionModel;
import hamaker2.models.electrostatic.ElectrostaticInteractionModel;
import hamaker2.models.misc.MiscInteractionModel;
import hamaker2.models.steric.StericInteractionModel;
import hamaker2.particleSizeDistribution.ParticleSizeDistribution;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Class to write files in the current version to disk
 *
 * @author asulrich
 */
public class HamakerFileWriter {

    /**
     * Method that writes the given to project to the given file
     *
     * @param project Project to be saved to disk
     * @param file File to be written to
     * @return
     */
    public static boolean writeFile(Project project, File file) {

        boolean success = true;

        //open the stream
        BufferedWriter output;
        try {
            output = new BufferedWriter(new FileWriter(file));

            output.write(HamakerInfo.version());
            output.newLine();

            output.write(project.getTitle());
            output.newLine();

            output.write(String.valueOf(project.getSelectedSerieIndex()));
            output.newLine();

            output.write(project.getPlotType().name());
            output.newLine();
            output.write(String.valueOf(project.getSelectedVariable1().getID()));
            output.newLine();
            output.write(String.valueOf(project.getSelectedVariable2().getID()));
            output.newLine();

            output.write(String.valueOf(project.getPrimaryAxisMin()));
            output.newLine();
            output.write(String.valueOf(project.getPrimaryAxisMax()));
            output.newLine();

            output.write(String.valueOf(project.getSecondaryAxisMin()));
            output.newLine();
            output.write(String.valueOf(project.getSecondaryAxisMax()));
            output.newLine();

            output.write(String.valueOf(project.getInteractionAxisMin()));
            output.newLine();
            output.write(String.valueOf(project.getInteractionAxisMax()));
            output.newLine();

            output.write(String.valueOf(project.getSeriesCount()));
            output.newLine();
            for (int i = 0; i < project.getSeriesCount(); i++) {
                output.write("***SERIE_START***");
                output.newLine();

                writeSerie(project.getSerie(i), output);

                output.write("***SERIE_END***");
                output.newLine();
            }

            output.close();

        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }

    private static boolean writeSerie(Serie s, BufferedWriter output) {
        boolean success = true;

        try {

            output.write(s.getName());
            output.newLine();

            output.write(String.valueOf(s.getColor().getRGB()));
            output.newLine();
            output.write(String.valueOf(s.getGridColor().getRGB()));
            output.newLine();
            output.write(String.valueOf(s.getVisible()));
            output.newLine();

            output.write(s.getDistanceAsText());
            output.newLine();
            output.write(String.valueOf(s.getTemperature()));
            output.newLine();
            output.write(String.valueOf(s.getHeterogeneous()));
            output.newLine();

            output.write("***GEOMETRY_CLASS***");
            output.newLine();
            output.write(s.getSelectedGeometryClass().toString());
            output.newLine();

            writeParticle(s.getParticle1(), output);
            writeParticle(s.getParticle2(), output);
            writeMedium(s.getMedium(), output);

            output.write(s.getSelectedDispersionModelID());
            output.newLine();
            output.write(String.valueOf(s.getAdditionalParametersDispersionModelCount()));
            output.newLine();
            for (int i = 0; i < s.getDispersionModelCount(); i++) {
                DispersionInteractionModel m = s.getDispersionModel(i);
                if (m.additionalParameters()) {
                    output.write("***DISPERSION_MODEL_START***");
                    output.newLine();
                    output.write(m.name());
                    output.newLine();

                    m.save(output);

                    output.write("***DISPERSION_MODEL_END***");
                    output.newLine();
                }
            }

            output.write(s.getSelectedElectrostaticModelID());
            output.newLine();
            output.write(String.valueOf(s.getAdditionalParametersElectrostaticModelCount()));
            output.newLine();
            for (int i = 0; i < s.getElectrostaticModelCount(); i++) {
                ElectrostaticInteractionModel m = s.getElectrostaticModel(i);
                if (m.additionalParameters()) {
                    output.write("***ELECTROSTATIC_MODEL_START***");
                    output.newLine();
                    output.write(m.name());
                    output.newLine();

                    m.save(output);

                    output.write("***ELECTROSTATIC_MODEL_END***");
                    output.newLine();
                }
            }

            output.write(s.getSelectedStericModelID());
            output.newLine();
            output.write(String.valueOf(s.getAdditionalParametersStericModelCount()));
            output.newLine();
            for (int i = 0; i < s.getStericModelCount(); i++) {
                StericInteractionModel m = s.getStericModel(i);
                if (m.additionalParameters()) {
                    output.write("***STERIC_MODEL_START***");
                    output.newLine();
                    output.write(m.name());
                    output.newLine();

                    m.save(output);

                    output.write("***STERIC_MODEL_END***");
                    output.newLine();
                }
            }

            output.write(String.valueOf(s.getAdditionalParametersMiscModelCount()));
            output.newLine();
            for (int i = 0; i < s.getMiscModelCount(); i++) {
                MiscInteractionModel m = s.getMiscModel(i);
                if (m.additionalParameters()) {
                    output.write("***MISC_MODEL_START***");
                    output.newLine();
                    output.write(m.name());
                    output.newLine();

                    m.save(output);

                    output.write("***MISC_MODEL_END***");
                    output.newLine();
                }
            }

            if (!writeYodel(s.getYodel(), output)) {
                return false;
            }
            
            s.setNeedsSave(false);

        } catch (IOException ex) {
            Logger.getLogger(HamakerFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }

    @SuppressWarnings("empty-statement")
    public static boolean writeParticle(Particle p, BufferedWriter output) {
        boolean success = true;
        try {
            output.write("***PARTICLE_START***");
            output.newLine();
            output.write(String.valueOf(p.getHamakerConstant()));
            output.newLine();
            output.write(String.valueOf(p.getDensity()));
            output.newLine();

            output.write(p.getSelectedSizeDistribution().id());
            output.newLine();
            output.write(String.valueOf(p.getAdditionalParametersParticleSizeDistributionCount()));
            output.newLine();
            for (int i = 0; i < p.getParticleSizeDistributionCount(); i++) {
                ParticleSizeDistribution d = p.getParticleSizeDistribution(i);
                if (d.hasParameters()) {
                    output.write("***PARTICLE_SIZE_DISTRIBUTION_START***");
                    output.newLine();
                    output.write(d.name());
                    output.newLine();
                    d.save(output);
                    output.write("***PARTICLE_SIZE_DISTRIBUTION_END***");
                    output.newLine();
                }
            }

            output.write(String.valueOf(p.getZetaPotential()));
            output.newLine();
            output.write(String.valueOf(p.getElectrostaticOrigin()));
            output.newLine();
            output.write("***PARTICLE_END***");
            output.newLine();

            p.setNeedsSave(false);

        } catch (IOException ex) {
            Logger.getLogger(HamakerFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }

    public static boolean writeMedium(Medium m, BufferedWriter output) {
        boolean success = true;

        try {

            output.write("***MEDIUM_START***");
            output.newLine();
            output.write(String.valueOf(m.getDensity()));
            output.newLine();
            output.write(String.valueOf(m.getDielectricConstant()));
            output.newLine();

            output.write(String.valueOf(m.getNumElectrolyteComponents()));
            output.newLine();
            for (int i = 0; i < m.getNumElectrolyteComponents(); i++) {
                output.write(m.getElectrolyteComponentLabel(i));
                output.newLine();
                output.write(String.valueOf(m.getElectrolyteComponentValence(i)));
                output.newLine();
                output.write(String.valueOf(m.getElectrolyteComponentConcentration(i)));
                output.newLine();
            }

            output.write("***MEDIUM_END***");
            output.newLine();

            m.setNeedsSave(false);

        } catch (IOException ex) {
            Logger.getLogger(HamakerFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }

    public static boolean writeYodel(Yodel y, BufferedWriter output) {
        boolean success = true;

        try {

            output.write("***YODEL_START***");
            output.newLine();

            output.write(y.getModel().getId());
            output.newLine();
            output.write(String.valueOf(y.getPercolationTreshold()));
            output.newLine();
            output.write(String.valueOf(y.getMaxPackingFraction()));
            output.newLine();
            output.write(y.getVolumeIncrementFunction().getId());
            output.newLine();
            output.write(y.getNormalizationRadius().getId());
            output.newLine();
            output.write(Utils.BooleanToString(y.getHarmonicCurvature()));
            output.newLine();
            output.write(String.valueOf(y.getCurvatureRadius()));
            output.newLine();
            output.write(Utils.BooleanToString(y.getAutoAttractiveForce()));
            output.newLine();
            output.write(String.valueOf(y.getManualAttractiveHamaker()));
            output.newLine();
            output.write(String.valueOf(y.getManualAttractiveSeparation()));
            output.newLine();
            output.write(String.valueOf(y.getDefaultVolumeFraction()));
            output.newLine();

            output.write("***YODEL_END***");
            output.newLine();

            y.setNeedsSave(false);

        } catch (IOException ex) {
            Logger.getLogger(HamakerFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }
}
