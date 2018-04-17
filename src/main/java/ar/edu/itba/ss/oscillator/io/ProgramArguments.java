package ar.edu.itba.ss.oscillator.io;

import ar.edu.itba.ss.oscillator.models.Particle;
import ar.edu.itba.ss.oscillator.models.UpdateStrategy;
import ar.edu.itba.ss.oscillator.models.UpdateStrategyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Execution arguments
 */
@Component
public class ProgramArguments {

    /**
     * The oscillating {@link Particle}'s mass.
     */
    private final double particleMass;

    /**
     * The initial stretching/compressed distance of the oscillating {@link Particle}
     * (i.e if positive, it is stretched; if negative, it is compressed).
     */
    private final double initialXPosition;

    /**
     * The spring constant (in kilograms over square seconds).
     */
    private final double springConstant;

    /**
     * The viscous damping coefficient (in kilograms over seconds).
     */
    private final double viscousDampingCoefficient;

    /**
     * The {@link UpdateStrategyEnum} that will provide the {@link UpdateStrategy} to the system.
     */
    private final UpdateStrategyEnum updateStrategyEnum;

    /**
     * The time step (i.e how much time elapses between two update events).
     */
    private final double timeStep;

    /**
     * The total oscillating time.
     */
    private final double totalTime;

    /**
     * Constructor.
     *
     * @param particleMass              The oscillating {@link Particle}'s mass.
     * @param initialXPosition          The initial stretching/compressed distance of the oscillating {@link Particle}
     *                                  (i.e if positive, it is stretched; if negative, it is compressed).
     * @param springConstant            The spring constant (in kilograms over square seconds).
     * @param viscousDampingCoefficient The viscous damping coefficient (in kilograms over seconds).
     * @param updateStrategyEnum        The {@link UpdateStrategyEnum}
     *                                  that will provide the {@link UpdateStrategy} to the system.
     * @param timeStep                  The time step (i.e how much time elapses between two update events).
     * @param totalTime                 The total oscillating time.
     */
    @Autowired
    public ProgramArguments(@Value("${custom.system.particle-mass}") final double particleMass,
                            @Value("${custom.system.initial-x}") final double initialXPosition,
                            @Value("${custom.system.spring-constant}") final double springConstant,
                            @Value("${custom.system.viscous-damping-coefficient}") final double viscousDampingCoefficient,
                            @Value("${custom.simulation.strategy}") final UpdateStrategyEnum updateStrategyEnum,
                            @Value("${custom.simulation.time-step}") final double timeStep,
                            @Value("${custom.simulation.duration}") final double totalTime) {
        this.particleMass = particleMass;
        this.initialXPosition = initialXPosition;
        this.springConstant = springConstant;
        this.viscousDampingCoefficient = viscousDampingCoefficient;
        this.updateStrategyEnum = updateStrategyEnum;
        this.timeStep = timeStep;
        this.totalTime = totalTime;
    }

    /**
     * @return The oscillating {@link Particle}'s mass.
     */
    public double getParticleMass() {
        return particleMass;
    }

    /**
     * @return The initial stretching/compressed distance of the oscillating {@link Particle}
     * (i.e if positive, it is stretched; if negative, it is compressed).
     */
    public double getInitialXPosition() {
        return initialXPosition;
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
     * @return The {@link UpdateStrategyEnum} that will provide the {@link UpdateStrategy} to the system.
     */
    public UpdateStrategyEnum getUpdateStrategyEnum() {
        return updateStrategyEnum;
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
}
