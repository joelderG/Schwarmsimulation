package simulation.behaviors;

import engine.math.Vector2D;
import simulation.agents.Agent;

import java.util.List;

public class SwimmingBehavior extends Behavior {
    private double amplitude;
    private double frequency;
    private double phaseOffset;
    private double time;

    public SwimmingBehavior(double weight, double amplitude, double frequency) {
        super(weight, 0.0);
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.phaseOffset = Math.random() * 2 * Math.PI;
        this.time = 0.0;
    }

    @Override
    public Vector2D calculateForce(Agent agent, List<Agent> neighbors) {
        time += 0.016;

        // Calculate oscillation based on swimming motion
        double phase = time * frequency + phaseOffset;
        double oscillation = Math.sin(phase) * amplitude;

        Vector2D sideForce = new Vector2D();
        if (!agent.velocity.isNullvector()) {
            sideForce.x = -agent.velocity.y;
            sideForce.y = agent.velocity.x;
            sideForce.normalize();
            sideForce.mult(oscillation);
        }

        Vector2D forwardForce = new Vector2D();
        if (!agent.velocity.isNullvector()) {
            forwardForce = new Vector2D(agent.velocity);
            forwardForce.normalize();

            double thrust = Math.abs(Math.cos(phase)) * amplitude * 0.5;
            forwardForce.mult(thrust);
        }

        Vector2D totalForce = new Vector2D(sideForce);
        totalForce.add(forwardForce);

        return totalForce;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getFrequency() {
        return frequency;
    }

    public void resetTime() {
        this.time = 0.0;
    }
}
