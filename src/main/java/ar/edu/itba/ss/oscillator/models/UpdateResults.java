package ar.edu.itba.ss.oscillator.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Class holding results of applying update operations.
 */
public class UpdateResults {

    /**
     * The calculated position.
     */
    private final Vector2D position;

    /**
     * The calculated velocity.
     */
    private final Vector2D velocity;

    /**
     * Constructor
     *
     * @param position The calculated position.
     * @param velocity The calculated velocity.
     */
    public UpdateResults(Vector2D position, Vector2D velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    /**
     * @return The calculated position.
     */
    public Vector2D getPosition() {
        return position;
    }

    /**
     * @return The calculated velocity.
     */
    public Vector2D getVelocity() {
        return velocity;
    }
}
