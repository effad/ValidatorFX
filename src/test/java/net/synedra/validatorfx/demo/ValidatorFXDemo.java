package net.synedra.validatorfx.demo;

import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.synedra.validatorfx.DefaultDecoration;
import net.synedra.validatorfx.Validator;

/** ValidatorFXDemo demonstrates the features of ValidatorFX.
 * @author r.lichtenberger@synedra.com
 */
public class ValidatorFXDemo extends Application {

	private Validator validator = new Validator();
	private StringBinding problemsText;
	private GridPane grid;

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("ValidatorFX Demo");

		grid = createGrid();
		
		Text sceneTitle = new Text("Welcome");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		
		TextField userTextField = new TextField();
		
		PasswordField password = new PasswordField();
		PasswordField passwordConfirmation = new PasswordField();
		
		TextArea problems = createProblemOutput();
		
		Button signUp = new Button("Sign up");
		signUp.disableProperty().bind(validator.containsErrorsProperty());
		HBox bottomBox = new HBox(10);
		bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
		bottomBox.getChildren().add(signUp);
		signUp.setOnAction(e -> signUp());
		
		validator.createCheck()
			.withMethod(c -> {
				String userName = c.get("username");
				if (!userName.toLowerCase().equals(userName)) {
					c.error("Please use only lowercase letters.");
				}
			})
			.dependsOn("username", userTextField.textProperty())
			.decorates(userTextField)
			.immediate();
		;
		
		validator.createCheck()
			.withMethod(c -> {
				if (!c.get("password").equals(c.get("passwordConfirmation"))) {
					c.error("Passwords do not match");
				}
			})
			.dependsOn("password", password.textProperty())
			.dependsOn("passwordConfirmation", passwordConfirmation.textProperty())
			.decorates(password)
			.decorates(passwordConfirmation)
			.immediate()
		;
		
		validator.createCheck()
			.withMethod(c -> {
				String pwd = c.get("password");
				if (pwd.length() < 8) {
					c.warn("Password should be at least 8 characters long.");
				}
			})
			.dependsOn("password", password.textProperty())
			.decorates(signUp)
			.immediate()
		;
		
		grid.add(sceneTitle, 0, 0, 2, 1);
		grid.add(new Label("User Name:"), 0, 1);
		grid.add(userTextField, 1, 1);
		grid.add(new Label("Password:"), 0, 2);
		grid.add(password, 1, 2);
		grid.add(new Label("Password confirmation:"), 0, 3);
		grid.add(passwordConfirmation, 1, 3);
		grid.add(problems, 0, 4, 2, 1);
		grid.add(bottomBox, 1, 6);
		
		Scene scene = new Scene(grid);
		scene.getStylesheets().add(getClass().getResource("demo.css").toExternalForm());
		
		primaryStage.setScene(scene);		
		primaryStage.show();		
	}

	private TextArea createProblemOutput() {
		TextArea problems = new TextArea();
		problems.setEditable(false);
		problems.setPrefHeight(60);
		problems.setBackground(Background.EMPTY);
		problems.setFocusTraversable(false);
		problemsText = Bindings.createStringBinding(this::getProblemText, validator.validationResultProperty());
		problems.textProperty().bind(problemsText);
		return problems;
	}
	
	private String getProblemText() {
		return validator.validationResultProperty().get().getMessages().stream()
			.map(msg -> msg.getSeverity().toString() + ": " + msg.getText())
			.collect(Collectors.joining("\n"));
	}

	private GridPane createGrid() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		return grid;
	}
	
	private void signUp() {
		grid.getChildren().setAll(new Text("You're now signed in."));
	}

	public static void main(String[] args) {
		if (Arrays.asList(args).contains("-css")) {
			DefaultDecoration.setFactory(DefaultDecoration::createStyleClassDecoration);
		}
		launch();
	}
}
