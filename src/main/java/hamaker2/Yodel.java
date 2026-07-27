/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2;

import hamaker2.particleSizeDistribution.ParticleSizeDistribution;

/**
 *
 * @author asulrich
 */
public class Yodel {

    /**
     * Create a new YODEL calculator with default values
     */
    public Yodel() {
        //default values
        m_model = Model.kWithPercolation;
        m_percolationTreshold = 0.0;
        m_maxPackingFraction = 0.58;
        m_volumeIncrementFunction = VolumeIncrementFunction.kEnclosingSphere;
        m_normalizationRadius = NormalizationRadius.kRvmean;
        m_harmonicCurvature = false;
        m_curvatureRadius = 125E-9;
        m_autoAttractiveForce = true;
        m_attractiveHamaker = 1E-20;
        m_attractiveSeparation = 5E-9;
        m_defaultVolumeFraction = 0.3;
        m_particle1VolumeFraction = 0.5;
        m_unsavedChanges = false;

        //remove this in final version
        m_bTildaBug = false;
    }

    /**
     * Deep copy the YODEL calculator
     *
     * @param yodel Calculator to be copied
     * @return Deep copy
     */
    public static Yodel newInstance(Yodel yodel) {
        Yodel copy = new Yodel();

        copy.m_percolationTreshold = yodel.m_percolationTreshold;
        copy.m_maxPackingFraction = yodel.m_maxPackingFraction;
        copy.m_volumeIncrementFunction = yodel.m_volumeIncrementFunction;
        copy.m_normalizationRadius = yodel.m_normalizationRadius;
        copy.m_harmonicCurvature = yodel.m_harmonicCurvature;
        copy.m_curvatureRadius = yodel.m_curvatureRadius;      // in meters
        copy.m_autoAttractiveForce = yodel.m_autoAttractiveForce;
        copy.m_attractiveHamaker = yodel.m_attractiveHamaker;    // in joules
        copy.m_attractiveSeparation = yodel.m_attractiveSeparation; // in meters
        copy.m_defaultVolumeFraction = yodel.m_defaultVolumeFraction;
        copy.m_particle1VolumeFraction = yodel.m_particle1VolumeFraction;

        //remove in final version
        copy.m_bTildaBug = yodel.m_bTildaBug;

        copy.m_unsavedChanges = true;

        return copy;
    }

    /**
     * An enum type of the YODEL model
     */
    public enum Model {

        /**
         * YODEL considering percolation
         */
        kWithPercolation("perc", "With Percolation"),
        /**
         * YODEL not considering percolation
         */
        kWithoutPercolation("no_perc", "Without Percolation");

        //parameters
        private final String m_id;
        private final String m_name;

        Model(String id, String name) {
            m_id = id;
            m_name = name;
        }

        /**
         * Return the model ID
         *
         * @return model ID
         */
        public String getId() {
            return m_id;
        }

        /**
         * Return the model name
         *
         * @return Model name
         */
        public String getName() {
            return m_name;
        }

        @Override
        public String toString() {
            return getName();
        }

        /**
         * Return the model with given ID
         *
         * @param id Model ID
         * @return Model with given ID
         */
        public static Model withID(String id) {
            for (Model i : Yodel.Model.values()) {
                if (i.getId().equals(id)) {
                    return i;
                }
            }
            return null;
        }
    }

    /**
     * Enum type for the volume increment function
     */
    public enum VolumeIncrementFunction {

        /**
         * Truncated cone with particle volume
         */
        kTruncatedConeNoParticle("trunc_wo", "Truncated cone (w/o particle fraction)"),
        /**
         * Truncated cone
         */
        kTruncatedCone("trunc", "Truncated cone"),
        /**
         * Enclosing sphere
         */
        kEnclosingSphere("sphere", "Enclosing sphere");
        private final String m_id;
        private final String m_name;

        VolumeIncrementFunction(String id, String name) {
            m_id = id;
            m_name = name;
        }

        /**
         * Return ID of volume increment function
         *
         * @return
         */
        public String getId() {
            return m_id;
        }

        /**
         * Return name of volume increment function
         *
         * @return
         */
        public String getName() {
            return m_name;
        }

        @Override
        public String toString() {
            return getName();
        }

        /**
         * Return volume increment function with given ID
         *
         * @param id Function ID
         * @return Volume increment function with given ID
         */
        public static VolumeIncrementFunction withID(String id) {
            for (VolumeIncrementFunction i : Yodel.VolumeIncrementFunction.values()) {
                if (i.getId().equals(id)) {
                    return i;
                }
            }
            return null;
        }
    }

    /**
     * ENum type for the normalization radius
     */
    public enum NormalizationRadius {

        /**
         * Median volume diameter
         */
        kRv50("rv50", "Rv,50"),
        /**
         * Mean volume diameter
         */
        kRvmean("rvmean", "Rv,mean");
        private final String m_id;
        private final String m_name;

        NormalizationRadius(String id, String name) {
            m_id = id;
            m_name = name;
        }

        /**
         * Return ID of normalization radius
         *
         * @return ID of normalization radius
         */
        public String getId() {
            return m_id;
        }

        /**
         * Return name of normalization radius
         *
         * @return Name of normalization radius
         */
        public String getName() {
            return m_name;
        }

        @Override
        public String toString() {
            return getName();
        }

        /**
         * Return normalization radius with given ID
         *
         * @param id ID of normalization radius
         * @return Normalization radius with given ID
         */
        public static NormalizationRadius withID(String id) {
            for (NormalizationRadius i : Yodel.NormalizationRadius.values()) {
                if (i.getId().equals(id)) {
                    return i;
                }
            }
            return null;
        }
    }

    private Model m_model;
    private double m_percolationTreshold;
    private double m_maxPackingFraction;
    private VolumeIncrementFunction m_volumeIncrementFunction;
    private NormalizationRadius m_normalizationRadius;
    private boolean m_harmonicCurvature;
    private double m_curvatureRadius;      // in meters
    private boolean m_autoAttractiveForce;
    private double m_attractiveHamaker;    // in joules
    private double m_attractiveSeparation; // in meters
    private double m_defaultVolumeFraction;
    private double m_particle1VolumeFraction;
    private boolean m_unsavedChanges;

    //remove in final version
    private boolean m_bTildaBug;

    /**
     * Return the selected YODEL model
     *
     * @return Selected YODEL model
     */
    public Model getModel() {
        return m_model;
    }

    /**
     * Set the selected YODEL model
     *
     * @param model Selected YODEL model
     */
    public void setModel(Model model) {
        m_model = model;
    }

    /**
     * Return the percolation treshold
     *
     * @return Percolation treshold
     */
    public double getPercolationTreshold() {
        return m_percolationTreshold;
    }

    /**
     * Set the percolation treshold
     *
     * @param treshold Percolation treshold
     */
    public void setPercolationTreshold(double treshold) {
        m_percolationTreshold = treshold;
    }

    /**
     * Return the maximum packing fraction
     *
     * @return Maximum packing fraction
     */
    public double getMaxPackingFraction() {
        return m_maxPackingFraction;
    }

    /**
     * Set the maximum packing fraction
     *
     * @param fraction Maximum packing fraction
     */
    public void setMaxPackingFraction(double fraction) {
        m_maxPackingFraction = fraction;
    }

    /**
     * Return the volume increment function
     *
     * @return Volume increment function
     */
    public VolumeIncrementFunction getVolumeIncrementFunction() {
        return m_volumeIncrementFunction;
    }

    /**
     * Set the volume increment function
     *
     * @param function Volume increment function
     */
    public void setVolumeIncrementFunction(VolumeIncrementFunction function) {
        m_volumeIncrementFunction = function;
    }

    /**
     * Return the normalization radius
     *
     * @return Normalization radius
     */
    public NormalizationRadius getNormalizationRadius() {
        return m_normalizationRadius;
    }

    /**
     * Set the normalization radius
     *
     * @param radius Normalization radius
     */
    public void setNormalizationRadius(NormalizationRadius radius) {
        m_normalizationRadius = radius;
    }

    /**
     * Return if model uses harmonic radius of curvature
     *
     * @return Model uses harmonic radius of curvature
     */
    public boolean getHarmonicCurvature() {
        return m_harmonicCurvature;
    }

    /**
     * Set if model uses harmonic radius of curvature
     *
     * @param curvature Model uses harmonic radius of curvature
     */
    public void setHarmonicCurvature(boolean curvature) {
        m_harmonicCurvature = curvature;
    }

    /**
     * Return radius of curvature
     *
     * @return Radius of curvature
     */
    public double getCurvatureRadius() {
        return m_curvatureRadius;
    }

    /**
     * Set radius of curvature
     *
     * @param radius Radius of curvature
     */
    public void setCurvatureRadius(double radius) {
        m_curvatureRadius = radius;
    }

    /**
     * Return if the model determined attractive force
     *
     * @return Does model determine attractive force
     */
    public boolean getAutoAttractiveForce() {
        return m_autoAttractiveForce;
    }

    /**
     * Set if the model determined attractive force
     *
     * @param force Does model determine attractive force
     */
    public void setAutoAttractiveForce(boolean force) {
        m_autoAttractiveForce = force;
    }

    /**
     * Return Hamaker constant for attractive force calculation
     *
     * @return Hamaker constant
     */
    public double getManualAttractiveHamaker() {
        return m_attractiveHamaker;
    }

    /**
     * Set Hamaker constant for attractive force calculation
     *
     * @param hamaker Hamaker constant
     */
    public void setManualAttractiveHamaker(double hamaker) {
        m_attractiveHamaker = hamaker;
    }

    /**
     * Return separation for attractive force calculation
     *
     * @return Separation
     */
    public double getManualAttractiveSeparation() {
        return m_attractiveSeparation;
    }

    /**
     * Set separation for attractive force calculation
     *
     * @param separation Separation
     */
    public void setManualAttractiveSeparation(double separation) {
        m_attractiveSeparation = separation;
    }

    /**
     * Return default volume fraction
     *
     * @return Default volume fraction
     */
    public double getDefaultVolumeFraction() {
        return m_defaultVolumeFraction;
    }

    /**
     * Set default volume fraction
     *
     * @param frac Default volume fraction
     */
    public void setDefaultVolumeFraction(double frac) {
        m_defaultVolumeFraction = frac;
    }

    /**
     * Return particle 1 volume fraction
     *
     * @return Particle 1 volume fraction
     */
    public double getParticle1VolumeFraction() {
        return m_particle1VolumeFraction;
    }

    /**
     * Set particle 1 volume fraction
     *
     * @param frac Particle 1 volume fraction
     */
    public void setParticle1VolumeFraction(double frac) {
        m_particle1VolumeFraction = frac;
    }

    /**
     * Return particle 2 volume fraction
     *
     * @return Particle 2 volume fraction
     */
    public double getParticle2VolumeFraction() {
        return 1.0 - m_particle1VolumeFraction;
    }

    /**
     * Set particle 2 volume fraction
     *
     * @param frac Particle 2 volume fraction
     */
    public void setParticle2VolumeFraction(double frac) {
        m_particle1VolumeFraction = 1.0 - frac;
    }

    /**
     * Return if the YODEL model needs saving
     *
     * @return YODEL model needs saving
     */
    public boolean getNeedsSave() {
        return m_unsavedChanges;
    }

    /**
     * Set if the YODEL model needs saving
     *
     * @param s YODEL model needs saving
     */
    public void setNeedsSave(boolean s) {
        m_unsavedChanges = s;
    }

    /**
     * Return maximum attractive force for given series
     *
     * @param serie Series to be analyzed
     * @return Maximum attractive force
     */
    /*public double maxAttractiveForce(Serie serie) {
     if (m_autoAttractiveForce) {
     return autoMaxAttractiveForce(serie);
     } else {
     return manualMaxAttractiveForce(serie);
     }
     }*/
    /**
     * Return the automatic maximum attractive force for given series
     *
     * @param serie Series to be analyzed
     * @return Maximum attractive force
     */
    /*public double autoMaxAttractiveForce(Serie serie) {
     double kT = 1.3807E-23 * serie.getTemperature();
     if (m_harmonicCurvature) {
     return serie.maxAttractiveForce() * kT / 1E-9 / serie.averageHarmonicRadius();
     } else {
     return serie.maxAttractiveForce() * kT / 1E-9 / m_curvatureRadius;
     }
     }*/
    /**
     * Return the manual maximum attractive force for given series
     *
     * @param serie Series to be analyzed
     * @return Maximum attractive force
     */
    /*public double manualMaxAttractiveForce(Serie serie) {
     return m_attractiveHamaker / (12.0 * m_attractiveSeparation * m_attractiveSeparation);
     }*/
    
    public double getAutoAttractiveHamaker(Serie serie) {
        if (serie.getHeterogeneous()) {
            return Math.sqrt(serie.getParticle1().getHamakerConstant() * serie.getParticle2().getHamakerConstant());
        } else {
            return serie.getParticle1().getHamakerConstant();
        }
    }

    public double getAutoAttractiveSeparation(Serie serie) {
        return serie.maxAttractiveSeparation();
    }

    public double getAttractiveHamaker(Serie serie) {
        if (m_autoAttractiveForce) {
            return getAutoAttractiveHamaker(serie);
        } else {
            return getManualAttractiveHamaker();
        }
    }

    public double getAttractiveSeparation(Serie serie) {
        if (m_autoAttractiveForce) {
            return getAutoAttractiveSeparation(serie);
        } else {
            return getManualAttractiveSeparation();
        }
    }

    //remove in final version
    public boolean bTildaBug() {
        return m_bTildaBug;
    }

    public void setBTildaBug(boolean bug) {
        m_bTildaBug = bug;
    }

    /**
     * Print model parameters for debugging
     */
    public void printParams() {
        System.out.println("YODEL model:                " + m_model.getName());
        System.out.println("Percolation treshold:       " + m_percolationTreshold);
        System.out.println("Max packing fraction:       " + m_maxPackingFraction);
        System.out.println("Volume increment function:  " + m_volumeIncrementFunction.getName());
        System.out.println("Normalization radius:       " + m_normalizationRadius.getName());
        System.out.println("Harmonic curvature:         " + m_harmonicCurvature);
        System.out.println("Radius of curvature:        " + m_curvatureRadius);
        System.out.println("Automatic attractive force: " + m_autoAttractiveForce);
        System.out.println("Hamaker constant:           " + m_attractiveHamaker);
        System.out.println("Separation:                 " + m_attractiveSeparation);
        System.out.println("Default volume farction:    " + m_defaultVolumeFraction);
        System.out.println("Particle 1 volume fraction: " + m_particle1VolumeFraction);
        System.out.println("----------------------------------------------------------");
    }
    
    public void printSizeDistribution(Serie s, int particle) {
        Particle p = s.getParticle1();
        if (particle == 2) {
            p = s.getParticle2();
        }
        
        ParticleSizeDistribution psd = p.getSelectedSizeDistribution();
        double[] diameters = psd.diameters();
        double[] fractions = psd.volumeFractions();
        
        for (int i=0;i<psd.numPoints();i++) {
            System.out.println(diameters[i] + "\t" + fractions[i]);
        }
    }

    /**
     * Yield stress for given series and volume fraction
     *
     * @param s Series to be analyzed
     * @param volumeFraction Volume fraction of particles
     * @return Yield stress
     */
    public double yieldStress(Serie s, double volumeFraction) {

        //first compute the chosen volume fraction function
        double volumeFractionFunction = 0;
        double delta0 = volumeFraction - m_percolationTreshold;
        double deltaMax = m_maxPackingFraction - volumeFraction;
        if (m_model == Model.kWithPercolation) {
            volumeFractionFunction = volumeFraction * delta0 * delta0 / (m_maxPackingFraction * deltaMax);
        } else if (m_model == Model.kWithoutPercolation) {
            volumeFractionFunction = volumeFraction * volumeFraction * delta0 / (m_maxPackingFraction * deltaMax);
        }

        if (!s.getHeterogeneous()) {
            //printSizeDistribution(s, 1);
            return m1(s, s.getParticle1()) * volumeFractionFunction;
        } else {
            double yield1 = m1(s, s.getParticle1()) * volumeFractionFunction;
            double yield2 = m1(s, s.getParticle2()) * volumeFractionFunction;
            return Math.pow(m_particle1VolumeFraction * Math.sqrt(yield1) + (1.0 - m_particle1VolumeFraction) * Math.sqrt(yield2), 2.0);
        }

    }

    /**
     * Yield stress for given series and default volume fraction
     *
     * @param s Series to be analyzed
     * @return Yield stress
     */
    public double defaultYieldStress(Serie s) {
        return yieldStress(s, m_defaultVolumeFraction);
    }

    private double m1(Serie s, Particle p) {

        //get the chosen normalization radius
        double normRad = 0;
        if (m_normalizationRadius == NormalizationRadius.kRv50) {
            normRad = p.getSelectedSizeDistribution().getRv(50);
        } else if (m_normalizationRadius == NormalizationRadius.kRvmean) {
            normRad = p.getSelectedSizeDistribution().getRvmean();
        }

        double ukk = 0;
        if (m_volumeIncrementFunction == VolumeIncrementFunction.kEnclosingSphere) {
            ukk = 16.0 * Math.PI / (2 - Math.sqrt(3));
        } else if (m_volumeIncrementFunction == VolumeIncrementFunction.kTruncatedCone) {
            ukk = 4.0 * Math.PI / (2 - Math.sqrt(3));
        } else if (m_volumeIncrementFunction == VolumeIncrementFunction.kTruncatedConeNoParticle) {
            ukk = 4.0 * Math.PI / (3 * (2 - Math.sqrt(3)));
        }

        //eq 41
        //return 1.8 / Math.pow(Math.PI, 4.0) * maxAttractiveForce(s) / normRad * Fsigma_delta(s, p, normRad);
        //eq 44
        double curvature = m_curvatureRadius;
        if (m_harmonicCurvature) {
            curvature = s.averageHarmonicRadius();
        }
        double f_star = Fsigma_delta(s, p, normRad) / ukk * normRad / curvature;
        if (m_bTildaBug) {
            f_star = Fsigma_delta(s, p, normRad) / ukk * normRad / (curvature / 1000.0);  //try to reproduce old behaviour where in eq 39 a* and Rv,50 had units of nm and µm respectively
        }
        return 0.15 * ukk * getAttractiveHamaker(s) * curvature / (Math.pow(Math.PI, 4.0) * Math.pow(getAttractiveSeparation(s), 2.0)) * f_star / (normRad * normRad);
    }

    private double Fsigma_delta(Serie s, Particle p, double normRad) {

        //get size distribution values
        int num = p.getSelectedSizeDistribution().numPoints();
        double[] radii = p.getSelectedSizeDistribution().radii();
        double[] volumeFractions = p.getSelectedSizeDistribution().volumeFractions();

        /*System.out.println("start---------------------");
         for (int i=0;i<num;i++) {
         System.out.println(String.valueOf(radii[i] * 2) + ", " + String.valueOf(volumeFractions[i]));
         }
         System.out.println("end---------------------");*/
        double inv_normRad = 1.0 / normRad;

        //procompute the Sa_l denominator
        double Sa_l_denom = 0;
        for (int i = 0; i < num; i++) {
            double bi = radii[i] * inv_normRad;
            double phii = volumeFractions[i];

            Sa_l_denom += phii / bi;
        }

        //start the double sum over particles
        double result = 0;
        for (int k = 0; k < num; k++) {

            //extract size and frequency of particle k
            double phik = volumeFractions[k];
            double bk = radii[k] * inv_normRad;

            //compute bk3
            double bk3 = bk * bk * bk;

            double sum_l = 0;
            for (int l = 0; l < num; l++) {

                //extract size and frequency of particle l
                double phil = volumeFractions[l];
                double bl = radii[l] * inv_normRad;

                //compute the Sa_l term
                double Sal = (phil / bl) / Sa_l_denom;

                //compute the As/Ac term
                double As_Ac = 2 * (bl + bk) / (bk + bl - Math.sqrt(bk * (bk + 2 * bl)));

                //compute g_kl
                double b_tilda;
                if (m_harmonicCurvature) {
                    b_tilda = 2 * bk * bl / (bk + bl);
                } else {
                    b_tilda = m_curvatureRadius * inv_normRad;
                }
                double gkl = 2 * b_tilda / (bk * bk + bl * bl);

                sum_l += Sal * As_Ac * delta_Vkl(bk, bl) / bk3 * gkl;
            }

            result += phik * sum_l;

        }

        //include the 1/2 factor and return
        return 0.5 * result;
    }

    private double delta_Vkl(double bk, double bl) {
        if (m_volumeIncrementFunction == VolumeIncrementFunction.kEnclosingSphere) {
            return 4.0 * Math.PI * (bk * bl) * (bk + bl);
        } else if (m_volumeIncrementFunction == VolumeIncrementFunction.kTruncatedCone) {
            return 16.0 * Math.PI / 3.0 * ((bk * bl) * (bk * bl)) / ((bk + bl) * (bk + bl) * (bk + bl)) * (bk * bk + bl * bl + bk * bl);
        } else if (m_volumeIncrementFunction == VolumeIncrementFunction.kTruncatedConeNoParticle) {
            return 4.0 * Math.PI / 3.0 * ((2 * bk * bl) * (2 * bk * bl)) / (bk + bl);
        } else {
            return 0;
        }
    }
}
