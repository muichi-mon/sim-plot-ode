package io.github.rajveer.simplotode.ode;

import io.github.rajveer.simplotode.systems.ODESystem;
import io.github.rajveer.simplotode.utils.Vector;

public interface ODESolver {
    /**
     * Perform one step of the ODE solver.
     *
     * @param system the ODE system
     * @param t current time
     * @param y current state vector
     * @param dt time step
     * @return estimated state vector after time step
     */
    Vector step(ODESystem system, double t, Vector y, double dt);
}
