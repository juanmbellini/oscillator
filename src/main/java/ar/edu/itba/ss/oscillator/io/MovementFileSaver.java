package ar.edu.itba.ss.oscillator.io;

import ar.edu.itba.ss.g7.engine.io.TextFileSaver;
import ar.edu.itba.ss.oscillator.models.DampedOscillator;
import ar.edu.itba.ss.oscillator.models.Particle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * {@link TextFileSaver} that saves all the positions the particle's goes through.
 */
@Component
public class MovementFileSaver extends TextFileSaver<DampedOscillator.DampedOscillatorState> {


    @Autowired
    public MovementFileSaver(@Value("${custom.output.movement}") String filePath) {
        super(filePath);
    }

    @Override
    public void doSave(Writer writer, Queue<DampedOscillator.DampedOscillatorState> queue) throws IOException {
        // Save particle's 'x' component of the position into the 'x' variable.
        final String x = "x = [" + queue.stream()
                .map(DampedOscillator.DampedOscillatorState::getParticleState)
                .map(Particle.ParticleState::getPosition)
                .map(Vector2D::getX)
                .map(Object::toString)
                .collect(Collectors.joining(", ")) + "];";
        // Save particle's 'y' component of the position into the 'y' variable.
        final String y = "y = [" + queue.stream()
                .map(DampedOscillator.DampedOscillatorState::getParticleState)
                .map(Particle.ParticleState::getPosition)
                .map(Vector2D::getY)
                .map(Object::toString)
                .collect(Collectors.joining(", ")) + "];";
        // Append results into the Writer
        writer.append(x)
                .append("\n")
                .append(y)
                .append("\n");

    }
}
