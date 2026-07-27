/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.io;

import hamaker2.HamakerInfo;
import hamaker2.Project;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Main entry point for file reading that will select the appropriate versioned
 * reader or fall back to the legacy reader
 *
 * @author asulrich
 */
public class HamakerFileReader {

    /**
     * Static method to read a file into the given project
     *
     * @param project Project that data is read into
     * @param file File that data is read from
     * @return Success
     */
    public static boolean readFile(Project project, File file) {

        if (file.exists() && file.isFile()) {
            try {
                int major;
                int minor;
                int update;
                try (BufferedReader input = new BufferedReader(new java.io.FileReader(file))) {
                    String version = input.readLine();
                    version = version.replaceAll("\\.", " ");
                    Scanner s = new Scanner(version);
                    major = -1;
                    minor = -1;
                    update = -1;
                    if (s.hasNextInt()) {
                        major = s.nextInt();
                        if (s.hasNextInt()) {
                            minor = s.nextInt();
                            if (s.hasNextInt()) {
                                update = s.nextInt();
                            }
                        }
                    }
                }

                if (major == -1 || minor == -1 || update == -1) {
                    if (!new HamakerFileReaderLegacy().readFile(project, file)) {
                        return false;
                    }
                } else {

                    Class reader = null;
                    try {
                        reader = Class.forName("hamaker2.HamakerFileReader" + major + minor + update);
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "This file was written by Hamaker version " + major + "." + minor + "." + update + ", which can't be read by Hamaker " + HamakerInfo.version());
                    }

                    if (reader != null) {
                        try {
                            if (!((HamakerFileReaderInterface) reader.newInstance()).readFile(project, file)) {
                                return false;
                            }
                        } catch (InstantiationException | IllegalAccessException ex) {
                            Logger.getLogger(HamakerFileReader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
