/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Class that defines an abstract interaction model having plot variables
 * @author uli
 */
public abstract class AbstractInteractionModel extends PlotVariableProvider implements InteractionModel {
    
    /**
     * Routine to return the model name to be shown in the GUI menus
     * @return Model name
     */
    @Override
    public String toString() {
        return name();
    }
    
    /**
     * Implementation for models that do not have a more dialog. For all others override this function.
     *
     * @return additional parameters
     */
    @Override
    public boolean additionalParameters() {
        return false;
    }

    /**
     * Implementation for models that do not have a more dialog. For all others override this function.
     */
    @Override
    public void showMoreDialog() {
    }
    
    /**
     * Are there parameters to be written to disk? Implementation for models that do not have data to write to disk. For all others override this function.
     *
     * @return Are there unsaved parameters
     */
    @Override
    public boolean getNeedsSave() {
        return false;
    }

    /**
     * Save the model to disk. Implementation for models that do not have data to write to disk. For all others override this function.
     *
     * @param output Output file
     * @throws java.io.IOException
     */
    @Override
    public void save(BufferedWriter output) throws IOException {
    }

    /**
     * Load the model from disk. Implementation for models that do not have data to write to disk. For all others override this function.
     *
     * @param input Input file
     * @throws java.io.IOException
     */
    @Override
    public void load(BufferedReader input) throws IOException {
    }
}
