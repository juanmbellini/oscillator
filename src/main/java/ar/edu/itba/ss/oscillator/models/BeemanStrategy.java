package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Optional;

/**
 * Implementation of {@link UpdateStrategy} using Beeman's equations with prediction-correction.
 */
public class BeemanStrategy implements UpdateStrategy {

    /**
     * The previous acceleration.
     */
    private Vector2D previousAcceleration;

    /**
     * The next acceleration (i.e will be used to store it here without having to recalculate it).
     */
    private Vector2D nextAcceleration;

    /**
     * Constructor.
     */
    public BeemanStrategy() {
        this.previousAcceleration = null;
        this.nextAcceleration = null;
    }


    @Override
    public UpdateResults calculate(DampedOscillator dampedOscillator) {
        // Get values into scope
        final Particle particle = dampedOscillator.getParticle();
        final Vector2D actualPosition = particle.getPosition();
        final Vector2D actualVelocity = particle.getVelocity();
        final Vector2D actualAcceleration = particle.getAcceleration();
        final double timeStep = dampedOscillator.getTimeStep();
        // Calculate position
        final Vector2D positionResult = Optional.ofNullable(previousAcceleration)
                .map(prev -> actualPosition
                        .add(actualVelocity.scalarMultiply(timeStep))
                        .add(actualAcceleration.scalarMultiply((2 / 3) * timeStep * timeStep))
                        .subtract(prev.scalarMultiply((1 / 6) * timeStep * timeStep)))
                .orElseGet(() -> {
                    // If here, the previous position has not been calculated yet, which means this is the first step
                    // TODO: what do we do here?
                    return null;
                });
        // Predict velocity
        final Vector2D predictedVelocity = Optional.ofNullable(previousAcceleration)
                .map(prev -> actualVelocity
                        .add(actualAcceleration.scalarMultiply((3 / 2) * timeStep))
                        .subtract(prev.scalarMultiply((1 / 2) * timeStep)))
                .orElseGet(() -> {
                    // If here, the previous position has not been calculated yet, which means this is the first step
                    // TODO: what do we do here?
                    return null;
                });
        // Calculate acceleration with predicted velocity
        this.nextAcceleration = positionResult.scalarMultiply(dampedOscillator.getSpringConstant())
                .add(predictedVelocity.scalarMultiply(dampedOscillator.getViscousDampingCoeffcient()))
                .scalarMultiply(-1);
        // Correct velocity using the calculated acceleration
        final Vector2D velocityResult = Optional.ofNullable(previousAcceleration)
                .map(prev -> actualVelocity
                        .add(nextAcceleration.scalarMultiply((1 / 3) * timeStep))
                        .add(actualAcceleration.scalarMultiply((5 / 6) * timeStep))
                        .subtract(prev.scalarMultiply((1 / 6) * timeStep)))
                .orElseGet(() -> {
                    // If here, the previous position has not been calculated yet, which means this is the first step
                    // TODO: what do we do here?
                    return null;
                });
        return new UpdateResults(positionResult, velocityResult);
    }

    @Override
    public void update(DampedOscillator dampedOscillator) {
        final UpdateResults results = this.calculate(dampedOscillator);
        final Particle particle = dampedOscillator.getParticle();
        this.previousAcceleration = particle.getAcceleration();
        particle.setPosition(results.getPosition());
        particle.setVelocity(results.getVelocity());
        particle.setAcceleration(this.nextAcceleration); // TODO: should we use this acceleration, which was calculated with predicted values? Or should we recalculate it with corrected values?
    }
}
