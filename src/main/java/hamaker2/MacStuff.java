/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2;

/**
 * Class that implements Mac OS specific functionality
 *
 * @author uli
 */
class MacStuff {

    /**
     * Method to set the quit behavior
     */
    public static void setQuitBehaviour() {
        //com.apple.eawt.Application.getApplication().setQuitStrategy(com.apple.eawt.QuitStrategy.CLOSE_ALL_WINDOWS);
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
    }

}
