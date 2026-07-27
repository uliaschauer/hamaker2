/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2;

/**
 * This interface defines the callback routines to be used for updating the
 * progress bar
 *
 * @author uli
 */
public interface ComputeListener {

    /**
     * Update the progress bar to the given percentage
     *
     * @param percentage Percentage complete
     */
    void notifyProgressUpdate(int percentage);

    /**
     * Reset the progress bar to the idle state
     */
    void notifyComplete();
}
