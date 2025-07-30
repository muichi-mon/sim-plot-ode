module io.github.rajveer.simplotode {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.github.rajveer.simplotode to javafx.fxml;
    exports io.github.rajveer.simplotode.utils;
    exports io.github.rajveer.simplotode.ode;
    exports io.github.rajveer.simplotode.systems;
}