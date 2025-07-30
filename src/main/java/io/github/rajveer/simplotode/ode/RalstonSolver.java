package io.github.rajveer.simplotode.ode;

import io.github.rajveer.simplotode.systems.ODESystem;
import io.github.rajveer.simplotode.utils.Vector;

/**
 * A second-order Runge-Kutta ODE solver using Ralston's method.
 * This method is more accurate than the midpoint method due to better weighting.
 *
 * y(t + dt) ≈ y + dt * (1/4 * k1 + 3/4 * k2)
 */
public class RalstonSolver implements ODESolver {

    /**
     * Perform one Ralston RK2 step.
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
        Vector k2 = system.computeDerivative(
                t + (2.0 / 3.0) * dt,
                y.add(k1.scale(2.0 / 3.0 * dt))
        );

        Vector weightedSum = k1.scale(0.25).add(k2.scale(0.75));
        return y.add(weightedSum.scale(dt));
    }
}
