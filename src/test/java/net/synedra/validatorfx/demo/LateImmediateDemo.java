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

/** LateImmediateDemo demonstrates immediate-after-first-validation: 
 * First validation happens "on submit", afterwards validation is set to immediate. 
 * @author r.lichtenberger@synedra.com
 */
public class LateImmediateDemo extends Application {

	private Validator validator = new Validator();

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Late immediate demo");
		
		GridPane grid = createGrid();
		
		Text sceneTitle = new Text("Welcome");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		
		TextField userTextField = new TextField();
		
		PasswordField password = new PasswordField();
		PasswordField passwordConfirmation = new PasswordField();
				
		Button reset = new Button("Reset to on-submit");
		reset.setOnAction(this::resetClicked);
		Button signUp = new Button("Sign up");
		signUp.setOnAction(this::signUpClicked);
		HBox bottomBox = new HBox(10);
		bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
		bottomBox.getChildren().addAll(reset, signUp);
		
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
	
	private void resetClicked(ActionEvent e) {
		validator.explicit();
		validator.clear();
	}
	
	private void signUpClicked(ActionEvent e) {
		if (validator.validate()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Sign up complete");
			alert.setHeaderText(null);
			alert.setContentText("You successfully signed up");

			alert.showAndWait();		
		} else {
			validator.immediate();
		}
	}

	public static void main(String[] args) {
		if (Arrays.asList(args).contains("-css")) {
			DefaultDecoration.setFactory(DefaultDecoration::createStyleClassDecoration);
		}
		launch();
	}
}
