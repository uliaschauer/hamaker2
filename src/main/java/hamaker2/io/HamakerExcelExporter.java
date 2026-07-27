/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.io;

import hamaker2.Project;
import hamaker2.Project.PlotType;
import hamaker2.Vector3D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to write current project data in an Excel readable CSV format
 *
 * @author asulrich
 */
public class HamakerExcelExporter {

    /**
     * Static method to write project in excel format to given file
     *
     * @param project Project the data of which is to be exported
     * @param file File the data is to be written to
     * @return Success
     */
    public static boolean exportData(Project project, File file) {

        boolean success = true;

        try {
            try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
                if (project.getPlotType() == PlotType.Plot2D) {
                    //write the header part (the column for the plot variable is blank)
                    output.write(project.getSelectedVariable1Name());
                    output.write("\t");
                    for (int i = 0; i < project.getSeriesCount(); i++) {
                        output.write(project.getSerie(i).getName() + "\t");
                    }
                    output.newLine();

                    int max = 0;
                    if (project.getSeriesCount() > 0) {
                        max = ((ArrayList) (project.getPlotPoints2D().get(0))).size();
                    }
                    for (int p = 0; p < max; p++) {
                        for (int s = 0; s < project.getSeriesCount(); s++) {

                            Vector3D point = ((Vector3D) ((ArrayList) project.getPlotPoints2D().get(s)).get(p));

                            if (s == 0) {
                                output.write(String.valueOf(point.getX()) + "\t");
                            }
                            if (s < project.getSeriesCount() - 1) {
                                output.write(String.valueOf(point.getY()) + "\t");
                            } else {
                                output.write(String.valueOf(point.getY()));
                            }
                        }

                        output.newLine();
                    }
                } else if (project.getPlotType() == PlotType.Plot3D) {
                    for (int s = 0; s < project.getSeriesCount(); s++) {
                        ArrayList points = (ArrayList) project.getPlotPoints3D().get(s);

                        //write header part
                        output.write(project.getSerie(s).getName());
                        output.newLine();
                        output.write("\t\t");
                        output.write(project.getSelectedVariable1Name());
                        output.newLine();
                        output.write("\t\t");
                        for (Object point : points) {
                            Vector3D p = (Vector3D) ((ArrayList) point).get(0);
                            output.write(String.valueOf(p.getX()));
                            output.write("\t");
                        }
                        output.newLine();

                        for (int l = 0; l < points.size(); l++) {
                            ArrayList line = (ArrayList) points.get(l);

                            if (l == 0) {
                                output.write(project.getSelectedVariable2Name());
                            }

                            output.write("\t");

                            output.write(String.valueOf(((Vector3D) line.get(0)).getY()));
                            output.write("\t");

                            for (Object line1 : line) {
                                Vector3D pt = (Vector3D) line1;
                                output.write(String.valueOf(pt.getZ()));
                                output.write("\t");
                            }

                            output.newLine();
                        }

                        output.newLine();
                        output.newLine();
                    }

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;

    }
}
