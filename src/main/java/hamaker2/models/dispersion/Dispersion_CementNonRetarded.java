/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.models.dispersion;

import hamaker2.*;
import hamaker2.models.*;
import java.util.ArrayList;

/**
 * This model implements the non retarded empirical dispersion interaction model
 * for cement as defined in R.J. Flatt, Cement and Concrete Research, 34,
 * 399-408, 2004
 *
 * @author uli
 */
public class Dispersion_CementNonRetarded extends AbstractInteractionModel implements DispersionInteractionModel {

    final private String m_version = "1.0";

    /**
     * Creates a new instance of Dispersion_CementNonRetarded
     */
    public Dispersion_CementNonRetarded() {
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "dispersion_cementnonretarded";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Cement (Empirical) Non Retarded";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "R.J. Flatt, Cement and Concrete Research, 34, 399-408, 2004";
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
    public Dispersion_CementNonRetarded duplicate() {
        //There are no parameters, to just return a new object
        return new Dispersion_CementNonRetarded();
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
     * @param size1 particle size
     * @return Interaction potential in kB/T
     */
    @Override
    public double interactionPotential(Serie serie, double size1) {
        return interactionPotential(serie, size1, size1);
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

    private double hamakerConstant(Particle particle, Medium medium) {

        double delta_dens = (particle.getDensity() - medium.getDensity()) / 1000; //100 is for kg/m3 to g/cm3 conversion

        double AH = 0.3413 * delta_dens * delta_dens * 1E-20;

        return AH;
    }

    private double interactionPotential_Sphere_Sphere(Serie serie, double size1, double size2) {

        double AH;
        double a1 = size1 * 0.5;
        double a2 = size2 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            double ah1 = hamakerConstant(serie.getParticle1(), serie.getMedium());
            double ah2 = hamakerConstant(serie.getParticle2(), serie.getMedium());
            AH = Math.sqrt(ah1 * ah2);
        } else {
            AH = hamakerConstant(serie.getParticle1(), serie.getMedium());
        }

        double term1 = 2 * a1 * a2 / (D * D + 2 * a1 * D + 2 * a2 * D);
        double term2 = 2 * a1 * a2 / (D * D + 2 * a1 * D + 2 * a2 * D + 4 * a1 * a2);
        double term3 = Math.log((D * D + 2 * a1 * D + 2 * a2 * D) / (D * D + 2 * a1 * D + 2 * a2 * D + 4 * a1 * a2));

        return -AH / 6.0 * (term1 + term2 + term3);
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1, double size2) {

        double AH;
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            double ah1 = hamakerConstant(serie.getParticle1(), serie.getMedium());
            double ah2 = hamakerConstant(serie.getParticle2(), serie.getMedium());
            AH = Math.sqrt(ah1 * ah2);
        } else {
            AH = hamakerConstant(serie.getParticle1(), serie.getMedium());
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
     * @param size1 particle size
     * @return Interaction force in kB/(T*nm)
     */
    @Override
    public double interactionForce(Serie serie, double size1) {
        return interactionForce(serie, size1, size1);
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
            double ah1 = hamakerConstant(serie.getParticle1(), serie.getMedium());
            double ah2 = hamakerConstant(serie.getParticle2(), serie.getMedium());
            AH = Math.sqrt(ah1 * ah2);
        } else {
            AH = hamakerConstant(serie.getParticle1(), serie.getMedium());
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

        return -AH / 6.0 * (term1 - term2 - term3 - term4);
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {

        double AH;
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            double ah1 = hamakerConstant(serie.getParticle1(), serie.getMedium());
            double ah2 = hamakerConstant(serie.getParticle2(), serie.getMedium());
            AH = Math.sqrt(ah1 * ah2);
        } else {
            AH = hamakerConstant(serie.getParticle1(), serie.getMedium());
        }

        double term1 = 1.0 / D;
        double term2 = 1.0 / (D + 2 * a1);
        double term3 = a1 / (D * D);
        double term4 = a1 / (D * D + 4 * D * a1 + 4 * a1 * a1);

        return -AH / 6.0 * (term1 - term2 - term3 - term4);
    }
}
