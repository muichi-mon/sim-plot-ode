package io.github.rajveer.simplotode;

import io.github.rajveer.simplotode.ode.EulerSolver;
import io.github.rajveer.simplotode.ode.ODESolver;
import io.github.rajveer.simplotode.systems.ODESystem;
import io.github.rajveer.simplotode.utils.Vector;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Stack;

public class HelloController {

    @FXML private TextField expressionField;
    @FXML private TextField t0Field;
    @FXML private TextField y0Field;
    @FXML private TextField dtField;
    @FXML private TextField tEndField;
    @FXML private LineChart<Number, Number> lineChart;

    private final ODESolver solver = new EulerSolver(); // Replace with any ODESolver

    @FXML
    public void onSolve() {
        try {
            double t0 = Double.parseDouble(t0Field.getText());
            double y0 = Double.parseDouble(y0Field.getText());
            double dt = Double.parseDouble(dtField.getText());
            double tEnd = Double.parseDouble(tEndField.getText());
            String exprStr = expressionField.getText();

            Expression expr = new ExpressionBuilder(exprStr)
                    .variables("t", "y")
                    .build();

            ODESystem system = (t, y) -> {
                expr.setVariable("t", t);
                expr.setVariable("y", y.get(0));
                return new Vector(new double[]{expr.evaluate()});
            };

            lineChart.getData().clear();
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("y(t)");

            double t = t0;
            Vector y = new Vector(new double[]{y0});
            while (t <= tEnd) {
                series.getData().add(new XYChart.Data<>(t, y.get(0)));
                y = solver.step(system, t, y, dt);
                t += dt;
            }

            lineChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * checks if the String ODE function has balanced parenthesis
     * @param expression String ODE function
     * @return boolean
     */
    public static boolean isBalanced(String expression) {
        Stack<Character> stack = new Stack<>();

        for (char ch : expression.toCharArray()) {

            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
            }

            else if (ch == ')' || ch == ']' || ch == '}') {
                if (stack.isEmpty()) {
                    return false; // Unmatched closing bracket
                }
                char topElement = stack.pop(); // Get the most recent opening bracket

                if ((ch == ')' && topElement != '(') ||
                        (ch == ']' && topElement != '[') ||
                        (ch == '}' && topElement != '{')) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    /**
     * Create quick Alert pop-ups
     * @param message Warning message
     */
    public static void showAlertBox(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Selection");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
