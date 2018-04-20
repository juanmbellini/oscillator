package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.function.Function;

/**
 * Concrete implementation of {@link AbstractSystemUpdater} using Verlet's equations.
 */
/* package */ class VerletUpdater extends DampedOscillatorAbstractUpdater {

    /**
     * The previous position, used for performing Verlet's recursive equations.
     */
    private Vector2D previousPosition;

    /**
     * Constructor.
     *
     * @param dampedOscillator The {@link DampedOscillator} to be updated.
     */
    /* package */ VerletUpdater(DampedOscillator dampedOscillator) {
        super(dampedOscillator);
        this.previousPosition = calculateInitialPreviousPosition(dampedOscillator);
    }

    @Override
    public void doUpdate(DampedOscillator dampedOscillator) {
        // Get values into scope
        final Particle particle = dampedOscillator.getParticle();
        final Vector2D actualPosition = particle.getPosition();
        final double timeStep = dampedOscillator.getTimeStep();
        final double mass = particle.getMass();
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();
        // Calculate position
        final Vector2D positionResult = actualPosition.scalarMultiply(2)
                .subtract(previousPosition)
                .add(forceProvider.apply(particle).scalarMultiply(timeStep * timeStep / mass));
        // Calculate velocity
        final double timeStepByViscousCoefficient = dampedOscillator.getViscousDampingCoefficient() * timeStep;
        final double timeStepBySpringConstant = dampedOscillator.getSpringConstant() * timeStep;
        final double doubleMass = 2 * mass;
        final double alpha = 1 + (timeStepByViscousCoefficient / doubleMass);
        final double beta = (1 / timeStep) - (timeStepBySpringConstant / doubleMass);
        final Vector2D velocityResult = positionResult.scalarMultiply(beta / alpha)
                .subtract(actualPosition.scalarMultiply(1 / (alpha * timeStep)));

        this.previousPosition = actualPosition;  // Save position
        particle.setPosition(positionResult);
        particle.setVelocity(velocityResult);
        // Calculate acceleration using new values
        particle.setAcceleration(forceProvider.apply(particle).scalarMultiply(1 / particle.getMass()));
    }

    /**
     * Calculates which is the previous position in the initial step.
     *
     * @param dampedOscillator The {@link DampedOscillator} from where data is taken to perform the calculation.
     * @return The previous position in the initial step
     */
    private static Vector2D calculateInitialPreviousPosition(DampedOscillator dampedOscillator) {
        final Particle particle = dampedOscillator.getParticle();
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();
        final double timeStep = dampedOscillator.getTimeStep();
        final Vector2D actualPosition = particle.getPosition();
        final double mass = particle.getMass();
        // Calculate actual force
        final Vector2D force = forceProvider.apply(particle);
        // Calculate velocity at -deltaT
        final Vector2D previousVelocity = particle.getVelocity()
                .subtract(force.scalarMultiply(timeStep / mass));
        // Return position at -deltaT
        return actualPosition
                .subtract(previousVelocity.scalarMultiply(timeStep))
                .add(force.scalarMultiply((timeStep * timeStep) / (2 * mass)));
    }
}
