package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Implementation of {@link Updater} using Gear's 5th-order predictor/corrector equations.
 */
public class GearUpdater extends DampedOscillatorAbstractUpdater {

    // ================================================================================================================
    // Derivatives
    // ================================================================================================================

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

    // ================================================================================================================
    // Factors used to predict
    // ================================================================================================================

    /**
     * The first factor used to predict.
     */
    private final double firstFactor;
    /**
     * The second factor used to predict.
     */
    private final double secondFactor;
    /**
     * The third factor used to predict.
     */
    private final double thirdFactor;
    /**
     * The fourth factor used to predict.
     */
    private final double fourthFactor;
    /**
     * The fifth factor used to predict.
     */
    private final double fifthFactor;

    // ================================================================================================================
    // Factors used to correct
    // ================================================================================================================

    /**
     * The first factor used to correct (i.e for the non derived).
     */
    private final double correctFactor0;
    /**
     * The second factor used to correct (i.e for the first derivative).
     */
    private final double correctFactor1;
    /**
     * The third factor used to correct (i.e for the second derivative).
     */
    private final double correctFactor2;
    /**
     * The fourth factor used to correct (i.e for the third derivative).
     */
    private final double correctFactor3;
    /**
     * The fifth factor used to correct (i.e for the fourth derivative).
     */
    private final double correctFactor4;
    /**
     * The sixth factor used to correct (i.e for the fifth derivative).
     */
    private final double correctFactor5;

    /**
     * Constructor.
     *
     * @param dampedOscillator The {@link DampedOscillator} to be updated.
     */
    /* package */ GearUpdater(DampedOscillator dampedOscillator) {
        super(dampedOscillator);
        final Particle particle = dampedOscillator.getParticle();
        this.nonDerived = particle.getPosition();
        this.firstDerivative = particle.getVelocity();
        this.secondDerivative = particle.getAcceleration();
        this.thirdDerivative = Vector2D.ZERO;
        this.fourthDerivative = Vector2D.ZERO;
        this.fifthDerivative = Vector2D.ZERO;
        final double timeStep = dampedOscillator.getTimeStep();
        this.firstFactor = timeStep;
        this.secondFactor = (timeStep * timeStep) / 2d;
        this.thirdFactor = (timeStep * timeStep * timeStep) / 6d;
        this.fourthFactor = (timeStep * timeStep * timeStep * timeStep) / 24d;
        this.fifthFactor = (timeStep * timeStep * timeStep * timeStep * timeStep) / 120d;
        this.correctFactor0 = 3d / 16d;
        this.correctFactor1 = (251d / 360d) / timeStep;
        this.correctFactor2 = 1d * (2d / (timeStep * timeStep));
        this.correctFactor3 = (11d / 18d) * (6d / (timeStep * timeStep * timeStep));
        this.correctFactor4 = (1d / 6d) * (24d / (timeStep * timeStep * timeStep * timeStep));
        this.correctFactor5 = (1d / 60d) * (120d / (timeStep * timeStep * timeStep * timeStep * timeStep));
    }

    @Override
    public void doUpdate(DampedOscillator dampedOscillator) {
        // Get values into scope
        final Particle particle = dampedOscillator.getParticle();
        // First predict
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
                .add(firstDerivativePredicted.scalarMultiply(dampedOscillator.getViscousDampingCoefficient()))
                .scalarMultiply(-1d);
        // With the force, calculate acceleration
        final Vector2D acceleration = force.scalarMultiply(1 / particle.getMass());
        // Compare acceleration with second derivative
        final Vector2D deltaAcceleration = acceleration.subtract(secondDerivativePredicted);
        // And then calculate deltaR2
        final Vector2D deltaR2 = deltaAcceleration.scalarMultiply(secondFactor); // (timeStep * timeStep) / 2

        // Now, correct
        this.nonDerived = nonDerivedPredicted.add(deltaR2.scalarMultiply(correctFactor0));
        this.firstDerivative = firstDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor1));
        this.secondDerivative = secondDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor2));
        this.thirdDerivative = thirdDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor3));
        this.fourthDerivative = fourthDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor4));
        this.fifthDerivative = fifthDerivativePredicted.add(deltaR2.scalarMultiply(correctFactor5));

        particle.setPosition(this.nonDerived);
        particle.setVelocity(this.firstDerivative);
        particle.setAcceleration(this.secondDerivative);
    }
}
