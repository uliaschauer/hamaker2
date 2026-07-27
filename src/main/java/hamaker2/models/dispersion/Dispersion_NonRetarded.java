/*
 * Dispersion_NonRetarded.java
 *
 * Created on March 24, 2007, 4:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2.models.dispersion;

import hamaker2.*;
import hamaker2.models.*;
import java.util.ArrayList;

/**
 * Implements the standard non retarded dispersion interaction model
 *
 * @author uli
 */
public class Dispersion_NonRetarded extends AbstractInteractionModel implements DispersionInteractionModel {

    final private String m_version = "1.0";

    /**
     * Creates a new instance of Dispersion_NonRetarded
     */
    public Dispersion_NonRetarded() {
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "dispersion_nonretarded";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Non Retarded";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "H.C. Hamaker, Physica, 4(10), 1058-1072, 1937";
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
    public Dispersion_NonRetarded duplicate() {
        //There are no parameters, to just return a new object
        return new Dispersion_NonRetarded();
    }

    /**
     * Return the list of all implemented geometry classes
     *
     * @return list of all supported geometry classes
     */
    @Override
    public ArrayList<GeometryClass> ImplementedGeometryClasses() {
        return GeometryClass.makeList(GeometryClass.Sphere_Sphere, GeometryClass.Sphere_Plate);
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
     * Return the interaction potential for a heterogeneous series
     *
     * @param serie Series to be computed
     * @param size1 Size of particle 1
     * @param size2 Size of particle 2
     * @return Interaction potential in kB/T
     */
    @Override
    public double interactionPotential(Serie serie, double size1, double size2) {

        if (serie.getSelectedGeometryClass() == GeometryClass.Sphere_Sphere) {
            return interactionPotential_Sphere_Sphere(serie, size1, size2);
        } else if (serie.getSelectedGeometryClass() == GeometryClass.Sphere_Plate) {
            return interactionPotential_Sphere_Plate(serie, size1);
        } else {
            System.out.println("Unknown geometry class");
            return 0;
        }
    }

    private double interactionPotential_Sphere_Sphere(Serie serie, double size1, double size2) {

        double AH;
        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            AH = serie.getParticle1().getHamakerConstant();
        }

        double term1 = 2 * a1 * a2 / (D * D + 2 * a1 * D + 2 * a2 * D);
        double term2 = 2 * a1 * a2 / (D * D + 2 * a1 * D + 2 * a2 * D + 4 * a1 * a2);
        double term3 = Math.log((D * D + 2 * a1 * D + 2 * a2 * D) / (D * D + 2 * a1 * D + 2 * a2 * D + 4 * a1 * a2));

        return -AH / 6.0 * (term1 + term2 + term3);
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1) {

        double AH;
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            AH = serie.getParticle1().getHamakerConstant();
        }

        double term1 = a1 / D;
        double term2 = a1 / (D + 2 * a1);
        double term3 = Math.log(D / (D + 2 * a1));

        return -AH / 6.0 * (term1 + term2 + term3);
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

        if (serie.getSelectedGeometryClass() == GeometryClass.Sphere_Sphere) {
            return interactionForce_Sphere_Sphere(serie, size1, size2);
        } else if (serie.getSelectedGeometryClass() == GeometryClass.Sphere_Plate) {
            return interactionForce_Sphere_Plate(serie, size1, size2);
        } else {
            System.out.println("Unknown geometry class");
            return 0;
        }
    }

    private double interactionForce_Sphere_Sphere(Serie serie, double size1, double size2) {

        double AH;
        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            AH = serie.getParticle1().getHamakerConstant();
        }

        //i'll use the center-center formula since it's so much easier... so
        //convert to center-center formulation
        D += a1 + a2;

        //calculate some reoccuring terms
        double dsq_minus_a1pa2sq = D * D - (a1 + a2) * (a1 + a2);
        double dsq_minus_a1ma2sq = D * D - (a1 - a2) * (a1 - a2);

        double term1 = 2 * D / dsq_minus_a1pa2sq;
        double term2 = 2 * D / dsq_minus_a1ma2sq;
        double term3 = 4 * a1 * a2 * D / (dsq_minus_a1pa2sq * dsq_minus_a1pa2sq);
        double term4 = 4 * a1 * a2 * D / (dsq_minus_a1pa2sq * dsq_minus_a1ma2sq);

        double simple = AH * a1 / (12 * D * D);

        return -AH / 6.0 * (term1 - term2 - term3 - term4);
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {

        double AH;
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            AH = serie.getParticle1().getHamakerConstant();
        }

        double term1 = 1.0 / D;
        double term2 = 1.0 / (D + 2 * a1);
        double term3 = a1 / (D * D);
        double term4 = a1 / (D * D + 4 * D * a1 + 4 * a1 * a1);

        return -AH / 6.0 * (term1 - term2 - term3 - term4);
    }  
}
