package net.synedra.validatorfx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

@ExtendWith(ApplicationExtension.class)
/** TooltipWrapperTest tests TooltipWrapper.
 * @author r.lichtenberger@synedra.com
 */
class TooltipWrapperTest {

	@Test
	void testWrappedNode() {
		Button button = new Button("test");
		TooltipWrapper<Button> wrapper = new TooltipWrapper<>(button, new SimpleBooleanProperty(false), new SimpleStringProperty("foobar"));
		assertEquals(button, wrapper.getWrappedNode());
	}
	
	@Test
	void testDisabling() {
		SimpleBooleanProperty disable = new SimpleBooleanProperty(false);
		Button button = new Button("test");
		TooltipWrapper<Button> wrapper = new TooltipWrapper<>(button, disable, new SimpleStringProperty("foobar"));		
		assertEquals(false, button.isDisabled());
		assertEquals(false, wrapper.isDisabled());
		disable.set(true);
		assertEquals(true, button.isDisabled());
		assertEquals(false, wrapper.isDisabled()); // wrapper always stays enabled to allow the tooltip to be shown
	}
	
	// this test uses implementation details of JavaFX and may fail if javafx.scene.control.Tooltip is changed
	@Test
	void testTooltip() {
		SimpleBooleanProperty disable = new SimpleBooleanProperty(false);
		Button button = new Button("test");
		TooltipWrapper<Button> wrapper = new TooltipWrapper<>(button, disable, new SimpleStringProperty("foobar"));		
		assertEquals(false, wrapper.getProperties().containsKey("javafx.scene.control.Tooltip"));
		disable.set(true);
		assertEquals(true, wrapper.getProperties().containsKey("javafx.scene.control.Tooltip"));
		disable.set(false);
		assertEquals(false, wrapper.getProperties().containsKey("javafx.scene.control.Tooltip"));
	}
}
