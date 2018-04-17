package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of {@link UpdateStrategy} using Verlet's equations.
 */
/* package */ class VerletStrategy implements UpdateStrategy {

    /**
     * The previous position, used for performing Verlet's recursive equations.
     */
    private Vector2D previousPosition;

    /**
     * Constructor.
     */
    /* package */ VerletStrategy() {
        this.previousPosition = null;
    }

    @Override
    public UpdateResults calculate(DampedOscillator dampedOscillator) {
        // Get values into scope
        final Particle particle = dampedOscillator.getParticle();
        final Vector2D actualPosition = particle.getPosition();
        final double timeStep = dampedOscillator.getTimeStep();
        final double mass = particle.getMass();
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();
        // Calculate position
        final Vector2D positionResult = Optional.ofNullable(previousPosition)
                .map(prev -> actualPosition.scalarMultiply(2)
                        .subtract(prev)
                        .add(forceProvider.apply(particle).scalarMultiply(timeStep * timeStep / mass)))
                .orElseGet(() -> {
                    // If here, the previous position has not been calculated yet, which means this is the first step
                    final Vector2D force = forceProvider.apply(particle);
                    // Calculate velocity at -deltaT
                    final Vector2D previousVelocity = particle.getVelocity()
                            .subtract(force.scalarMultiply(timeStep / mass));
                    return actualPosition
                            .subtract(previousVelocity.scalarMultiply(timeStep))
                            .add(force.scalarMultiply((timeStep * timeStep) / (2 * mass)));
                });

        // Calculate velocity
        final double timeStepByViscousCoefficient = dampedOscillator.getViscousDampingCoeffcient() * timeStep;
        final double timeStepBySpringConstant = dampedOscillator.getSpringConstant() * timeStep;
        final double doubleMass = 2 * mass;
        final double alpha = 1 + (timeStepByViscousCoefficient / doubleMass);
        final double beta = (1 / timeStep) - (timeStepBySpringConstant / doubleMass);
        final Vector2D velocityResult = positionResult.scalarMultiply(beta / alpha)
                .subtract(actualPosition.scalarMultiply(1 / (alpha * timeStep)));

        return new UpdateResults(positionResult, velocityResult);
    }

    @Override
    public void update(DampedOscillator dampedOscillator) {
        final UpdateResults results = this.calculate(dampedOscillator);
        final Particle particle = dampedOscillator.getParticle();
        this.previousPosition = particle.getPosition();
        particle.setPosition(results.getPosition());
        particle.setVelocity(results.getVelocity());
        // Calculate acceleration using new values
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();
        particle.setAcceleration(forceProvider.apply(particle).scalarMultiply(1 / particle.getMass()));
    }
}
