package ar.edu.itba.ss.oscillator.models;

/**
 * Defines the update strategies.
 */
public enum UpdateStrategyEnum {
    /**
     * Updates the system according to Verlet's equations (normal implementation).
     */
    VERLET {
        @Override
        public Updater getStrategyInstance(DampedOscillator dampedOscillator) {
            return new VerletUpdater(dampedOscillator);
        }
    },
    /**
     * Updates the system according to Verlet's equations (using own trick).
     */
    VERLET_TRICK {
        @Override
        public Updater getStrategyInstance(DampedOscillator dampedOscillator) {
            return new VerletTrickUpdater(dampedOscillator);
        }
    },
    /**
     * Updates the system according to Beeman's equations.
     */
    BEEMAN {
        @Override
        public Updater getStrategyInstance(DampedOscillator dampedOscillator) {
            return new BeemanUpdater(dampedOscillator);
        }
    },
    /**
     * Updates the system according to Order 5 Gear Predictor-Corrector equations.
     */
    GEAR {
        @Override
        public Updater getStrategyInstance(DampedOscillator dampedOscillator) {
            return new GearUpdater(dampedOscillator);
        }
    };

    /**
     * Builds an {@link Updater} according to the enum value.
     *
     * @param dampedOscillator The {@link DampedOscillator} the built {@link Updater} will update.
     * @return The built {@link Updater}.
     */
    public abstract Updater getStrategyInstance(DampedOscillator dampedOscillator);
}
