/*
 * Dispersion_Vincent.java
 *
 * Created on March 24, 2007, 7:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2.models.dispersion;

import hamaker2.*;
import hamaker2.models.*;
import java.util.ArrayList;

/**
 * This class implements the dispersion interaction model as defined in B.
 * Vincent, Journal of Colloid and Interface Science, 42(2), 270-285, 1973
 *
 * @author uli
 */
public class Dispersion_Vincent extends AbstractInteractionModel implements DispersionInteractionModel {

    final private String m_version = "1.0";

    /**
     * Creates a new instance of Dispersion_Vincent
     */
    public Dispersion_Vincent() {
    }

    /**
     * Return the id for this model
     *
     * @return ID
     */
    @Override
    public String id() {
        return "dispersion_vincent";
    }

    /**
     * Return the name of this model
     *
     * @return name
     */
    @Override
    public String name() {
        return "Vincent";
    }

    /**
     * Return the reference for this model
     *
     * @return reference
     */
    @Override
    public String reference() {
        return "B. Vincent, Journal of Colloid and Interface Science, 42(2), 270-285, 1973";
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
    public Dispersion_Vincent duplicate() {
        //There are no parameters, to just return a new object
        return new Dispersion_Vincent();
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
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
            //a1 = serie.getParticle1().getRadius();
            //a2 = serie.getParticle2().getRadius();
        } else {
            AH = serie.getParticle1().getHamakerConstant();
            //a1 = serie.getParticle1().getRadius();
            //a2 = serie.getParticle1().getRadius();
        }

        double landa = 100E-9;

        double A = -AH / 6.0;
        double G = 2 * a1 * a2;
        double H = 2 * a1;
        double I = 2 * a2;
        double J = a1 + a2;

        double xxphxpix = D * D + H * D + I * D;
        double xph = D + H;
        double xpi = D + I;

        double term1 = G / xxphxpix;
        double term2 = G / (xph * xpi);
        double term3 = Math.log(xxphxpix / (xph * xpi));
        double term4 = 2 * Math.PI / landa * G / (2 * (J + D));
        double term5 = (xxphxpix + G) / (2 * G);

        return A * (1.01 * (term1 + term2 + term3) + 1.12 * term4 * (1 + term5 * term3));
    }

    private double interactionPotential_Sphere_Plate(Serie serie, double size1, double size2) {

        double AH;
        double a1 = size1 * 0.5;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
            //a1 = serie.getParticle1().getRadius();
        } else {
            AH = serie.getParticle1().getHamakerConstant();
            //a1 = serie.getParticle1().getRadius();
        }

        double landa = 100E-9;

        double term1 = 1.01 * (a1 / D + a1 / (D + 2 * a1) + Math.log(D / (D + 2 * a1)));
        double term2 = 1.12 * 2 * Math.PI * a1 / landa * (1.0 + (D + a1) / (2 * a1) * Math.log(D / (D + 2 * a1)));

        return -AH / 6.0 * (term1 + term2);
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

        double landa = 100E-9;

        double A = -AH / 6.0;
        double G = 2 * a1 * a2;
        double H = 2 * a1;
        double I = 2 * a2;
        double J = a1 + a2;

        double xxphxpix = D * D + H * D + I * D;
        double xph = D + H;
        double xpi = D + I;

        double log = Math.log(xxphxpix / (xph * xpi));

        double term1_1 = 1 / (2 * G) * (xph + xpi) * log;
        double term1_2 = xpi * xph * (xxphxpix + G);
        double term1_3 = (xph + xpi) / (xpi * xph) - xxphxpix / (xpi * xpi * xph) - xxphxpix / (xpi * xph * xph);
        double term1 = (term1_1 + term1_2 * term1_3 / (2 * G * xxphxpix)) / (D + J);

        double term2 = (1 / (2 * G) * (xxphxpix + G) * log + 1) / ((D + J) * (D + J));
        double term3 = xpi * xph * term1_3 / xxphxpix;
        double term4 = G * (xph + xpi) / (xxphxpix * xxphxpix);
        double term5 = G / (xpi * xpi * xph);
        double term6 = G / (xpi * xph * xph);

        return A * (1.12 * G * Math.PI / landa * (term1 - term2) + 1.01 * (term3 - term4 - term5 - term6));
    }

    private double interactionForce_Sphere_Plate(Serie serie, double size1, double size2) {
        double AH;
        double a1 = size1 * 0.5;
        double a2;
        double D = serie.getDistance();
        if (serie.getHeterogeneous()) {
            AH = Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            AH = serie.getParticle1().getHamakerConstant();
        }

        //ugly hack for a2 - change this to an analytical solution some time
        a2 = 1000; //set radius to 1000 meters - this should do the trick

        double landa = 100E-9;

        double A = -AH / 6.0;
        double G = 2 * a1 * a2;
        double H = 2 * a1;
        double I = 2 * a2;
        double J = a1 + a2;

        double xxphxpix = D * D + H * D + I * D;
        double xph = D + H;
        double xpi = D + I;

        double log = Math.log(xxphxpix / (xph * xpi));

        double term1_1 = 1 / (2 * G) * (xph + xpi) * log;
        double term1_2 = xpi * xph * (xxphxpix + G);
        double term1_3 = (xph + xpi) / (xpi * xph) - xxphxpix / (xpi * xpi * xph) - xxphxpix / (xpi * xph * xph);
        double term1 = (term1_1 + term1_2 * term1_3 / (2 * G * xxphxpix)) / (D + J);

        double term2 = (1 / (2 * G) * (xxphxpix + G) * log + 1) / ((D + J) * (D + J));
        double term3 = xpi * xph * term1_3 / xxphxpix;
        double term4 = G * (xph + xpi) / (xxphxpix * xxphxpix);
        double term5 = G / (xpi * xpi * xph);
        double term6 = G / (xpi * xph * xph);

        return A * (1.12 * G * Math.PI / landa * (term1 - term2) + 1.01 * (term3 - term4 - term5 - term6));
    }
}
