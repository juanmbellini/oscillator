package ar.edu.itba.ss.oscillator.models;

import ar.edu.itba.ss.g7.engine.models.System;
import ar.edu.itba.ss.g7.engine.simulation.State;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.function.Function;

/**
 * Represents a damped oscillator.
 */
public class DampedOscillator implements System<DampedOscillator.DampedOscillatorState> {

    /**
     * The particle to be oscillated.
     */
    private final Particle particle;

    /**
     * The spring constant (in kilograms over square seconds).
     */
    private final double springConstant;

    /**
     * The viscous damping coefficient (in kilograms over seconds).
     */
    private final double viscousDampingCoefficient;

    /**
     * A {@link Function} that takes a {@link Particle} and returns A {@link Vector2D}
     * that represents the acceleration the {@link Particle} is suffering.
     */
    private final Function<Particle, Vector2D> forceProvider;

    /**
     * The {@link Updater} used to calculate new values.
     */
    private final Updater updater;

    /**
     * The time step (i.e how much time elapses between two update events).
     */
    private final double timeStep;

    /**
     * The total oscillating time.
     */
    private final double totalTime;

    /**
     * The amount of time the system has been oscillating.
     */
    private double actualTime;


    /**
     * Constructor.
     *
     * @param particleMass              The oscillating {@link Particle}'s mass.
     * @param initialXPosition          The initial stretching/compressed distance of the oscillating {@link Particle}
     *                                  (i.e if positive, it is stretched; if negative, it is compressed).
     * @param springConstant            The spring constant (in kilograms over square seconds).
     * @param viscousDampingCoefficient The viscous damping coefficient (in kilograms over seconds).
     * @param updaterEnum               The {@link UpdateStrategyEnum} used to built the {@link Updater}
     *                                  that calculate new values and updates this damper oscillator.
     * @param timeStep                  The time step (i.e how much time elapses between two update events).
     * @param totalTime                 The total oscillating time.
     */
    public DampedOscillator(final double particleMass, final double initialXPosition,
                            final double springConstant, final double viscousDampingCoefficient,
                            UpdateStrategyEnum updaterEnum, final double timeStep, final double totalTime) {

        final Vector2D initialPosition = new Vector2D(initialXPosition, 0d);
        final Vector2D initialVelocity = new Vector2D(-viscousDampingCoefficient / (2 * particleMass), 0d); // TODO: check units issue (is acceleration?)
        final Vector2D initialAcceleration = Vector2D.ZERO;

        this.particle = new Particle(particleMass, initialPosition, initialVelocity, initialAcceleration);
        this.springConstant = springConstant;
        this.viscousDampingCoefficient = viscousDampingCoefficient;
        this.forceProvider = p -> {
            final Vector2D dampingForce = p.getVelocity().scalarMultiply(viscousDampingCoefficient);
            final Vector2D springForce = p.getPosition().scalarMultiply(springConstant);
            return springForce.add(dampingForce).scalarMultiply(-1);
        };
        this.timeStep = timeStep;
        this.totalTime = totalTime;
        this.actualTime = 0d;
        // Initialize the updater at the end.
        this.updater = updaterEnum.getStrategyInstance(this);
    }

    /**
     * @return The particle to be oscillated.
     */
    public Particle getParticle() {
        return particle;
    }

    /**
     * @return The spring constant (in kilograms over square seconds).
     */
    public double getSpringConstant() {
        return springConstant;
    }

    /**
     * @return The viscous damping coefficient (in kilograms over seconds).
     */
    public double getViscousDampingCoefficient() {
        return viscousDampingCoefficient;
    }

    /**
     * @return The time step (i.e how much time elapses between two update events).
     */
    public double getTimeStep() {
        return timeStep;
    }

    /**
     * @return The total oscillating time.
     */
    public double getTotalTime() {
        return totalTime;
    }

    /**
     * @return The amount of time the system has been oscillating.
     */
    public double getActualTime() {
        return actualTime;
    }

    /**
     * @return A {@link Function} that takes a {@link Particle} and returns A {@link Vector2D}
     * that represents the acceleration the {@link Particle} is suffering.
     */
    public Function<Particle, Vector2D> getForceProvider() {
        return forceProvider;
    }

    @Override
    public void update() {
        this.updater.update();
        this.actualTime += timeStep;
    }

    @Override
    public void restart() {
        // TODO: implement?
    }

    @Override
    public DampedOscillatorState outputState() {
        return new DampedOscillatorState(this);
    }

    /**
     * A {@link DampedOscillator} state.
     */
    public static final class DampedOscillatorState implements State {
        /**
         * The {@link State} of the {@link Particle} in the {@link DampedOscillator}.
         */
        private final Particle.ParticleState particleState;

        /**
         * Constructor.
         *
         * @param dampedOscillator The {@link DampedOscillator} whose state will be saved.
         */
        public DampedOscillatorState(DampedOscillator dampedOscillator) {
            this.particleState = dampedOscillator.getParticle().outputState();
        }

        /**
         * @return The {@link State} of the {@link Particle} in the {@link DampedOscillator}.
         */
        public Particle.ParticleState getParticleState() {
            return particleState;
        }
    }
}
