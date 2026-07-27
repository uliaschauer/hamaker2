/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.models;

/**
 * Define a class for an oscillator
 * @author uli
 */
public class Oscillator {

    private double m_strength;
    private double m_frequency;

    /**
     * Create a new oscillator with given strength and frequency
     * @param strength Oscillator strength
     * @param frequency Oscillator frequency
     */
    public Oscillator(double strength, double frequency) {
        m_strength = strength;
        m_frequency = frequency;
    }

    /**
     * Return the oscillator strength
     * @return Oscillator strength
     */
    public double getStrength() {
        return m_strength;
    }

    /**
     * Set the oscillator strength
     * @param strength Oscillator strength
     */
    public void setStrength(double strength) {
        m_strength = strength;
    }

    /**
     * Return the oscillator frequency
     * @return Oscillator frequency
     */
    public double getFrequency_10_15_RadS() {
        return m_frequency * 1E12 * 6.2831853 / 1E15;
    }
    
    /**
     * Set the oscillator frequency
     * @return Oscillator frequency
     */
    public double getFrequencyTHz() {
        return m_frequency;
    }

    /**
     * Set the oscillator frequency in THz
     * @param frequency Oscillator frequency in THz
     */
    public void setFrequencyTHz(double frequency) {
        m_frequency = frequency;
    }
    
    /**
     * Deep copy the oscillator
     * @return
     */
    public Oscillator duplicate() {
        return new Oscillator(m_strength, m_frequency);
    }
}
