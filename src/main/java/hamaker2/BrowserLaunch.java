/*
 * BrowserLaunch.java
 *
 * Created on 13. august 2007, 10:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;

/**
 * This class provides a static routine to launch a given URL in the users
 * default browser
 *
 * @author asulrich
 */
public class BrowserLaunch {

    /**
     * Static routine to open the given URL in the default browser
     *
     * @param url the URL to be opened
     */
    public static void openURL(String url) {
        if (!java.awt.Desktop.isDesktopSupported()) {

            JOptionPane.showMessageDialog(null, "Your system does not seem to support opening a web browser!", "Web browser error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {

            JOptionPane.showMessageDialog(null, "Your system does not seem to support opening a web browser!", "Web browser error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            java.net.URI uri = new java.net.URI(url);
            desktop.browse(uri);
        } catch (URISyntaxException | IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Web browser error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
