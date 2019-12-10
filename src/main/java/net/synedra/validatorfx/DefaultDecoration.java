package net.synedra.validatorfx;

import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** DefaultDecoration provides default graphical decorations.
 * @author r.lichtenberger@synedra.com
 */
public class DefaultDecoration {
	
    private static final Image ERROR_IMAGE = new Image(GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/decoration-error.png").toExternalForm()); 
    private static final Image WARNING_IMAGE = new Image(GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/decoration-warning.png").toExternalForm()); 

    private static final String POPUP_SHADOW_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);"; 
    private static final String TOOLTIP_COMMON_EFFECTS = "-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;"; 
    
    private static final String ERROR_TOOLTIP_EFFECT = POPUP_SHADOW_EFFECT + TOOLTIP_COMMON_EFFECTS
            + "-fx-background-color: FBEFEF; -fx-text-fill: cc0033; -fx-border-color:cc0033;"; 

    private static final String WARNING_TOOLTIP_EFFECT = POPUP_SHADOW_EFFECT + TOOLTIP_COMMON_EFFECTS
            + "-fx-background-color: FFFFCC; -fx-text-fill: CC9900; -fx-border-color: CC9900;";

    public static Decoration createGraphicDecoration(ValidationMessage message) {
    	return new GraphicDecoration(createDecorationNode(message),Pos.TOP_LEFT);
    }
	
	private static Node createDecorationNode(ValidationMessage message) {
        Node graphic = Severity.ERROR == message.getSeverity() ? createErrorNode() : createWarningNode();
        graphic.getStyleClass().add("shadow_effect");
        Label label = new Label();
        label.setGraphic(graphic);
        label.setTooltip(createTooltip(message));
        label.setAlignment(Pos.CENTER);
        return label;
	}
	
    private static Tooltip createTooltip(ValidationMessage message) {
        Tooltip tooltip = new Tooltip(message.getText());
        tooltip.setOpacity(.9);
        tooltip.setAutoFix(true);
        tooltip.setStyle( Severity.ERROR == message.getSeverity()? ERROR_TOOLTIP_EFFECT: WARNING_TOOLTIP_EFFECT);
        return tooltip;
    }

    private static Node createErrorNode() {
        return new ImageView(ERROR_IMAGE);
    }

    private static Node createWarningNode() {
        return new ImageView(WARNING_IMAGE);
    }
    
}
