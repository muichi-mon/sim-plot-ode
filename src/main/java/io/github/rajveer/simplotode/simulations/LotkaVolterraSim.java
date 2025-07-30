package io.github.rajveer.simplotode.simulations;

import io.github.rajveer.simplotode.ode.RK4Solver;
import io.github.rajveer.simplotode.systems.LotkaVolterraSystem;
import io.github.rajveer.simplotode.systems.ODESystem;
import io.github.rajveer.simplotode.utils.Figure;
import io.github.rajveer.simplotode.utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class LotkaVolterraSim {

    public static void main(String[] args) {

        ODESystem system = new LotkaVolterraSystem(
                1.1,  // alpha: prey birth rate
                0.4,  // beta: predation rate
                0.1,  // delta: predator reproduction rate
                0.4   // gamma: predator death rate
        );

        RK4Solver solver = new RK4Solver();  // or EulerSolver

        Vector y = new Vector(new double[]{10.0, 5.0}); // [prey, predator]
        double t = 0;
        double dt = 0.1;

        List<double[]> preyPoints = new ArrayList<>();
        List<double[]> predatorPoints = new ArrayList<>();

        for (int i = 0; i < 300; i++) {
            preyPoints.add(new double[]{t, y.get(0)});
            predatorPoints.add(new double[]{t, y.get(1)});
            y = solver.step(system, t, y, dt);
            t += dt;
        }

        // Plot using JavaFX
        Figure.setTitle("Lotka-Volterra Simulation");
        Figure.setXLabel("Time");
        Figure.setYLabel("Population");
        Figure.addSeries("Prey", preyPoints);
        Figure.addSeries("Predator", predatorPoints);
        Figure.show();
    }
}
