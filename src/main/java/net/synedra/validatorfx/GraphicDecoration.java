package net.synedra.validatorfx;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;

/** GraphicDecoration provides decoration of nodes by overlaying them with another node.
 * @author r.lichtenberger@synedra.com
 */
public class GraphicDecoration implements Decoration {

	private Node target;
    private final Node decorationNode;
    private final Pos pos;
    private final double xOffset;
    private final double yOffset;
	
    private final ChangeListener<Boolean> targetNeedsLayoutListener;
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
		targetNeedsLayoutListener = (observable, oldValue, newValue) -> layoutGraphic();
	}
	
	@Override
	public void add(Node target) {
		this.target = target;
		ensureStack(target);
		stack.getChildren().add(decorationNode);
		layoutGraphic();		
	}


	@Override
	public void remove(Node target) {
        stack.getChildren().remove(decorationNode);
        stack.needsLayoutProperty().removeListener(targetNeedsLayoutListener);
        this.target = null;
	}
	
	private void ensureStack(Node target) {
		Node parent = target.getParent();
		while (parent != null && !(parent instanceof GraphicDecorationStackPane)) {
			parent = parent.getParent();			
		}
		if (parent == null) {
			stack = new GraphicDecorationStackPane();
			Parent oldRoot = target.getScene().getRoot();
			target.getScene().setRoot(stack);
			stack.getChildren().add(oldRoot);
		} else {
			stack = (GraphicDecorationStackPane) parent;
		}
        stack.needsLayoutProperty().removeListener(targetNeedsLayoutListener);
        stack.needsLayoutProperty().addListener(targetNeedsLayoutListener);
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
