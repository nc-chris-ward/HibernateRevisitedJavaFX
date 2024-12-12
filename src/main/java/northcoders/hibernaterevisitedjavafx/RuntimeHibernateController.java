package northcoders.hibernaterevisitedjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RuntimeHibernateController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}