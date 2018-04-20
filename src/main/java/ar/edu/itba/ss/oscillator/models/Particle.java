package ar.edu.itba.ss.oscillator.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Represents a particle in the system.
 */
public class Particle implements StateHolder<Particle.ParticleState> {

    /**
     * The particle's mass (in kilograms).
     */
    private final double mass;

    /**
     * The particle's position (represented as a 2D vector).
     */
    private Vector2D position;

    /**
     * The particle's velocity (represented as a 2D vector).
     */
    private Vector2D velocity;

    /**
     * The particle's acceleration (represented as a 2D vector)
     */
    private Vector2D acceleration;


    /**
     * Constructor.
     *
     * @param mass                The particle's mass (in kilograms).
     * @param initialPosition     The starting position of the particle.
     * @param initialVelocity     The starting velocity of the particle.
     * @param initialAcceleration The starting acceleration of the particle.
     */
    public Particle(final double mass,
                    final Vector2D initialPosition,
                    final Vector2D initialVelocity,
                    final Vector2D initialAcceleration) {
        this.mass = mass;
        this.position = initialPosition;
        this.velocity = initialVelocity;
        this.acceleration = initialAcceleration;
    }

    /**
     * @return The particle's mass.
     */
    public double getMass() {
        return this.mass;
    }

    /**
     * @return The particle's position (represented as a 2D vector).
     */
    public Vector2D getPosition() {
        return this.position;
    }

    /**
     * @return The particle's velocity (represented as a 2D vector).
     */
    public Vector2D getVelocity() {
        return this.velocity;
    }

    /**
     * @return The particle's aceleration (represented as a 2D vector).
     */
    public Vector2D getAcceleration() {
        return this.acceleration;
    }

    /**
     * Sets a new position.
     *
     * @param position The new position.
     */
    public void setPosition(Vector2D position) {
        this.position = position;
    }

    /**
     * Sets a new velocity.
     *
     * @param velocity The new velocity.
     */
    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    /**
     * Sets a new acceleration.
     *
     * @param acceleration The new acceleration.
     */
    public void setAcceleration(Vector2D acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public ParticleState outputState() {
        return new ParticleState(this);
    }

    /**
     * Represents the state of a given particle.o
     */
    public static final class ParticleState implements State {

        /**
         * The {@link Particle}'s mass.
         */
        private final double mass;

        /**
         * The {@link Particle}'s position (represented as a 2D vector).
         */
        private final Vector2D position;

        /**
         * The {@link Particle}'s velocity (represented as a 2D vector).
         */
        private final Vector2D velocity;

        /**
         * The {@link Particle}'s acceleration (represented as a 2D vector).
         */
        private final Vector2D acceleration;

        /**
         * Constructor.
         *
         * @param particle The {@link Particle}'s whose state will be represented.
         */
        private ParticleState(final Particle particle) {
            this.mass = particle.getMass();
            this.position = particle.getPosition(); // The Vector2D class is unmodifiable.
            this.velocity = particle.getVelocity(); // The Vector2D class is unmodifiable.
            this.acceleration = particle.getAcceleration(); // The Vector2D class is unmodifiable.
        }

        /**
         * The {@link Particle}'s mass.
         */
        public double getMass() {
            return mass;
        }

        /**
         * The {@link Particle}'s position (represented as a 2D vector).
         */
        public Vector2D getPosition() {
            return position;
        }

        /**
         * The {@link Particle}'s velocity (represented as a 2D vector).
         */
        public Vector2D getVelocity() {
            return velocity;
        }

        /**
         * The {@link Particle}'s velocity (represented as a 2D vector).
         */
        public Vector2D getAcceleration() {
            return acceleration;
        }
    }
}