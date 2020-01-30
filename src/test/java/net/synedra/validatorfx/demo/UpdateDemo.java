package net.synedra.validatorfx.demo;

import java.util.Arrays;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
	private ScrollPane root;

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Update Demo");

		root = new ScrollPane();
		root.setMaxHeight(480);
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("demo.css").toExternalForm());
		
		rebuild();
		
		primaryStage.setScene(scene);		
		primaryStage.show();		
	}

	private void rebuild() {
		grid = createGrid();
		
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
		
		VBox vbox = new VBox();
		
		CheckBox toggleGridVisibility = new CheckBox("fields visible");
		toggleGridVisibility.setSelected(true);
		grid.visibleProperty().bind(toggleGridVisibility.selectedProperty());
		
		CheckBox toggleGridInVBox = new CheckBox("grid in scene");
		toggleGridInVBox.setSelected(true);
		toggleGridInVBox.selectedProperty().addListener((observable, oldValue, newValue) -> toggleGridInVBox(vbox, newValue));
		
		Button rebuild = new Button("rebuild GUI");
		rebuild.setOnAction(e -> rebuild());
		
		vbox.getChildren().addAll(new HBox(toggleGridVisibility, toggleGridInVBox, rebuild), grid);
		
		root.setContent(vbox);
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
	
	private void toggleGridInVBox(VBox vbox, boolean add) {
		if (add) {
			vbox.getChildren().add(grid);
		} else {
			vbox.getChildren().remove(grid);			
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
