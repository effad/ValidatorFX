package net.synedra.validatorfx.demo;

import java.util.Arrays;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.synedra.validatorfx.DefaultDecoration;
import net.synedra.validatorfx.Validator;

/** UpdateDemo shows validation is updated correctly in various situations (scrolling, toggling visibility, etc).
 * @author r.lichtenberger@synedra.com
 */
public class UpdateDemo extends Application {

	private GridPane grid;
	private Validator validator = new Validator();

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("ValidatorFX Demo");

		grid = createGrid();
		ScrollPane scrollPane = new ScrollPane(grid);
		scrollPane.setMaxHeight(480);
		
		for (int i = 0; i < 50; i++) {
			grid.add(new Text("Field #" + i), 0, i);
			TextField textfield = new TextField();
			textfield.setUserData(Integer.valueOf(i));
			grid.add(textfield, 1, i);

			validator.createCheck()
				.withMethod(c -> {
					String text = c.get("text");
					if (text.length() > 3) {
						c.error("Text too long");
					}
				})
				.dependsOn("text", textfield.textProperty())
				.decorates(textfield)
				.immediate();
			;
			
			CheckBox toggleVisibility = new CheckBox("visible");
			toggleVisibility.setSelected(true);
			textfield.visibleProperty().bind(toggleVisibility.selectedProperty());
			grid.add(toggleVisibility, 2, i);
			
			CheckBox toggleParent = new CheckBox("in grid");
			toggleParent.setSelected(true);
			toggleParent.setUserData(textfield);
			toggleParent.selectedProperty().addListener((observable, oldValue, newValue) -> toggleParent(toggleParent));
			grid.add(toggleParent, 3, i);
		}
		
		Scene scene = new Scene(scrollPane);
		scene.getStylesheets().add(getClass().getResource("demo.css").toExternalForm());
		
		primaryStage.setScene(scene);		
		primaryStage.show();		
	}

	
	private void toggleParent(CheckBox toggleParent) {
		TextField textfield = (TextField) toggleParent.getUserData();
		Integer rowIndex = (Integer) textfield.getUserData(); 
		if (toggleParent.isSelected()) {
			grid.add(textfield, 1, rowIndex);
		} else {
			grid.getChildren().remove(textfield);
		}
	}


	private GridPane createGrid() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		return grid;
	}
	
	public static void main(String[] args) {
		if (Arrays.asList(args).contains("-css")) {
			DefaultDecoration.setFactory(DefaultDecoration::createStyleClassDecoration);
		}
		launch();
	}
}
