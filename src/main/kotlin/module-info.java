module josfg333.projects.turtle {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens josfg333.projects.turtle to javafx.fxml;
    exports josfg333.projects.turtle;
}