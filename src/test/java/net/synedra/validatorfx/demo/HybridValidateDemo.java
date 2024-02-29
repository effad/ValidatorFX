package net.synedra.validatorfx.demo;

import java.util.Arrays;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.synedra.validatorfx.DefaultDecoration;
import net.synedra.validatorfx.Validator;

/** HybridValidateDemo demonstrates "hybrid" validation: Validation happens "on submit", but error marks are removed immediately if the user 
 * gives input.
 * @author r.lichtenberger@synedra.com
 */
public class HybridValidateDemo extends Application {

	private Validator validator = new Validator();

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Validate on submit demo");
		
		GridPane grid = createGrid();
		
		Text sceneTitle = new Text("Welcome");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		
		TextField userTextField = new TextField();
		
		PasswordField password = new PasswordField();
		PasswordField passwordConfirmation = new PasswordField();
				
		Button clear = new Button("Clear validation");
		clear.setOnAction(this::clearClicked);
		Button signUp = new Button("Sign up");
		signUp.setOnAction(this::signUpClicked);
		HBox bottomBox = new HBox(10);
		bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
		bottomBox.getChildren().addAll(clear, signUp);
		
		validator.createCheck()
			.withMethod(c -> {
				String userName = c.get("username");
				if (!userName.toLowerCase().equals(userName)) {
					c.error("Please use only lowercase letters.");
				}
				if (userName.isBlank()) {
					c.error("Username is required.");
				}
			})
			.dependsOn("username", userTextField.textProperty())
			.decorates(userTextField)
			.immediateClearing()
		;
		
		validator.createCheck()
			.withMethod(c -> {
				if (!c.get("password").equals(c.get("passwordConfirmation"))) {
					c.error("Passwords do not match");
				}
				if (((String) c.get("password")).isBlank()) {
					c.error("Password is required.");
				}
			})
			.dependsOn("password", password.textProperty())
			.dependsOn("passwordConfirmation", passwordConfirmation.textProperty())
			.decorates(password)
			.decorates(passwordConfirmation)
			.immediateClearing()
		;
		
		grid.add(sceneTitle, 0, 0, 2, 1);
		grid.add(new Label("User Name:"), 0, 1);
		grid.add(userTextField, 1, 1);
		grid.add(new Label("Password:"), 0, 2);
		grid.add(password, 1, 2);
		grid.add(new Label("Password confirmation:"), 0, 3);
		grid.add(passwordConfirmation, 1, 3);
		grid.add(bottomBox, 1, 6);
		
		Scene scene = new Scene(grid);
		scene.getStylesheets().add(getClass().getResource("demo.css").toExternalForm());
		
		primaryStage.setScene(scene);		
		primaryStage.show();		
	}

	private GridPane createGrid() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		return grid;
	}
	
	private void clearClicked(ActionEvent e) {
		validator.clear();
	}
	private void signUpClicked(ActionEvent e) {
		if (validator.validate()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Sign up complete");
			alert.setHeaderText(null);
			alert.setContentText("You successfully signed up");

			alert.showAndWait();		
		}
	}

	public static void main(String[] args) {
		if (Arrays.asList(args).contains("-css")) {
			DefaultDecoration.setFactory(DefaultDecoration::createStyleClassDecoration);
		}
		launch();
	}
}
