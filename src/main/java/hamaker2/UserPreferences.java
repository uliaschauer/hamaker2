/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2;

import java.util.prefs.Preferences;

/**
 * Class that implements storing user preferences
 * @author aschauer
 */
public class UserPreferences {

    private final Preferences userPrefs;

    /**
     * Initializes user preferences with stored values
     */
    public UserPreferences() {
        userPrefs = Preferences.userNodeForPackage(
                UserPreferences.class);
    }

    /**
     * Get the default path user preference
     * @return Default path preference or the home directory if not set
     */
    public String getDefaultPath() {
        return userPrefs.get("default_path", System.getProperty("user.home"));
    }

    /**
     * Set the default path user preference
     * @param path Default path preference
     */
    public void setDefaultPath(String path) {
        userPrefs.put("default_path", path);
    }
}
