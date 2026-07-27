/*
 * Steric_Bergstrom.java
 *
 * Created on March 24, 2007, 9:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2.models.steric;

import hamaker2.*;
import hamaker2.models.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;

/**
 * This class implements the 'zero' steric interaction model
 *
 * @author Ulrich Aschauer
 * @version 2.2
 * @since 2.0
 */
public class Steric_None extends AbstractInteractionModel implements StericInteractionModel {

    final private String m_version = "1.0";

    /**
     * Standard constructor for this class. No parameters since this is an empty
     * model
     */
    public Steric_None() {
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "steric_none";
    }

    /**
     * Return the name of the model.
     *
     * @return Name of the model
     */
    @Override
    public String name() {
        return "None";
    }

    /**
     * There is no reference for an empty model to return an empty string
     *
     * @return Literature reference to the model
     */
    @Override
    public String reference() {
        return "";
    }

    /**
     * Return the version information for this model
     *
     * @return version string
     */
    @Override
    public String version() {
        return m_version;
    }

    /**
     * Make a deep copy of the model
     *
     * @return Deep copy of model
     */
    @Override
    public Steric_None duplicate() {

        Steric_None copy = new Steric_None();

        return copy;
    }

    /**
     * Return the list of all implemented geometry classes
     *
     * @return list of all supported geometry classes
     */
    @Override
    public ArrayList<GeometryClass> ImplementedGeometryClasses() {
        return GeometryClass.getList();
    }
    
    /**
     * Return the interaction potential for a homogeneous series
     *
     * @param serie Series to be computed
     * @param size particle size
     * @return Interaction potential in kB/T
     */
    @Override
    public double interactionPotential(Serie serie, double size) {
        return interactionPotential(serie, size, size);
    }

    /**
     * Return the interaction potential
     *
     * @param serie the series for which the potential is returned
     * @param size1 Size of the first particle
     * @param size2 Size of the second particle
     * @return interaction potential in units of kT
     */
    @Override
    public double interactionPotential(Serie serie, double size1, double size2) {
        return 0.0;
    }

    /**
     * Return the interaction force for a homogeneous series
     *
     * @param serie Series to be computed
     * @param size particle size
     * @return Interaction force in kB/(T*nm)
     */
    @Override
    public double interactionForce(Serie serie, double size) {
        return interactionForce(serie, size, size);
    }

    /**
     * Return the interaction potential for a heterogeneous series
     *
     * @param serie Series to be computed
     * @param size1 Size of particle 1
     * @param size2 Size of particle 2
     * @return Interaction force in kB/(T*nm)
     */
    @Override
    public double interactionForce(Serie serie, double size1, double size2) {
        return 0.0;
    }
}
