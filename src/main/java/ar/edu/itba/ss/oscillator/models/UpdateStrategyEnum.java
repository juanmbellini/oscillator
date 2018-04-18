package ar.edu.itba.ss.oscillator.models;

/**
 * Defines the update strategies.
 */
public enum UpdateStrategyEnum {
    /**
     * Updates the system according to Verlet's equations.
     */
    VERLET {
        @Override
        public UpdateStrategy getStrategyInstance() {
            return new VerletStrategy();
        }
    },
    /**
     * Updates the system according to Beeman's equations.
     */
    BEEMAN {
        @Override
        public UpdateStrategy getStrategyInstance() {
            return new BeemanStrategy();
        }
    },
    /**
     * Updates the system according to Order 5 Gear Predictor-Corrector equations.
     */
    GEAR {
        @Override
        public UpdateStrategy getStrategyInstance() {
            return new GearStrategy();
        }
    };

    /**
     * Builds an {@link UpdateStrategy} according to the enum value.
     *
     * @return The buils {@link UpdateStrategy}.
     */
    public abstract UpdateStrategy getStrategyInstance();
}
