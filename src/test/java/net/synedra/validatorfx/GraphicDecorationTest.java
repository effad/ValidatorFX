package net.synedra.validatorfx;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class GraphicDecorationTest extends TestBase {
	
	private VBox root;
	private Rectangle decorationNode;
	private Rectangle target;

	@Start
	private void setupScene(Stage stage) {
		root = new VBox();
		decorationNode = new Rectangle(10, 10);
		target = new Rectangle(100, 100);
		root.getChildren().add(target);
        stage.setScene(new Scene(root, 640, 400));
        stage.show();		
	}
	
	@Test
	void testDecoration() {
		GraphicDecoration decoration = new GraphicDecoration(decorationNode);
		fx(() -> {
			decoration.add(target);
		});
		assertEquals(-5, decorationNode.getBoundsInParent().getMinX());
		assertEquals(-5, decorationNode.getBoundsInParent().getMinY());
		fx(() -> {
			decoration.remove(target);
		});
		assertNull(decorationNode.getParent());
	}
	
	@Test
	void testDecorationTopLeft() {
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode);
			decoration.add(target);
		});
		assertEquals(-5, decorationNode.getBoundsInParent().getMinX());
		assertEquals(-5, decorationNode.getBoundsInParent().getMinY());
	}
	
	@Test
	void testDecorationBottomLeft() {
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode, Pos.BOTTOM_LEFT);
			decoration.add(target);
		});
		assertEquals(-5, decorationNode.getBoundsInParent().getMinX());
		assertEquals(95, decorationNode.getBoundsInParent().getMinY());
	}
	
	@Test
	void testDecorationTopRight() {
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode, Pos.TOP_RIGHT);
			decoration.add(target);
		});
		assertEquals(95, decorationNode.getBoundsInParent().getMinX());
		assertEquals(-5, decorationNode.getBoundsInParent().getMinY());
	}
	
	@Test
	void testDecorationBottomRight() {
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode, Pos.BOTTOM_RIGHT);
			decoration.add(target);
		});
		assertEquals(95, decorationNode.getBoundsInParent().getMinX());
		assertEquals(95, decorationNode.getBoundsInParent().getMinY());
	}
	
	@Test
	void testDecorationCenter() {
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode, Pos.CENTER);
			decoration.add(target);
		});
		assertEquals(45, decorationNode.getBoundsInParent().getMinX());
		assertEquals(45, decorationNode.getBoundsInParent().getMinY());
	}
	
	
	@Test
	void testDecorationOffset() {
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode, Pos.TOP_LEFT, 12, 17);
			decoration.add(target);
		});
		assertEquals(-5 + 12, decorationNode.getBoundsInParent().getMinX());
		assertEquals(-5 + 17, decorationNode.getBoundsInParent().getMinY());
	}
	
	@Test
	void testDecorationStackReuse() {
		// without decoration, we don't have a decoration pane
		assertEquals(0, countDecorationStackPanes(target));
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode);
			decoration.add(target);
		});
		// now we have one
		assertEquals(1, countDecorationStackPanes(target));

		// Add a second node with decoration
		Circle secondDecorationNode = new Circle(50, 50, 20);
		Rectangle secondTarget = new Rectangle(100, 100);
		fx(() -> {
			root.getChildren().add(secondTarget);
			GraphicDecoration decoration = new GraphicDecoration(secondDecorationNode);
			decoration.add(secondTarget);			
		});
		
		// We must still only have one decoration pane
		assertEquals(1, countDecorationStackPanes(target));
	}
	
	@Test
	void testNonSceneNode(FxRobot robot) {
		HBox hbox = new HBox();
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode);
			decoration.add(hbox);
		});
		assertEquals(0, countDecorationStackPanes(hbox));
		assertFalse(hasDecorationNode(robot));
		fx(() -> {
			root.getChildren().add(hbox);
		});
		assertEquals(1, countDecorationStackPanes(hbox));
		assertTrue(hasDecorationNode(robot));
	}
	
	@Test
	void testDelayedAddAfterRemove(FxRobot robot) {
		HBox hbox = new HBox();
		GraphicDecoration decoration = new GraphicDecoration(decorationNode);
		fx(() -> {
			decoration.add(hbox);
		});
		assertEquals(0, countDecorationStackPanes(hbox));
		assertFalse(hasDecorationNode(robot));
		fx(() -> {
			decoration.remove(hbox);
			root.getChildren().add(hbox);
		});
		assertFalse(hasDecorationNode(robot));
	}
	
	
	@Test
	void testToggleVisiblity() {
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode);
			decoration.add(target);
		});
		assertTrue(decorationNodeVisible());
		
		fx(() -> target.setVisible(false));
		WaitForAsyncUtils.waitForFxEvents();
		assertFalse(decorationNodeVisible());
	}
	
	@Test
	void testToggleTargetInScene() {
		HBox hbox = new HBox();
		fx(() -> {
			GraphicDecoration decoration = new GraphicDecoration(decorationNode);
			decoration.add(hbox);
		});
		WaitForAsyncUtils.waitForFxEvents();
		assertFalse(decorationNodeVisible());
		
		fx(() -> root.getChildren().add(hbox));		
		WaitForAsyncUtils.waitForFxEvents();
		assertTrue(decorationNodeVisible());
		
		fx(() -> root.getChildren().remove(hbox));
		WaitForAsyncUtils.waitForFxEvents();
		assertFalse(decorationNodeVisible());
	}
	
	private boolean decorationNodeVisible() {
		return decorationNode.getScene() != null;
	}
	
	private int countDecorationStackPanes(Node node) {
		int count = 0;
		while (node != null) {
			if (node instanceof GraphicDecorationStackPane) {
				count++;
			}
			node = node.getParent();
		}
		return count;
	}
	
	private boolean hasDecorationNode(FxRobot robot) {
		return ! robot.lookup(candidate -> candidate == decorationNode)
				.queryAll().isEmpty();
	}
	
}
