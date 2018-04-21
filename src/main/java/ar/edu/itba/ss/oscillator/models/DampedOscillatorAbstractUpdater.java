package ar.edu.itba.ss.oscillator.models;

/**
 * An abstract extension of {@link AbstractSystemUpdater}, to be used with the {@link DampedOscillator} system.
 */
/* package */ abstract class DampedOscillatorAbstractUpdater
        extends AbstractSystemUpdater<DampedOscillator.DampedOscillatorState, DampedOscillator> {

    /**
     * Constructor.
     *
     * @param dampedOscillator The {@link DampedOscillator} to be updated.
     */
    /* package */ DampedOscillatorAbstractUpdater(DampedOscillator dampedOscillator) {
        super(dampedOscillator);
    }
}
