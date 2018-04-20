package ar.edu.itba.ss.oscillator.models;

/**
 * Defines behaviour for an object in charge of updating position and velocity.
 */
public interface UpdateStrategy {

    /**
     * Calculates the new position and velocity, wrapped in an {@link UpdateResults} instance.
     *
     * @param dampedOscillator The damped oscillator that acts as the system to be updated.
     * @return The calcualted results.
     */
    UpdateResults calculate(DampedOscillator dampedOscillator);

    /**
     * Updates the system.
     *
     * @param dampedOscillator The damped oscillator that acts as the system to be updated.
     */
    void update(DampedOscillator dampedOscillator);
}
