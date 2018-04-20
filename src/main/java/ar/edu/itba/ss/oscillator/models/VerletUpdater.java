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
     * The previous velocity, used for approximating the force.
     */
    private Vector2D previousVelocity;

    /**
     * Constructor.
     *
     * @param dampedOscillator The {@link DampedOscillator} to be updated.
     */
    /* package */ VerletUpdater(DampedOscillator dampedOscillator) {
        super(dampedOscillator);
        final Particle particle = dampedOscillator.getParticle();
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();
        final double timeStep = dampedOscillator.getTimeStep();
        final Vector2D actualPosition = particle.getPosition();
        final double mass = particle.getMass();
        // Calculate actual force
        final Vector2D force = forceProvider.apply(particle);
        // Calculate velocity at -deltaT
        this.previousVelocity = particle.getVelocity()
                .subtract(force.scalarMultiply(timeStep / mass));
        // Return position at -deltaT
        this.previousPosition = actualPosition
                .subtract(previousVelocity.scalarMultiply(timeStep))
                .add(force.scalarMultiply((timeStep * timeStep) / (2 * mass)));
    }

    @Override
    public void doUpdate(DampedOscillator dampedOscillator) {
        // Get values into scope
        final Particle particle = dampedOscillator.getParticle();
        final Vector2D actualPosition = particle.getPosition();
        final double timeStep = dampedOscillator.getTimeStep();
        final double mass = particle.getMass();
        // Approximate force
        final Vector2D approximateForce = actualPosition.scalarMultiply(dampedOscillator.getSpringConstant())
                .add(previousVelocity.scalarMultiply(dampedOscillator.getViscousDampingCoefficient()))
                .scalarMultiply(-1d);
        // Calculate the next position with approximated force
        final Vector2D positionResult = actualPosition.scalarMultiply(2)
                .subtract(previousPosition)
                .add(approximateForce.scalarMultiply(timeStep * timeStep / mass));
        // Calculate velocity for previous step
        this.previousVelocity = positionResult
                .subtract(previousPosition)
                .scalarMultiply(1d / (2d * timeStep));
        // Calculate acceleration using the calculated position and the previous velocity)
        final Vector2D approximatedAcceleration = positionResult.scalarMultiply(dampedOscillator.getSpringConstant())
                .add(previousVelocity.scalarMultiply(dampedOscillator.getViscousDampingCoefficient()))
                .scalarMultiply(-1d / mass);

        this.previousPosition = actualPosition;  // Save position
        particle.setPosition(positionResult);
        // This method calculates velocity out of phase, so we can't save it in the particle
        // So, we will always store the velocity as a zero vector.
        particle.setVelocity(Vector2D.ZERO);
        particle.setAcceleration(approximatedAcceleration);
    }
}
