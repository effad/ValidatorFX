# ValidatorFX

## Introduction
ValidatorFX is a validation library for JavaFX. It is inspired (and in parts based on) [ControlsFX](https://github.com/controlsfx/controlsfx) but tries to overcome its shortcomings:
* Validations based on arbitrary observable values (not just the value of a control)
* Validations can decorate an arbitrary number of nodes (not just the one control)
* Validations can decorate any node (not just Control subtypes)

The central class of ValidatorFX is Validator which contains a number of Checks. As a rule a form will have one Validator object and multiple Checks.

## Example

Heres a minimal complete example of how ValidatorFX is used:

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
          .install();
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


Let's look at the central lines which show the fluent API of ValidatorFX:

    validator.createCheck()
A new check is created within the validator in this line.    
    
      .dependsOn("username", userTextField.textProperty())
A dependency named `username` is declared here. You can call dependsOn multiple times if you have more dependencies.

      .withMethod(c -> {
        String userName = c.get("username");
        if (!userName.toLowerCase().equals(userName)) {
          c.error("Please use only lowercase letters.");
        }
      })
This defines the check to be executed. Note how the dependency declared above can easily be accessed here (of course we could also have used `userTextField.getText()` instead of `c.get("username")` here. 

      .decorates(userTextField)
This line tells ValidatorFX to decorate the text field itself. You can call decorates multiple times if you want multiple nodes to be decorated.      
      
      .install();
The check is activated by this line.

Here's a screenshot of the example in action:

![Screenshot of MinimalExample](images/MinimalDemo.png)

To see more features and Details have a look at [ValidatorFXDemo.java](src/test/java/net/synedra/validatorfx/demo/ValidatorFXDemo.java)
