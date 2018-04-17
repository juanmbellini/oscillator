package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of {@link UpdateStrategy} using Beeman's equations with prediction-correction.
 */
/* package */ class BeemanStrategy implements UpdateStrategy {

    /**
     * The previous acceleration.
     */
    private Vector2D previousAcceleration;

    /**
     * The next acceleration (i.e will be used to store it here without having to recalculate it).
     */
    private Vector2D nextAcceleration; // TODO: make UpdateResults be subclassed for each strategy

    /**
     * Constructor.
     */
    /* package */ BeemanStrategy() {
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
        final double mass = particle.getMass();
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();

        // Check if the previous position was set
        // In case not, we are in the first step, so we need to calculate it
        this.previousAcceleration = Optional.ofNullable(previousAcceleration)
                .orElseGet(() -> {
                    // If here, the previous acceleration has not been calculated yet, so this is the first step
                    final Vector2D force = forceProvider.apply(particle);
                    // Calculate velocity at -deltaT
                    final Vector2D previousVelocity = particle.getVelocity()
                            .subtract(force.scalarMultiply(timeStep / mass));
                    // Calculate position at -deltaT
                    final Vector2D previousPosition = actualPosition
                            .subtract(previousVelocity.scalarMultiply(timeStep))
                            .add(force.scalarMultiply((timeStep * timeStep) / (2 * mass)));

                    return previousPosition.scalarMultiply(dampedOscillator.getSpringConstant())
                            .add(previousVelocity.scalarMultiply(dampedOscillator.getViscousDampingCoeffcient()))
                            .scalarMultiply(-1 / mass);
                });

        // When control reaches here, previous acceleration is already initialized with something
        // Calculate position
        final Vector2D positionResult = actualPosition
                .add(actualVelocity.scalarMultiply(timeStep))
                .add(actualAcceleration.scalarMultiply((2d / 3d) * timeStep * timeStep))
                .subtract(previousAcceleration.scalarMultiply((1d / 6d) * timeStep * timeStep));
        // Predict velocity
        final Vector2D predictedVelocity = actualVelocity
                .add(actualAcceleration.scalarMultiply((3d / 2d) * timeStep))
                .subtract(previousAcceleration.scalarMultiply((1d / 2d) * timeStep));
        // Calculate acceleration with predicted velocity
        this.nextAcceleration = positionResult.scalarMultiply(dampedOscillator.getSpringConstant())
                .add(predictedVelocity.scalarMultiply(dampedOscillator.getViscousDampingCoeffcient()))
                .scalarMultiply(-1 / mass);
        // Correct velocity using the calculated acceleration
        final Vector2D velocityResult = actualVelocity
                .add(nextAcceleration.scalarMultiply((1d / 3d) * timeStep))
                .add(actualAcceleration.scalarMultiply((5d / 6d) * timeStep))
                .subtract(previousAcceleration.scalarMultiply((1d / 6d) * timeStep));
        return new UpdateResults(positionResult, velocityResult);
    }

    @Override
    public void update(DampedOscillator dampedOscillator) {
        final UpdateResults results = this.calculate(dampedOscillator);
        final Particle particle = dampedOscillator.getParticle();
        this.previousAcceleration = particle.getAcceleration();
        particle.setPosition(results.getPosition());
        particle.setVelocity(results.getVelocity());
        particle.setAcceleration(this.nextAcceleration);
    }
}
