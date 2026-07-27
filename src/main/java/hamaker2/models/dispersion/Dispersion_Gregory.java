/*
 * Dispersion_Gregory.java
 *
 * Created on March 24, 2007, 7:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2.models.dispersion;

import hamaker2.*;
import hamaker2.models.*;
import java.util.ArrayList;

/**
 * Implements the retarded dispersion model as defined in J. Gregory, Journal of
 * Colloid and Interface Science, 83(1), 138-145, 1981
 *
 * @author uli
 */
public class Dispersion_Gregory extends AbstractInteractionModel implements DispersionInteractionModel {

    final private String m_version = "1.0";

    /**
     * Creates a new instance of Dispersion_Gregory
     */
    public Dispersion_Gregory() {
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "dispersion_gregory";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Gregory";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "J. Gregory, Journal of Colloid and Interface Science, 83(1), 138-145, 1981";
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
    public Dispersion_Gregory duplicate() {
        //There are no parameters, to just return a new object
        return new Dispersion_Gregory();
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
            return interactionPotential_Sphere_Plate(serie, size1, size2);
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
        double b = 5.32;
        double landa = 100E-9;

        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            AH = serie.getParticle1().getHamakerConstant();
        }

        double term = b * D / landa * Math.log(1.0 + landa / (b * D));

        return -(AH * a1 * a2) / (6.0 * (a1 + a2) * D) * (1.0 - term);
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1, double size2) {
        double AH;
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        double b = 5.32;
        double landa = 100E-9;

        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            AH = serie.getParticle1().getHamakerConstant();
        }

        double term = b * D / landa * Math.log(1.0 + landa / (b * D));

        return -(AH * a1) / (6.0 * D) * (1.0 - term);
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
        double b = 5.32;
        double landa = 100E-9;

        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            AH = serie.getParticle1().getHamakerConstant();
        }

        double term1 = 1.0 / (D * (landa / (b * D) + 1));
        double term2 = b / landa * Math.log(landa / (b * D) + 1);
        double term3 = 1 / D * (1 - b * D / landa * Math.log(landa / (b * D) + 1));

        return -(AH * a1 * a2) / (6.0 * (a1 + a2) * D) * (term1 - term2 - term3);
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {

        double AH;
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        double b = 5.32;
        double landa = 100E-9;

        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            AH = serie.getParticle1().getHamakerConstant();
        }

        double term1 = 1.0 / (D * (landa / (b * D) + 1));
        double term2 = b / landa * Math.log(landa / (b * D) + 1);
        double term3 = 1 / D * (1 - b * D / landa * Math.log(landa / (b * D) + 1));

        return -(AH * a1) / (6.0 * D) * (term1 - term2 - term3);
    }
}
