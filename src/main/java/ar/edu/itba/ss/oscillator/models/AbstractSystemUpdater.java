package ar.edu.itba.ss.oscillator.models;

import ar.edu.itba.ss.g7.engine.models.System;
import ar.edu.itba.ss.g7.engine.simulation.State;

/**
 * An abstract {@link Updater}, which is in charge of updating a {@link System}.
 *
 * @param <E> The concrete type of {@link State} the wrapped {@link System} outputs.
 * @param <S> The concrete type of {@link System} being wrapped.
 */
public abstract class AbstractSystemUpdater<E extends State, S extends System<E>> implements Updater {

    /**
     * The system to be updated.
     */
    private final S system;

    /**
     * Constructor.
     *
     * @param system The system to be updated.
     */
    protected AbstractSystemUpdater(S system) {
        this.system = system;
    }

    @Override
    public void update() {
        doUpdate(system);
    }

    /**
     * Defines how extension of this class updated the wrapped {@link #system}.
     */
    protected abstract void doUpdate(S system);
}
