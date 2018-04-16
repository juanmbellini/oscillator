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
     * The particle's previous acceleration (represented as a 2D vector)
     */
    private Vector2D previousAcceleration;

    /**
     * The particle's previous position (represented as a 2D vector)
     */
    private Vector2D previousPosition;

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

        this.previousAcceleration = null;
        this.previousPosition = null;
    }

    /**
     * Constructor.
     *
     * @param mass      The particle's mass (in kilograms).
     * @param xPosition The particle's 'x' component of the position.
     * @param xVelocity The particle's 'x' component of the velocity.
     */
    public Particle(final double mass, final double xPosition, final double xVelocity) {
        this(mass, new Vector2D(xPosition, 0), new Vector2D(xVelocity, 0), new Vector2D(0, 0));
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
     * @return The particle's kinetic energy.
     */
    public double getKineticEnergy() {
        return 0.5 * mass * velocity.getNormSq();
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


    // ================================================================================================================
    // Verlet equations
    // ================================================================================================================

    /**
     * Update the particle's state according to the Verlet's equations, using the given {@code deltaTime}.
     *
     * @param deltaTime The time variable of the linear motion equation.
     * @param k         The oscilator's constant.
     * @param gamma     The constant value that resolves the differential equation for the oscilator.
     */
    public void verletUpdate(final double deltaTime, final int k, final int gamma) {
        final Vector2D newPosition = verletMovementUpdate(deltaTime);
        verletVelocityUpdate(deltaTime, newPosition);

        previousPosition = position;
        position = newPosition;

        verletAccelerationUpdate(deltaTime, k, gamma);
    }

    /**
     * Moves this particle according to the Verlet equation, using the given {@code deltaTime}.
     *
     * @param deltaTime The time variable of the linear motion equation.
     * @return The new position of the particle.
     */
    public Vector2D verletMovementUpdate(final double deltaTime) {
        final double xPosition =
                2 * position.getX()
                        - previousPosition.getX()
                        + Math.pow(deltaTime, 2) / mass; //TODO: * Fi(t) where Fi is the taylor component

        return new Vector2D(xPosition, 0);
    }

    /**
     * Updates the velocity for this particle according to the Verlet equation, using the given {@code deltaTime}.
     *
     * @param deltaTime   The time variable of the linear motion equation.
     * @param newPosition The position of the particle at t + deltaTime
     */
    public void verletVelocityUpdate(final double deltaTime, final Vector2D newPosition) {
        final double xVelocity = (newPosition.getX() - (previousPosition == null ? 0 : previousPosition.getX()))
                / (2 * deltaTime);

        velocity = new Vector2D(xVelocity, 0);
    }

    /**
     * Updates the acceleration for this particle according to the Verlet equation, using the given {@code deltaTime}.
     *
     * @param deltaTime The time variable of the linear motion equation.
     * @param k         The oscilator's constant.
     * @param gamma     The constant value that resolves the differential equation for the oscilator.
     * @return The Particle's new acceleration.
     */
    public void verletAccelerationUpdate(final double deltaTime, final int k, final int gamma) {
        previousAcceleration = acceleration;
        final double xAcceleration = -(k * position.getX() + gamma * velocity.getX()) / mass;

        acceleration = new Vector2D(xAcceleration, 0);
    }

    // ================================================================================================================
    // Beeman equations
    // ================================================================================================================

    /**
     * Update the particle's state according to the Beeman's equations, using the given {@code deltaTime}.
     *
     * @param deltaTime        The time variable of the linear motion equation.
     * @param enablePrediction A flag indicating the use of Beeman's prediction equations.
     * @param k                The oscilator's constant.
     * @param gamma            The constant value that resolves the differential equation for the oscilator.
     */
    public void beemanUpdate(final double deltaTime, final boolean enablePrediction, final int k, final int gamma) {
        beemanMovementUpdate(deltaTime);
        beemanVelocityUpdate(deltaTime, enablePrediction, k, gamma); //This equation will update the acceleration
    }

    /**
     * Moves this particle according to the Beeman equation, using the given {@code deltaTime}.
     *
     * @param deltaTime The time variable of the linear motion equation.
     */
    public void beemanMovementUpdate(final double deltaTime) {
        final double xPosition =
                position.getX()
                        + velocity.getX() * deltaTime
                        + (2 / 3 * acceleration.getX() - 1 / 6 * previousAcceleration.getX())
                        * Math.pow(deltaTime, 2);

        this.position = new Vector2D(xPosition, 0);
    }

    /**
     * Updates the velocity for this particle according to the Beeman equation, using the given {@code deltaTime}.
     *
     * @param deltaTime        The time variable of the linear motion equation.
     * @param enablePrediction A flag indicating the use of Beeman's prediction equations.
     * @param k                The oscilator's constant.
     * @param gamma            The constant value that resolves the differential equation for the oscilator.
     */
    public void beemanVelocityUpdate(final double deltaTime, final boolean enablePrediction,
                                     final int k, final int gamma) {
        final double xVelocity =
                velocity.getX()
                        + (5 * acceleration.getX()
                        - previousAcceleration.getX()
                        + 2 * beemanAccelerationUpdate(deltaTime, enablePrediction, k, gamma).getX()) * deltaTime / 6;

        this.velocity = new Vector2D(xVelocity, 0);
    }

    /**
     * Updates the acceleration for this particle according to the Beeman equation, using the given {@code deltaTime}.
     *
     * @param deltaTime        The time variable of the linear motion equation.
     * @param enablePrediction A flag indicating the use of Beeman's prediction equations.
     * @param k                The oscilator's constant.
     * @param gamma            The constant value that resolves the differential equation for the oscilator.
     * @return The Particle's new acceleration.
     */
    public Vector2D beemanAccelerationUpdate(final double deltaTime, final boolean enablePrediction,
                                             final int k, final int gamma) {
        previousAcceleration = acceleration;
        final double xAcceleration = -(k * position.getX() + gamma *
                (enablePrediction ? beemanPredictedVelocity(deltaTime).getX() : velocity.getX())) / mass;

        acceleration = new Vector2D(xAcceleration, 0);
        return acceleration;
    }

    private Vector2D beemanPredictedVelocity(final double deltaTime) {
        final double xVelocity =
                velocity.getX()
                        + (3 * acceleration.getX()
                        - previousAcceleration.getX()) * deltaTime / 2;

        return new Vector2D(xVelocity, 0);
    }

    // ================================================================================================================
    // Order 5 Gear Predictor-Corrector equations
    // ================================================================================================================

    /**
     * Update the particle's state using order 5 equations of the predictor-corrector algorithm,
     * using the given {@code deltaTime}.
     *
     * @param deltaTime The time variable of the linear motion equation.
     */
    public void gearPredictorCorrectorUpdate(final double deltaTime) {
        predict(deltaTime);
        evaluate(deltaTime);
        correct(deltaTime);
    }

    /**
     * Prediction step, predicts the particle equations, using the given {@code deltaTime}.
     *
     * @param deltaTime The time variable of the linear motion equation.
     */
    public void predict(final double deltaTime) {
        //TBD
    }

    /**
     * Evaluation step, evaluate the forces using the predicted variables using the given {@code deltaTime}.
     *
     * @param deltaTime The time variable of the linear motion equation.
     */
    public void evaluate(final double deltaTime) {
        //TBD
    }

    /**
     * Correction step, correct the given values using the gear coefficient, using the given {@code deltaTime}.
     *
     * @param deltaTime The time variable of the linear motion equation.
     */
    public void correct(final double deltaTime) {
        //TBD
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