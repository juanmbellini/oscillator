package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.function.Function;

/**
 * Implementation of {@link UpdateStrategy} using Gear's 5th-order predictor/corrector equations.
 */
public class GearStrategy implements UpdateStrategy {

    /**
     * The non derived vector.
     */
    private Vector2D nonDerived;

    /**
     * The vector derived once.
     */
    private Vector2D firstDerivative;

    /**
     * The vector derived twice.
     */
    private Vector2D secondDerivative;

    /**
     * The vector derived three times.
     */
    private Vector2D thirdDerivative;

    /**
     * The vector derived four times.
     */
    private Vector2D fourthDerivative;

    /**
     * The vector derived five times.
     */
    private Vector2D fifthDerivative;

    /**
     * Constructor.
     */
    public GearStrategy() {
        this.nonDerived = null;
        this.firstDerivative = null;
        this.secondDerivative = null;
        this.thirdDerivative = null;
        this.fourthDerivative = null;
        this.fifthDerivative = null;
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

        // First check if we are in the first step by checking if any of the vectors is null
        if (nonDerived == null) {
            // If here, we are in the first step.
            fifthDerivative = Vector2D.ZERO;
            fourthDerivative = Vector2D.ZERO;
            thirdDerivative = Vector2D.ZERO;
            secondDerivative = actualAcceleration;
            firstDerivative = actualVelocity;
            nonDerived = actualPosition;
        }

        // First predict
        final double firstFactor = timeStep;
        final double secondFactor = (timeStep * timeStep) / 2d;
        final double thirdFactor = (timeStep * timeStep * timeStep) / 6d;
        final double fourthFactor = (timeStep * timeStep * timeStep * timeStep) / 24d;
        final double fifthFactor = (timeStep * timeStep * timeStep * timeStep * timeStep) / 120d;

        final Vector2D nonDerivedPredicted = nonDerived
                .add(firstDerivative.scalarMultiply(firstFactor))
                .add(secondDerivative.scalarMultiply(secondFactor))
                .add(thirdDerivative.scalarMultiply(thirdFactor))
                .add(fourthDerivative.scalarMultiply(fourthFactor))
                .add(fifthDerivative.scalarMultiply(fifthFactor));
        final Vector2D firstDerivativePredicted = firstDerivative
                .add(secondDerivative.scalarMultiply(firstFactor))
                .add(thirdDerivative.scalarMultiply(secondFactor))
                .add(fourthDerivative.scalarMultiply(thirdFactor))
                .add(fifthDerivative.scalarMultiply(fourthFactor));
        final Vector2D secondDerivativePredicted = secondDerivative
                .add(thirdDerivative.scalarMultiply(firstFactor))
                .add(fourthDerivative.scalarMultiply(secondFactor))
                .add(fifthDerivative.scalarMultiply(thirdFactor));
        final Vector2D thirdDerivativePredicted = thirdDerivative
                .add(fourthDerivative.scalarMultiply(firstFactor))
                .add(fifthDerivative.scalarMultiply(secondFactor));
        final Vector2D fourthDerivativePredicted = fourthDerivative
                .add(fifthDerivative.scalarMultiply(firstFactor));
        final Vector2D fifthDerivativePredicted = fifthDerivative;

        // Then, calculate the force using predicted values
        final Vector2D force = nonDerivedPredicted.scalarMultiply(dampedOscillator.getSpringConstant())
                .add(firstDerivativePredicted.scalarMultiply(dampedOscillator.getViscousDampingCoeffcient()))
                .scalarMultiply(-1d);
        // With the force, calculate acceleration
        final Vector2D acceleration = force.scalarMultiply(1 / mass);
        // Compare acceleration with second derivative
        final Vector2D deltaAcceleration = acceleration.subtract(secondDerivativePredicted);
        // And then calculate deltaR2
        final Vector2D deltaR2 = deltaAcceleration.scalarMultiply(secondFactor); // (timeStep * timeStep) / 2

        // Now, correct
        final double correctFactor0 = 3d / 16d;
        final double correctFactor1 = (251d / 360d) / timeStep;
        final double correctFactor2 = 1d * (2d / (timeStep * timeStep));
        final double correctFactor3 = (11d / 18d) * (6d / (timeStep * timeStep * timeStep));
        final double correctFactor4 = (1d / 6d) * (24d / (timeStep * timeStep * timeStep * timeStep));
        final double correctFactor5 = 1d / 60d * (120d / (timeStep * timeStep * timeStep * timeStep * timeStep));
        this.nonDerived = nonDerivedPredicted.add(deltaR2.scalarMultiply(correctFactor0));
        this.firstDerivative = firstDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor1));
        this.secondDerivative = secondDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor2));
        this.thirdDerivative = thirdDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor3));
        this.fourthDerivative = fourthDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor4));
        this.fifthDerivative = fifthDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor5));


        return new UpdateResults(this.nonDerived, this.firstDerivative);
    }

    @Override
    public void update(DampedOscillator dampedOscillator) {
        final UpdateResults results = this.calculate(dampedOscillator);
        final Particle particle = dampedOscillator.getParticle();
        particle.setPosition(results.getPosition());
        particle.setVelocity(results.getVelocity());
        particle.setAcceleration(this.secondDerivative);
    }
}
