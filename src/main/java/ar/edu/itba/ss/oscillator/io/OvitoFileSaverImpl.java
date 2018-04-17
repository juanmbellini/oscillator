package ar.edu.itba.ss.oscillator.io;

import ar.edu.itba.ss.g7.engine.io.OvitoFileSaver;
import ar.edu.itba.ss.oscillator.models.DampedOscillator;
import ar.edu.itba.ss.oscillator.models.Particle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;

/**
 * {@link OvitoFileSaver} for the {@link DampedOscillator} {@link ar.edu.itba.ss.g7.engine.models.System}.
 */
@Component
public class OvitoFileSaverImpl extends OvitoFileSaver<DampedOscillator.DampedOscillatorState> {

    @Autowired
    public OvitoFileSaverImpl(@Value("${custom.output.ovito}") String filePath) {
        super(filePath);
    }

    @Override
    public void saveState(Writer writer, DampedOscillator.DampedOscillatorState state, int frame)
            throws IOException {

        final Particle.ParticleState particle = state.getParticleState();
        final StringBuilder data = new StringBuilder()
                // First, headers
                .append(4)
                .append("\n")
                .append(frame)
                .append("\n")
                // Store the particle
                .append(particle.getPosition().getX())
                .append(" ")
                .append(particle.getPosition().getY())
                .append(" ")
                .append(particle.getVelocity().getX())
                .append(" ")
                .append(particle.getVelocity().getY())
                .append("\n")
                // Store the origin point
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append("\n")
                // Store one limit
                .append(100)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append("\n")
                // Store another limit
                .append(-100)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append("\n");

        writer.append(data);
    }
}
