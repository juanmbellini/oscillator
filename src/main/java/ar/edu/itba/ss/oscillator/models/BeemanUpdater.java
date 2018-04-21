package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.function.Function;

/**
 * Implementation of {@link Updater} using Beeman's equations with prediction-correction.
 */
/* package */ class BeemanUpdater extends DampedOscillatorAbstractUpdater {

    /**
     * The previous acceleration.
     */
    private Vector2D previousAcceleration;

    /**
     * Constructor.
     *
     * @param dampedOscillator The {@link DampedOscillator} to be updated.
     */
    /* package */ BeemanUpdater(DampedOscillator dampedOscillator) {
        super(dampedOscillator);
        this.previousAcceleration = calculateInitialPreviousAcceleration(dampedOscillator);
    }


    @Override
    public void doUpdate(DampedOscillator dampedOscillator) {
        // Get values into scope
        final Particle particle = dampedOscillator.getParticle();
        final Vector2D actualPosition = particle.getPosition();
        final Vector2D actualVelocity = particle.getVelocity();
        final Vector2D actualAcceleration = particle.getAcceleration();
        final double timeStep = dampedOscillator.getTimeStep();
        final double mass = particle.getMass();
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
        final Vector2D accelerationResult = positionResult.scalarMultiply(dampedOscillator.getSpringConstant())
                .add(predictedVelocity.scalarMultiply(dampedOscillator.getViscousDampingCoefficient()))
                .scalarMultiply(-1 / mass);
        // Correct velocity using the calculated acceleration
        final Vector2D velocityResult = actualVelocity
                .add(accelerationResult.scalarMultiply((1d / 3d) * timeStep))
                .add(actualAcceleration.scalarMultiply((5d / 6d) * timeStep))
                .subtract(previousAcceleration.scalarMultiply((1d / 6d) * timeStep));

        this.previousAcceleration = actualAcceleration;
        particle.setPosition(positionResult);
        particle.setVelocity(velocityResult);
        particle.setAcceleration(accelerationResult);
    }


    /**
     * Calculates which is the previous acceleration in the initial step.
     *
     * @param dampedOscillator The {@link DampedOscillator} from where data is taken to perform the calculation.
     * @return The previous acceleration in the initial step
     */
    private static Vector2D calculateInitialPreviousAcceleration(DampedOscillator dampedOscillator) {
        final Particle particle = dampedOscillator.getParticle();
        final Vector2D actualPosition = particle.getPosition();
        final double timeStep = dampedOscillator.getTimeStep();
        final double mass = particle.getMass();
        final Function<Particle, Vector2D> forceProvider = dampedOscillator.getForceProvider();

        // Calculate actual force
        final Vector2D force = forceProvider.apply(particle);
        // Calculate velocity at -deltaT
        final Vector2D previousVelocity = particle.getVelocity()
                .subtract(force.scalarMultiply(timeStep / mass));
        // Calculate position at -deltaT
        final Vector2D previousPosition = actualPosition
                .subtract(previousVelocity.scalarMultiply(timeStep))
                .add(force.scalarMultiply((timeStep * timeStep) / (2 * mass)));
        // Calculate acceleration using the velocity and position
        return previousPosition.scalarMultiply(dampedOscillator.getSpringConstant())
                .add(previousVelocity.scalarMultiply(dampedOscillator.getViscousDampingCoefficient()))
                .scalarMultiply(-1 / mass);

    }
}
