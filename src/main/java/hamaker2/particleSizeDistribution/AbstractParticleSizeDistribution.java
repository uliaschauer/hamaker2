/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.particleSizeDistribution;

import hamaker2.models.PlotVariableProvider;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * Class that implements generic PSD functionality
 *
 * @author uli
 */
public abstract class AbstractParticleSizeDistribution extends PlotVariableProvider implements ParticleSizeDistribution {

    /**
     * Variable to save the preferred size into
     */
    protected Dimension m_prefSize;

    /**
     * The panel that displays the PSD settings
     */
    protected JPanel m_panel;

    /**
     * The version string
     */
    protected String m_version = "";

    /**
     * Create a new abstract PSD
     */
    public AbstractParticleSizeDistribution() {
        m_panel = new JPanel();
        savePrefSize();
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public final void savePrefSize() {
        m_prefSize = m_panel.getPreferredSize();
    }

    @Override
    public final void setPrefSize() {
        m_panel.setPreferredSize(m_prefSize);
    }

    @Override
    public final void setZeroSize() {
        m_panel.setPreferredSize(new Dimension(0, 0));
    }

    @Override
    public final JPanel panel() {
        return m_panel;
    }

    @Override
    public final String version() {
        return m_version;
    }

    //PSD functions
    /**
     * Convert from number to volume fraction, this is provided as a convenience
     * routine for all PSD classes
     *
     * @return array of volume fractions
     */
    public double[] numberToVolumeFraction() {
        double[] cx = diameters();
        double[] cy = numberFractions();
        assert (cx.length == cy.length);
        double sum = 0;
        double[] volumeFraction = new double[cx.length];
        for (int i = 0; i < cx.length; i++) {
            volumeFraction[i] = 4.0 / 3.0 * (0.125 * cx[i] * cx[i] * cx[i]) * Math.PI * cy[i];
            sum += volumeFraction[i];
        }
        for (int i = 0; i < cx.length; i++) {
            volumeFraction[i] /= sum;
        }
        return volumeFraction;
    }

    /**
     * Return the cumulative number fraction by summing the frequencies. Use
     * this if you don't have an analytic expression.
     *
     * @return array of cumulative number fractions
     */
    @Override
    public double[] cumulativeNumberFraction() {
        double[] fraction = numberFractions();
        double[] cummulative = new double[fraction.length];

        double sum = 0.0;
        for (int i = 0; i < fraction.length; i++) {
            sum += fraction[i];
            cummulative[i] = sum;
        }

        return cummulative;
    }

    /**
     * Return the cumulative volume fraction by summing the frequencies. Use
     * this if you don't have an analytic expression.
     *
     * @return array of cumulative volume fractions
     */
    @Override
    public double[] cumulativeVolumeFraction() {
        double[] fraction = volumeFractions();
        double[] cummulative = new double[fraction.length];

        double sum = 0.0;
        for (int i = 0; i < fraction.length; i++) {
            sum += fraction[i];
            cummulative[i] = sum;
        }

        return cummulative;

    }

    /**
     * Get the radius for a given cumulative volume fraction. Determines by
     * bisection. Use if you have no analytical expression.
     *
     * @param fraction cumulative volume fraction
     * @return radius for the given cumulative volume fraction
     */
    @Override
    public double getRv(double fraction) {
        double[] radii = radii();
        double[] fractions = cumulativeVolumeFraction();

        assert (radii.length == fractions.length);

        for (int i = 1; i < radii.length; i++) {
            if (fractions[i - 1] < fraction && fractions[i] > fraction) {
                double a = (fractions[i] - fractions[i - 1]) / (radii[i] - radii[i - 1]);
                double b = fractions[i] - a * radii[i];
                return (fraction - b) / a;
            }
        }

        return radii[radii.length - 1];
    }

    /**
     * Get the diameter for a given cumulative volume fraction. Determines by
     * bisection. Use if you have no analytical expression.
     *
     * @param fraction cumulative volume fraction
     * @return diameter for the given cumulative volume fraction
     */
    @Override
    public double getDv(double fraction) {
        double[] diameters = diameters();
        double[] fractions = cumulativeVolumeFraction();

        assert (diameters.length == fractions.length);

        for (int i = 1; i < diameters.length; i++) {
            if (fractions[i - 1] < fraction && fractions[i] > fraction) {
                double a = (fractions[i] - fractions[i - 1]) / (diameters[i] - diameters[i - 1]);
                double b = fractions[i] - a * diameters[i];
                return (fraction - b) / a;
            }
        }

        return diameters[diameters.length - 1];
    }

    /**
     * Return the mean radius
     *
     * @return mean radius
     */
    @Override
    public double getRvmean() {
        return 0.5 * getDvmean();
    }

    /**
     * Return the mean diameter
     *
     * @return mean diameter
     */
    @Override
    public double getDvmean() {
        double[] diameters = diameters();
        double[] fractions = numberFractions();

        assert (diameters.length == fractions.length);

        double nom = 0.0, denom = 0.0;
        for (int i = 1; i < diameters.length; i++) {
            nom += Math.pow(diameters[i], 4.0) * fractions[i];
            denom += Math.pow(diameters[i], 3.0) * fractions[i];
        }

        return nom / denom;
    }
}
