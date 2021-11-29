package net.synedra.validatorfx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/** TooltipWrapper will disable a node in case validation fails. It will also display a tooltip for the user to explain why the button is not enabled.
 * It works around JDK-8090379 (tooltips don't show on disabled controls)
 * @author r.lichtenberger@synedra.com
 */
public class TooltipWrapper<T extends Node> extends HBox {
	private T node;
	private Tooltip disabledTooltip;
	private ObservableValue<Boolean> disabledProperty;
    private BooleanProperty visibleProperty = new SimpleBooleanProperty(true);
	
	public TooltipWrapper(T node, ObservableValue<Boolean> disabledProperty, ObservableValue<String> tooltipText) {
		this.node = node;
		this.disabledProperty = disabledProperty;
		node.disableProperty().bind(disabledProperty);
		disabledProperty.addListener((observable, oldValue, newValue) -> updateTooltip());

		setId(node.getId() + "-wrapper");
		setAlignment(Pos.CENTER);
		HBox.setHgrow(node, Priority.ALWAYS);
		visibleProperty().bind(visibleProperty);
		getChildren().add(node);
		
		disabledTooltip = new Tooltip();
		disabledTooltip.setId(node.getId() + "-tooltip");
		disabledTooltip.getStyleClass().add("TooltipWrapper");
		disabledTooltip.textProperty().bind(tooltipText);
	}
	
	public T getWrappedNode() {
		return node;
	}
	
	private void updateTooltip() {
		if (Boolean.TRUE.equals(disabledProperty.getValue())) {
			Tooltip.install(this, disabledTooltip);
		} else {
			Tooltip.uninstall(this, disabledTooltip);
		}
	}
}
