package io.github.rajveer.simplotode;

import io.github.rajveer.simplotode.ode.*;
import io.github.rajveer.simplotode.systems.ODESystem;
import io.github.rajveer.simplotode.utils.Vector;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;

public class HelloController {

    @FXML private TextField expressionField;
    @FXML private TextField t0Field;
    @FXML private TextField y0Field;
    @FXML private TextField dtField;
    @FXML private TextField tEndField;
    @FXML private Label infoLabel;
    @FXML private ComboBox<String> solverBox;
    @FXML private LineChart<Number, Number> lineChart;

    private ODESolver solver;

    private final List<String> expressions = new ArrayList<>();
    private final List<Double> initialValues = new ArrayList<>();

    @FXML
    public void initialize() {
        solverBox.getItems().addAll("Euler Solver", "RK4 Solver", "Heun Solver", "Ralston Solver");
        infoLabel.setText("ODE Description");
    }

    @FXML
    void onAdd() {
        try {
            String exprStr = expressionField.getText().trim();
            double y0 = Double.parseDouble(y0Field.getText().trim());

            if (exprStr.isEmpty()) {
                infoLabel.setText("Expression is empty!");
                return;
            }

            expressions.add(exprStr);
            initialValues.add(y0);

            infoLabel.setText("Added: dy/dt = " + exprStr + " | y₀ = " + y0 +
                    "\nTotal ODEs: " + expressions.size());

            expressionField.clear();
            y0Field.clear();

        } catch (NumberFormatException e) {
            infoLabel.setText("Invalid y₀ value");
        }
    }

    @FXML
    public void onSolve() {
        try {
            // Select solver
            switch (solverBox.getValue()) {
                case "Euler Solver" -> solver = new EulerSolver();
                case "RK4 Solver" -> solver = new RK4Solver();
                case "Heun Solver" -> solver = new HeunSolver();
                case "Ralston Solver" -> solver = new RalstonSolver();
                default -> throw new IllegalArgumentException("Unknown solver: " + solverBox.getValue());
            }

            // Time parameters
            double t0 = Double.parseDouble(t0Field.getText().trim());
            double dt = Double.parseDouble(dtField.getText().trim());
            double tEnd = Double.parseDouble(tEndField.getText().trim());

            // Build expression array
            int n = expressions.size();
            if (n == 0) {
                infoLabel.setText("No ODEs added. Use 'Add' first.");
                return;
            }

            Expression[] exprArr = new Expression[n];
            for (int i = 0; i < n; i++) {
                exprArr[i] = new ExpressionBuilder(expressions.get(i))
                        .variables("t")
                        .variables(buildYVariables(n))
                        .build();
            }

            // Define system
            ODESystem system = (t, y) -> {
                double[] result = new double[n];
                for (int i = 0; i < n; i++) {
                    exprArr[i].setVariable("t", t);
                    for (int j = 0; j < n; j++) {
                        exprArr[i].setVariable("y" + j, y.get(j));
                    }
                    result[i] = exprArr[i].evaluate();
                }
                return new Vector(result);
            };

            // Initial values
            Vector y = new Vector(initialValues.stream().mapToDouble(Double::doubleValue).toArray());
            double t = t0;

            // Prepare chart
            lineChart.getData().clear();
            List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("y" + i + "(t)");
                seriesList.add(series);
            }

            // Solve and plot
            while (t <= tEnd) {
                for (int i = 0; i < n; i++) {
                    seriesList.get(i).getData().add(new XYChart.Data<>(t, y.get(i)));
                }
                y = solver.step(system, t, y, dt);
                t += dt;
            }

            lineChart.getData().addAll(seriesList);
            StringBuilder finalValues = new StringBuilder();
            for (int i = 0; i < n; i++) {
                finalValues.append("y").append(i).append("(").append(String.format("%.2f", t)).append(") = ")
                        .append(String.format("%.4f", y.get(i))).append("\n");
            }

            infoLabel.setText(
                    "✅ Solved " + n + " ODE(s)\n" +
                            "Solver: " + solverBox.getValue() + "\n" +
                            "Time range: [" + t0 + ", " + tEnd + "] with Δt = " + dt + "\n\n" +
                            "Final values at t = " + String.format("%.2f", t - dt) + ":\n" + finalValues
            );


        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Error: " + e.getMessage());
        }
    }

    private String[] buildYVariables(int n) {
        String[] vars = new String[n];
        for (int i = 0; i < n; i++) {
            vars[i] = "y" + i;
        }
        return vars;
    }
}
