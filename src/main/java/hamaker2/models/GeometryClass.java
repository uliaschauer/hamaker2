/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hamaker2.models;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that defines a geometry class
 * @author aschauer
 */
public enum GeometryClass {

    Sphere_Sphere("Sphere-Sphere"),
    Sphere_Plate("Sphere-Plate");
    
    //parameters
    private final String m_name;

    GeometryClass(String name) {
        m_name = name;
    }

    @Override
    public String toString() {
        return m_name;
    }
    
    public String getName() {
        return m_name;
    }

    /**
     * Return the list of all geometry classes
     * @return List of all geometry classes
     */
    public static ArrayList<GeometryClass> getList() {
        return makeList(Sphere_Sphere, Sphere_Plate);
    }
    
    public static ArrayList<GeometryClass> makeList(GeometryClass... geometryClasses) {
        ArrayList<GeometryClass> list = new ArrayList<>();
        list.addAll(Arrays.asList(geometryClasses));
        return list;
    }
}
