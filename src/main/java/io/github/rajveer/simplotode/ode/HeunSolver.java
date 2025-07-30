package io.github.rajveer.simplotode.ode;

import io.github.rajveer.simplotode.systems.ODESystem;
import io.github.rajveer.simplotode.utils.Vector;

/**
 * A second-order Runge-Kutta ODE solver using Heun's method (Improved Euler).
 * It averages the slope at the start and end of the interval.
 *
 * y(t + dt) ≈ y + (dt/2) * (k1 + k2)
 */
public class HeunSolver implements ODESolver {

    /**
     * Perform one Heun RK2 step.
     *
     * @param system the ODE system
     * @param t current time
     * @param y current state vector
     * @param dt time step
     * @return estimated state vector at t + dt
     */
    @Override
    public Vector step(ODESystem system, double t, Vector y, double dt) {
        Vector k1 = system.computeDerivative(t, y);
        Vector k2 = system.computeDerivative(t + dt, y.add(k1.scale(dt)));

        Vector averageSlope = k1.add(k2).scale(0.5);
        return y.add(averageSlope.scale(dt));
    }
}
