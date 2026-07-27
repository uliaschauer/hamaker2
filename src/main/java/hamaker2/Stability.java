/*
 * Stability.java
 *
 * Created on 6. septembre 2007, 12:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2;

/**
 * Class that implements stability calculations by doublet formation probability
 * @author aschauer
 */
public class Stability {

    private double m_p1_conc, m_p2_conc;
    private int m_hours, m_minutes;
    private boolean m_viscous;

    /**
     * Creates a new instance of Stability calculator
     */
    public Stability() {
        m_p1_conc = 10;
        m_p2_conc = 5;
        m_hours = 12;
        m_minutes = 0;
        m_viscous = false;
    }

    /**
     * Deep copy the stability calculator
     * @param stab
     * @return
     */
    public static Stability newInstance(Stability stab) {
        Stability copy = new Stability();

        copy.m_p1_conc = stab.m_p1_conc;
        copy.m_p2_conc = stab.m_p2_conc;
        copy.m_hours = stab.m_hours;
        copy.m_minutes = stab.m_minutes;
        copy.m_viscous = stab.m_viscous;

        return copy;
    }

    /**
     * Return the concentration of particle type 1 in the suspension 
     * @return Concentration of particle type 1
     */
    public double getParticle1Concentration() {
        return m_p1_conc;
    }

    /**
     * Return the concentration of particle type 2 in the suspension 
     * @return Concentration of particle type 2
     */
    public double getParticle2Concentration() {
        return m_p2_conc;
    }

    /**
     * Set the concentration of particle type 1 in the suspension
     * @param conc Concentration of particle type 1
     */
    public void setParticle1Concentration(double conc) {
        m_p1_conc = conc;
    }

    /**
     * Set the concentration of particle type 2 in the suspension
     * @param conc Concentration of particle type 2
     */
    public void setParticle2Concentration(double conc) {
        m_p2_conc = conc;
    }

    /**
     * Return the hours of required stability
     * @return Hours of required stability
     */
    public int getHours() {
        return m_hours;
    }

    /**
     * Set the hours of required stability
     * @param hours Hours of required stability
     */
    public void setHours(int hours) {
        m_hours = hours;
    }

    /**
     * Return the minutes of required stability
     * @return Minutes of required stability
     */
    public int getMinutes() {
        return m_minutes;
    }

    /**
     * Set the minutes of required stability
     * @param minutes Minutes of required stability
     */
    public void setMinutes(int minutes) {
        m_minutes = minutes;
    }

    /**
     * Return if viscous drag is to be included in the model
     * @return Viscous drag
     */
    public boolean getViscous() {
        return m_viscous;
    }

    /**
     * Set if viscous drag is to be included in the model
     * @param viscous Viscous drag
     */
    public void setViscous(boolean viscous) {
        m_viscous = viscous;
    }

    /**
     * Return the barrier required to achieve colloidal stability for the set amount of time and at the given conditions
     * @param p1 Particle type 1
     * @param p2 Particle type 2
     * @param m Dispersion medium
     * @param kB Boltzmann constant
     * @param T Temperature
     * @return Required barrier in kT
     */
    public double getBarrier(Particle p1, Particle p2, Medium m, double kB, double T) {

        double lowest_barrier = -1.0;

        //loop over all particle pairs in the system
        int count1 = p1.getSelectedSizeDistribution().numPoints();
        double[] diameters1 = p1.getSelectedSizeDistribution().diameters();
        double[] fractions1 = p1.getSelectedSizeDistribution().numberFractions();

        int count2 = p2.getSelectedSizeDistribution().numPoints();
        double[] diameters2 = p2.getSelectedSizeDistribution().diameters();
        double[] fractions2 = p2.getSelectedSizeDistribution().numberFractions();


        for (int i1 = 0; i1 < count1; i1++) {

            double diameter1 = diameters1[i1];
            double radius1 = 0.5 * diameter1;
            double fraction1 = fractions1[i1];

            for (int i2 = 0; i2 < count2; i2++) {

                double diameter2 = diameters2[i2];
                double radius2 = 0.5 * diameter2;
                double fraction2 = fractions2[i2];

                //now perform actual barrier computation
                double m1 = 4.0 / 3.0 * Math.PI * Math.pow(radius1, 3) * p1.getDensity();
                double m2 = 4.0 / 3.0 * Math.PI * Math.pow(radius2, 3) * p2.getDensity();

                double v1 = Math.sqrt(2 * kB * T / m1);
                double v2 = Math.sqrt(2 * kB * T / m2);

                double np1 = m_p1_conc * 0.01 * m.getDensity() / m1;
                double np2 = m_p2_conc * 0.01 * m.getDensity() / m2;
                double np = np1 + np2;

                //main difference: rarer particle pairs are further apart, hence collide less often
                //double d = 1.0 / Math.pow(np, 1.0 / 3.0);
                double fraction = fraction1 * fraction2;
                if (fraction > 0.0) {
                    double d = 1.0 / Math.pow(np * fraction, 1.0 / 3.0);

                    double dt;
                    if (m_viscous == false) {
                        dt = d / Math.max(v1, v2);
                    } else {
                        double A = 0.25 * Math.PI * Math.min(diameter1, diameter2) * Math.min(diameter1, diameter2);
                        double damp = 0.5 * m.getDensity() * A * 0.47 / Math.min(m1, m2);
                        dt = (Math.exp(damp * d) - 1) / (damp * Math.max(v1, v2));
                    }

                    double nc = (m_hours * 3600 + m_minutes * 60) / dt;

                    double barrier = -Math.log(1.0 / nc);
                    if (barrier < lowest_barrier || lowest_barrier == -1.0) {
                        lowest_barrier = barrier;
                    }
                }
            }
        }

        //This is the old unchanged code
        /*double m1 = 4.0 / 3.0 * Math.PI * Math.pow(p1.getRadius(), 3) * p1.getDensity();
         double m2 = 4.0 / 3.0 * Math.PI * Math.pow(p2.getRadius(), 3) * p2.getDensity();
        
         double v1 = Math.sqrt(2 * kB * T / m1);
         double v2 = Math.sqrt(2 * kB * T / m2);
        
         double np1 = m_p1_conc * 0.01  * m.getDensity() / m1;
         double np2 = m_p2_conc * 0.01  * m.getDensity() / m2;
         double np = np1 + np2;
        
         double d = 1.0 / Math.pow(np, 1.0/3.0);
         double dt;
         if (m_viscous == false) {
         dt = d / Math.max(v1, v2);
         }
         else
         {
         double A = 0.25 * Math.PI * Math.min(p1.getDiameter(), p2.getDiameter()) * Math.min(p1.getDiameter(), p2.getDiameter());
         double damp = 0.5 * m.getDensity() * A * 0.47 / Math.min(m1, m2);
         dt = (Math.exp(damp * d) - 1) / (damp * Math.max(v1, v2));
         }
        
         double nc = (m_hours * 3600 + m_minutes * 60) / dt;
        
         return -Math.log(1.0 / nc);*/


        return lowest_barrier;
    }
}
