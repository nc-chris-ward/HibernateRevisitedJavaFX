package northcoders.hibernaterevisitedjavafx.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import northcoders.hibernaterevisitedjavafx.util.ReflectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassMenu extends Scene {

    private static final List<Class<?>> classes;
    private static final VBox mainLayout;
    private static final HashMap<Button, Class<?>> buttons;

    static {
        classes = ReflectionUtils.getClasses();

        mainLayout = new VBox(10);
        mainLayout.setPadding(new javafx.geometry.Insets(10));
        mainLayout.setAlignment(Pos.CENTER);

        buttons = new HashMap<>();
        for (Class<?> clazz : classes) {
            Button classButton = new Button(clazz.getSimpleName());
            classButton.setStyle("-fx-background-color: #3498db; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 36px; " +
                    "-fx-padding: 10px 20px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.0, 0, 0);");

            buttons.put(classButton, clazz);
        }
    }

    public ClassMenu(Stage primaryStage) {
        super(mainLayout, 800, 1100);
        for (Map.Entry<Button, Class<?>> buttonEntry : buttons.entrySet()) {
            buttonEntry.getKey().setOnAction(event -> {
                new ObjectCreationMenu(buttonEntry.getValue(), primaryStage);
            });
            mainLayout.getChildren().add(buttonEntry.getKey());
        }
    }
}
