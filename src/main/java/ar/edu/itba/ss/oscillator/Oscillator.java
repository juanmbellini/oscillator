package ar.edu.itba.ss.oscillator;

import ar.edu.itba.ss.g7.engine.io.DataSaver;
import ar.edu.itba.ss.g7.engine.simulation.SimulationEngine;
import ar.edu.itba.ss.oscillator.io.ProgramArguments;
import ar.edu.itba.ss.oscillator.models.DampedOscillator;
import ar.edu.itba.ss.oscillator.models.UpdateStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class.
 */
@SpringBootApplication
public class Oscillator implements CommandLineRunner, InitializingBean {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Oscillator.class);

    /**
     * The {@link DataSaver} for the ovito file.
     */
    private final DataSaver<DampedOscillator.DampedOscillatorState> ovitoFileSaver;

    /**
     * The {@link DataSaver} that will store the positions.
     */
    private final DataSaver<DampedOscillator.DampedOscillatorState> movementFileSaver;

    /**
     * The simulation engine.
     */
    private final SimulationEngine<DampedOscillator.DampedOscillatorState, DampedOscillator> engine;

    /**
     * Constructor.
     *
     * @param programArguments  The execution arguments.
     * @param ovitoFileSaver    The {@link DataSaver} for the ovito file.
     * @param movementFileSaver The {@link DataSaver} that will store the positions.
     */
    @Autowired
    public Oscillator(ProgramArguments programArguments,
                      @Qualifier("ovitoFileSaverImpl")
                              DataSaver<DampedOscillator.DampedOscillatorState> ovitoFileSaver,
                      @Qualifier("movementFileSaver")
                              DataSaver<DampedOscillator.DampedOscillatorState> movementFileSaver) {
        final double mass = programArguments.getParticleMass();
        final double initialX = programArguments.getInitialXPosition();
        final double springConstant = programArguments.getSpringConstant();
        final double viscousDampingConstant = programArguments.getViscousDampingCoefficient();
        final UpdateStrategy strategy = programArguments.getUpdateStrategyEnum().getStrategyInstance();
        final double step = programArguments.getTimeStep();
        final double time = programArguments.getTotalTime();

        final DampedOscillator dampedOscillator =
                new DampedOscillator(mass, initialX, springConstant, viscousDampingConstant, strategy, step, time);

        this.engine = new SimulationEngine<>(dampedOscillator);
        this.ovitoFileSaver = ovitoFileSaver;
        this.movementFileSaver = movementFileSaver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.engine.initialize();
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Hello, Oscillator!");
        // First, simulate
        simulate();
        // Then, save
        save();
        LOGGER.info("Bye-bye!");
        System.exit(0);
    }

    /**
     * Performs the simulation phase of the program.
     */
    private void simulate() {
        LOGGER.info("Starting simulation...");
        engine.simulate(oscillator -> oscillator.getActualTime() >= oscillator.getTotalTime());
        LOGGER.info("Finished simulation");
    }

    /**
     * Performs the save phase of the program.
     */
    private void save() {
        LOGGER.info("Saving outputs...");
        ovitoFileSaver.save(engine.getResults());
        movementFileSaver.save(engine.getResults());
        LOGGER.info("Finished saving output in all formats.");
    }

    /**
     * Entry point.
     *
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Oscillator.class, args);
    }
}
