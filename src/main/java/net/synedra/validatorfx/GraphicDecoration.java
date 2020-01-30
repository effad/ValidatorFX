package net.synedra.validatorfx;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Transform;

/** GraphicDecoration provides decoration of nodes by overlaying them with another node.
 * @author r.lichtenberger@synedra.com
 */
public class GraphicDecoration implements Decoration {

	private Node target;
    private final Node decorationNode;
    private final Pos pos;
    private final double xOffset;
    private final double yOffset;
	
    private final ChangeListener<Boolean> layoutListener;
    private final ChangeListener<Transform> transformListener;
    
    private ChangeListener<Scene> sceneChangedListener;
    private GraphicDecorationStackPane stack;
    
	/** Create GraphicDecoration that will be overlayed in the top-left corner
	 * @param decorationNode The node to overlay over the decorated node 
	 */
	public GraphicDecoration(Node decorationNode) {
		this(decorationNode, Pos.TOP_LEFT);
	}
	
	/** Create GraphicDecoration
	 * @param decorationNode The node to overlay over the decorated node 
	 * @param position The location of the overlay 
	 */
	public GraphicDecoration(Node decorationNode, Pos position) {
		this(decorationNode, position, 0, 0);
	}
	
	/** Create GraphicDecoration
	 * @param decorationNode The node to overlay over the decorated node 
	 * @param position The location of the overlay 
	 * @param xOffset Horizontal offset of overlay (with respect to position)
	 * @param yOffset Vertical offset of overlay (with respect to position)
	 */
	public GraphicDecoration(Node decorationNode, Pos position, double xOffset, double yOffset) {
		this.decorationNode = decorationNode;
		this.decorationNode.setManaged(false);
		this.pos = position;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		layoutListener = (observable, oldValue, newValue) -> layoutGraphic();
		transformListener = (observable, oldValue, newValue) -> layoutGraphic();
	}
	
	@Override
	public void add(Node target) {
		this.target = target;
		decorationNode.visibleProperty().bind(target.visibleProperty().and(target.sceneProperty().isNotNull()));
		
		withStack(() -> {
			setListener();
			stack.getChildren().add(decorationNode);
			layoutGraphic();
		});
	}


	@Override
	public void remove(Node target) {
		decorationNode.visibleProperty().unbind();
		if (stack != null) {
			stack.getChildren().remove(decorationNode);
			stack.needsLayoutProperty().removeListener(layoutListener);
			this.target = null;
		}
		target.localToSceneTransformProperty().removeListener(transformListener);		
	}
	
	private void withStack(Runnable code) {
		Node parent = target.getParent();
		while (parent != null && !(parent instanceof GraphicDecorationStackPane)) {
			parent = parent.getParent();			
		}
		if (parent == null) {
			if (target.getScene() == null) {
				sceneChangedListener = (observable, oldValue, newValue) -> {
					if (oldValue == null && newValue != null) {
						target.sceneProperty().removeListener(sceneChangedListener);
						setupStack();
						code.run();
					}
				};
				target.sceneProperty().addListener(sceneChangedListener);
			} else {
				setupStack();
				code.run();
			}
		} else {
			stack = (GraphicDecorationStackPane) parent;
			code.run();
		}
	}
	
	private void setupStack() {
		stack = new GraphicDecorationStackPane();
		Parent oldRoot = target.getScene().getRoot();
		target.getScene().setRoot(stack);
		stack.getChildren().add(oldRoot);		
	}
	
	private void setListener() {
		stack.needsLayoutProperty().removeListener(layoutListener);
		stack.needsLayoutProperty().addListener(layoutListener);		
		target.localToSceneTransformProperty().removeListener(transformListener);
		target.localToSceneTransformProperty().addListener(transformListener);
	}
	
	
    private void layoutGraphic() {
        // Because we made decorationNode unmanaged, we are responsible for sizing it:
        decorationNode.autosize();
        // Now get decorationNode's layout Bounds and use for its position computations: 
        final Bounds decorationNodeLayoutBounds = decorationNode.getLayoutBounds();
        final double decorationNodeWidth = decorationNodeLayoutBounds.getWidth();
        final double decorationNodeHeight = decorationNodeLayoutBounds.getHeight();

        Bounds targetBounds = target.getLayoutBounds();
        double x = targetBounds.getMinX();
        double y = targetBounds.getMinY();

        double targetWidth = targetBounds.getWidth();
        if (targetWidth <= 0) {
            targetWidth = target.prefWidth(-1);
        }
        
        double targetHeight = targetBounds.getHeight();
        if (targetHeight <= 0) {
            targetHeight = target.prefHeight(-1);
        }

        switch (pos.getHpos()) {
        	case CENTER: 
        		x += targetWidth/2 - decorationNodeWidth / 2.0;
        		break;
        	case LEFT: 
        		x -= decorationNodeWidth / 2.0;
        		break;
        	case RIGHT:
        		x += targetWidth - decorationNodeWidth / 2.0;
        		break;
        }
        
        switch (pos.getVpos()) {
        	case CENTER: 
        		y += targetHeight/2 - decorationNodeHeight / 2.0;
        		break;
        	case TOP: 
        		y -= decorationNodeHeight / 2.0;
        		break;
        	case BOTTOM:
        		y += targetHeight - decorationNodeHeight / 2.0;
        		break;
        	case BASELINE: 
        		y += target.getBaselineOffset() - decorationNode.getBaselineOffset() - decorationNodeHeight / 2.0;
        		break;
        }
        
        Bounds sceneBounds = target.localToScene(targetBounds);
        Bounds stackBounds = stack.sceneToLocal(sceneBounds);
        decorationNode.setLayoutX(x + xOffset + stackBounds.getMinX());
        decorationNode.setLayoutY(y + yOffset +  stackBounds.getMinY());
    }
}
