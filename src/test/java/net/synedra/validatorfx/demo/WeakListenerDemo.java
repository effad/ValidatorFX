package net.synedra.validatorfx.demo;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableIntegerValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/** WeakListenerDemo shows why weak listeners are required in ValidatorFX.
 * @author r.lichtenberger@synedra.com
 */
public class WeakListenerDemo extends Application {

	private WeakListenerDemoForm form;
	private GridPane grid;
	private RadioButton odd;
	private RadioButton even;
	private ToggleGroup togglegroup; // this contains the "global" property our check will depend on ...
	private ObservableIntegerValue allowedModulus;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		grid = createGrid();

		Button showFormButton = new Button("Show form");
		showFormButton.setOnAction(this::showNewForm);
		grid.add(showFormButton, 0, 0);
		
		togglegroup = new ToggleGroup();
		
		odd = new RadioButton("Odd");
		odd.setToggleGroup(togglegroup);
		grid.add(odd, 1, 0);
		
		even = new RadioButton("Even");
		even.setToggleGroup(togglegroup);
		grid.add(even, 2, 0);
		
		togglegroup.selectToggle(odd);
		
		allowedModulus = Bindings.createIntegerBinding(() -> {
			return togglegroup.getSelectedToggle() == odd ? 1 : 0;
		}, togglegroup.selectedToggleProperty());

		Scene scene = new Scene(grid);		
		primaryStage.setScene(scene);		
		primaryStage.show();		
	}
	
	private void showNewForm(ActionEvent e) {
		if (form != null) {
			grid.getChildren().remove(form.getPresentation());
		}
		form = new WeakListenerDemoForm(allowedModulus);		
		grid.add(form.getPresentation(), 0, 1, 2, 1);
	}
	
	private GridPane createGrid() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		grid.setMinSize(600, 400);
		return grid;
	}


	public static void main(String[] args) {
		launch();
	}
}
