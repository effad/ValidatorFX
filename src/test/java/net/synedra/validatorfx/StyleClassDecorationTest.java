package net.synedra.validatorfx;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.scene.shape.Rectangle;

@ExtendWith(ApplicationExtension.class)
public class StyleClassDecorationTest {

	@Test
	public void testDecoration() {
		Rectangle targetNode = new Rectangle(1, 2, 3, 4);
		StyleClassDecoration decoration = new StyleClassDecoration("a", "b", "c");
		decoration.add(targetNode);
		assertTrue(targetNode.getStyleClass().contains("a"));
		assertTrue(targetNode.getStyleClass().contains("b"));
		assertTrue(targetNode.getStyleClass().contains("c"));
		targetNode.getStyleClass().add("otherStyle");
		decoration.remove(targetNode);
		assertFalse(targetNode.getStyleClass().contains("a"));
		assertFalse(targetNode.getStyleClass().contains("b"));
		assertFalse(targetNode.getStyleClass().contains("c"));
		assertTrue(targetNode.getStyleClass().contains("otherStyle"));
	}
	
	@Test
	public void testInvalidConstructorCall() {
		assertThrows(IllegalArgumentException.class, () -> new StyleClassDecoration((String []) null));
		assertThrows(IllegalArgumentException.class, () -> new StyleClassDecoration());
	}
}
