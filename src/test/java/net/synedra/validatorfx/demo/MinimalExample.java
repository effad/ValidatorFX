package net.synedra.validatorfx.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;

public class MinimalExample extends Application {

	private Validator validator = new Validator();

	@Override
	public void start(Stage primaryStage) throws Exception {

		TextField userTextField = new TextField();

		validator.createCheck()
			.dependsOn("username", userTextField.textProperty())
			.withMethod(c -> {
				String userName = c.get("username");
				if (!userName.toLowerCase().equals(userName)) {
					c.error("Please use only lowercase letters.");
				}
			})
			.decorates(userTextField)
			.immediate();
		;
		
		GridPane grid = createGrid();
		grid.add(userTextField, 1, 1);

		Scene scene = new Scene(grid);		
		primaryStage.setScene(scene);		
		primaryStage.show();		
	}

	private GridPane createGrid() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setPrefSize(400,  200);
		return grid;
	}

	public static void main(String[] args) {
		launch();
	}
}
