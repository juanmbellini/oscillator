package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.function.Function;

/**
 * Implementation of {@link UpdateStrategy} using Verlet's equations.
 */
public class VerletStrategy implements UpdateStrategy {

    /**
     * The previous positions, used to calculate new values of position and velocity.
     */
    private Vector2D previousPosition;
    /**
     * The future position, which is pre-calculated in order to get the new velocity value.
     */
    private Vector2D nextPosition;

    /**
     * Constructor.
     */
    public VerletStrategy() {
        this.nextPosition = null;
        this.previousPosition = null;
    }

    @Override
    public UpdateResults calculate(DampedOscillator dampedOscillator) {
        if (previousPosition == null) {
            // TODO: what do we do here?
            return null;
        }
        if (nextPosition == null) {
            // TODO: what do we do here?
            return null;
        }

        // Get values into scope
        final Particle particle = dampedOscillator.getParticle();
        final Vector2D actualPosition = particle.getPosition();
        final double timeStep = dampedOscillator.getTimeStep();
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();
        // The actual position is the value calculated in previous step as the next position
        final Vector2D positionResult = this.nextPosition;
        // Calculate the next step position to be used for velocity calculation
        this.nextPosition = actualPosition.scalarMultiply(2)
                .subtract(previousPosition)
                .add(forceProvider.apply(particle).scalarMultiply(timeStep * timeStep / particle.getMass()));
        // The speed is calculated using the next position and the previous one.
        final Vector2D velocityResult = this.nextPosition.subtract(previousPosition).scalarMultiply(1 / (2 * timeStep));
        // Save the actual position to be the previous position in next step
        this.previousPosition = actualPosition;

        return new UpdateResults(positionResult, velocityResult);
    }
}
