package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of {@link UpdateStrategy} using Verlet's equations.
 */
public class VerletStrategy implements UpdateStrategy {

    /**
     * The future position, which is pre-calculated in order to get the new velocity value.
     */
    private Vector2D nextPosition;

    /**
     * Constructor.
     */
    public VerletStrategy() {
        this.nextPosition = null;
    }

    @Override
    public UpdateResults calculate(DampedOscillator dampedOscillator) {
        // Get values into scope
        final Particle particle = dampedOscillator.getParticle();
        final Vector2D actualPosition = particle.getPosition();
        final double timeStep = dampedOscillator.getTimeStep();
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();

        // The actual position is the value calculated in previous step as the next position
        final Vector2D positionResult = Optional.ofNullable(nextPosition)
                .orElseGet(() -> {
                    // If here, the next position has not been calculated yet, which means this is the first step
                    // TODO: what do we do here?
                    return null;

                });

        // Calculate the next step position to be used for velocity calculation
        // Note that positionResult has the new actual position, and the actual position is the previous one
        this.nextPosition = positionResult.scalarMultiply(2)
                .subtract(actualPosition)
                .add(forceProvider.apply(particle).scalarMultiply(timeStep * timeStep / particle.getMass()));
        // The speed is calculated using the next position and the previous one.
        final Vector2D velocityResult = this.nextPosition.subtract(actualPosition).scalarMultiply(1 / (2 * timeStep));
        // Save the actual position to be the previous position in next step


        return new UpdateResults(positionResult, velocityResult);
    }

    @Override
    public void update(DampedOscillator dampedOscillator) {
        final UpdateResults results = this.calculate(dampedOscillator);
        final Particle particle = dampedOscillator.getParticle();
        particle.setPosition(results.getPosition());
        particle.setVelocity(results.getVelocity());
        // Calculate acceleration using new values
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();
        particle.setAcceleration(forceProvider.apply(particle).scalarMultiply(1 / particle.getMass()));
    }
}
