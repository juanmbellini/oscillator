package ar.edu.itba.ss.oscillator.models;

/**
 * Defines the update strategies.
 */
public enum UpdateStrategyEnum {
    /**
     * Updates the system according to Verlet's equations.
     */
    VERLET,
    /**
     * Updates the system according to Beeman's equations.
     */
    BEEMAN,
    /**
     * Updates the system according to Order 5 Gear Predictor-Corrector equations.
     */
    GEAR,
}
