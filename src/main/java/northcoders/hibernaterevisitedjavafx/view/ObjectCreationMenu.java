package northcoders.hibernaterevisitedjavafx.view;

import jakarta.persistence.GeneratedValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.text.Text;
import northcoders.hibernaterevisitedjavafx.repository.H2Repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ObjectCreationMenu extends Stage {

    private final Field[] objectFields;
    private final Map<String, Control> inputFields = new HashMap<>();
    private final VBox formLayout = new VBox(10);
    private final H2Repository repository = new H2Repository();

    public ObjectCreationMenu(Class<?> clazz, Stage previousStage) {
        Stage formStage = new Stage();

        Screen secondaryScreen = Screen.getScreens().get(1);
        Rectangle2D bounds = secondaryScreen.getVisualBounds();

        formStage.setX(bounds.getMinX());
        formStage.setY(bounds.getMinY());

        formLayout.setPadding(new javafx.geometry.Insets(10));
        formLayout.setAlignment(Pos.CENTER);

        objectFields = clazz.getDeclaredFields();

        generateInputFields();

        Button submitButton = createSubmitButton(clazz, previousStage, formStage);

        Button backButton = createBackButton(previousStage, formStage);

        formLayout.getChildren().addAll(submitButton, backButton);
        Scene scene = new Scene(formLayout, 700, 1000);
        formStage.setTitle("Create new " + clazz.getSimpleName());
        formStage.setScene(scene);
        formStage.show();
    }

    private Button createSubmitButton(Class<?> clazz, Stage previousStage, Stage formStage) {
        Button submitButton = new Button("Create Object");
        submitButton.setOnAction(event -> {
            try {
                Object instance = generateObjectInstance(clazz);
                repository.persist(instance, objectFields, inputFields);
                showSuccessPopup(formStage);
                formStage.close();
                previousStage.show();
            }
            catch (Exception e) {
                showFailurePopup(formStage, e);
            }
        });
        submitButton.setStyle("-fx-background-color: #71bb7b; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 36px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-font-weight: bold; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.0, 0, 0);");
        return submitButton;
    }

    private static Button createBackButton(Stage previousStage, Stage formStage) {
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            formStage.close();
            previousStage.show();
        });
        backButton.setStyle("-fx-background-color: #cfc79d; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 36px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-font-weight: bold; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.0, 0, 0);");
        return backButton;
    }

    private static Object generateObjectInstance(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private void generateInputFields() {
        for (Field field : objectFields) {
            if (field.isAnnotationPresent(GeneratedValue.class)) continue;
            Label label = new Label(field.getName() + "   ");
            Control inputControl;

            inputControl = determineFieldControlType(field);

            label.setStyle("-fx-font-size: 28px; -fx-font-weight: normal; -fx-text-fill: #333;");

            inputControl.setStyle("-fx-font-size: 16px; " +
                    "-fx-background-color: #f0f0f0; " +
                    "-fx-border-color: #3498db; " +
                    "-fx-border-width: 2px; " +
                    "-fx-padding: 10px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-pref-width: 200px;" +
                    "-fx-alignment: center;");

            HBox hbox = new HBox(30, label, inputControl);
            hbox.setStyle("-fx-spacing: 10px; -fx-padding: 10px; -fx-alignment: center;");
            hbox.setAlignment(Pos.CENTER);

            formLayout.getChildren().add(hbox);
            formLayout.setAlignment(Pos.CENTER);
            inputFields.put(field.getName(), inputControl);
        }
    }

    private static Control determineFieldControlType(Field field) {
        Control inputControl;
        if (field.getType() == String.class) {
            inputControl = new TextField();
        } else if (field.getType() == int.class) {
            inputControl = new TextField();
            ((TextField) inputControl).setTextFormatter(new TextFormatter<>(change -> {
                if (change.getText().matches("\\d*")) {
                    return change;
                }
                return null;
            }));
        } else if (field.getType() == double.class) {
            inputControl = new TextField();
            ((TextField) inputControl).setTextFormatter(new TextFormatter<>(change -> {
                if (change.getText().matches("[0-9.]*")) {
                    return change;
                }
                return null;
            }));
        } else if (field.getType() == boolean.class) {
            inputControl = new CheckBox();
        } else {
            inputControl = new TextField();
        }
        return inputControl;
    }

    private void showSuccessPopup(Window owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Object created successfully!");
        alert.initOwner(owner);
        alert.showAndWait();
    }

    private void showFailurePopup(Window owner, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Object Creation Failed");
        Text contentText = new Text(customErrorMessage(e));
        contentText.setFill(Color.INDIANRED);
        contentText.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        alert.getDialogPane().setContent(contentText);
        alert.getDialogPane().setPadding(new Insets(20));
        alert.initOwner(owner);
        alert.showAndWait();
    }

    private String customErrorMessage(Exception e) {
        String message;
        Map<Class<? extends Throwable>, String> errorMessages = Map.of(
                NoSuchMethodException.class, "Tried to fetch a method. How did this happen?",
                InvocationTargetException.class, "Mismatched/otherwise dodgy constructor. Noargs?",
                InstantiationException.class, "Couldn't create class object. Lots of reasons, but issue is specifically during instantiation.",
                IllegalAccessException.class, "Can't access field/class. Unnecessary final?"
        );
        if (e.getCause() != null) {
            message = errorMessages.getOrDefault(e.getCause().getClass(), "Unexpected exception.");
        } else {
            message = errorMessages.getOrDefault(e.getClass(), "Unexpected exception.");
        }
        return message + "\n\n" + e.getMessage();
    }

}
