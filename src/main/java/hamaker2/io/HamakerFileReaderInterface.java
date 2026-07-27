/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.io;

import hamaker2.Project;
import java.io.File;

/**
 * Interface that reader classes have to adopt
 *
 * @author asulrich
 */
public interface HamakerFileReaderInterface {

    /**
     * Method to read a Hamaker file into the given project
     *
     * @param project Project that data is read into
     * @param file File that data is read from
     * @return Success
     */
    public boolean readFile(Project project, File file);
}
