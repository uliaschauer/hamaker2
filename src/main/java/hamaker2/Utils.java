package hamaker2;
/*
 * Utils.java
 *
 * Created on 22. august 2007, 13:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * A class for utility conversion functions
 * @author aschauer
 */
public class Utils {
    
    /**
     * Convert a string to double
     * @param s String
     * @return double extracted from string
     */
    public static double StringToDouble(String s) {
        if (s.equals("")) {
            return 0.0;
        }
        String tmp = s.replace(",", ".");
        return Double.parseDouble(tmp);
    }
    
    /**
     * Convert a string to int
     * @param s String
     * @return int extracted from string
     */
    public static int StringToInt(String s) {
        return Integer.parseInt(s);
    }
    
    /**
     * Convert a string to boolean
     * @param s String
     * @return boolean extracted from string
     */
    public static boolean StringToBoolean(String s) {
        return s.equals("1");
    }
    
    /**
     * Convert a boolean to string
     * @param b Boolean
     * @return String of either 0 or 1
     */
    public static String BooleanToString(boolean b) {
        if (b) {
            return "1";
        } else {
            return "0";
        }
    }
}
