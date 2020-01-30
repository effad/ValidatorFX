package net.synedra.validatorfx.demo;

import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import net.synedra.validatorfx.DefaultDecoration;
import net.synedra.validatorfx.Validator;

/** AccordionDemo shows validation in an accordion 
 * @author r.lichtenberger@synedra.com
 */
public class AccordionDemo extends Application {

	private Accordion accordion;
	private Validator validator = new Validator();

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("ValidatorFX Demo");

		accordion = new Accordion();
		
		for (int i = 0; i < 10; i++) {
			TextField textfield = new TextField();
			TitledPane tp = new TitledPane("Pane " + i, textfield);

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
			accordion.getPanes().add(tp);
		}
		accordion.setExpandedPane(accordion.getPanes().get(0));
		
		Scene scene = new Scene(accordion);
		scene.getStylesheets().add(getClass().getResource("demo.css").toExternalForm());
		
		primaryStage.setScene(scene);		
		primaryStage.show();		
	}

	public static void main(String[] args) {
		if (Arrays.asList(args).contains("-css")) {
			DefaultDecoration.setFactory(DefaultDecoration::createStyleClassDecoration);
		}
		launch();
	}
}
