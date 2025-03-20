package net.synedra.validatorfx.demo;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.synedra.validatorfx.*;
import net.synedra.validatorfx.Check.Context;

import java.util.Arrays;
import java.util.stream.Collectors;

/** AddRemoveDemo demonstrates that checks can be added / removed.
 *  https://github.com/effad/ValidatorFX/issues/43
 * @author r.lichtenberger@synedra.com
 */
public class AddRemoveDemo extends Application {

	private Validator validator = new Validator();
	private StringBinding problemsText;
    private Check check;
	private TextField userTextField;

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("ValidatorFX Demo");

        GridPane grid = createGrid();

		Button add = new Button("Add check");
		add.setOnAction(this::addCheck);
		grid.add(add, 0, 0);

		Button remove = new Button("Remove check");
		remove.setOnAction(this::removeCheck);
		grid.add(remove, 1, 0);
		
		userTextField = new TextField();
		grid.add(new Label("User Name:"), 0, 1);
		grid.add(userTextField, 1, 1);

		Scene scene = new Scene(grid);
		scene.getStylesheets().add(getClass().getResource("demo.css").toExternalForm());
		
		primaryStage.setScene(scene);		
		primaryStage.show();		
	}

	private void addCheck(ActionEvent actionEvent) {
		if (check == null) {
			check = validator.createCheck()
					.withMethod(this::required)
					.dependsOn("text", userTextField.textProperty())
					.decorates(userTextField)
					.immediate()
			;
		}
	}

	private void removeCheck(ActionEvent actionEvent) {
		if (check != null) {
			validator.remove(check);
			check = null;
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

	private void required(Context context) {
		String text = context.get("text");
		if (text == null || text.isEmpty()) {
			context.error("This field is required.");
		}
	}
}
