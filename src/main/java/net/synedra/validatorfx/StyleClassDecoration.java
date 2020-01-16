package net.synedra.validatorfx;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Node;

/** StyleClassDecoration provides decoration of nodes by setting / removing style classes.
 * @author r.lichtenberger@synedra.com
 */
public class StyleClassDecoration implements Decoration {

    private final Set<String> styleClasses;

    /** Create new StyleClassDecoration
     * @param styleClasses The style classes to apply to the target node, if check fails
     * @throws IllegalArgumentException if styleClasses is null or empty.
     */
    public StyleClassDecoration(String... styleClasses) {
        if (styleClasses == null || styleClasses.length == 0) {
            throw new IllegalArgumentException("At least one style class is required");
        }
        this.styleClasses = new HashSet<>(Arrays.asList(styleClasses));
    }

    @Override 
    public void add(Node targetNode) {
        List<String> styleClassList = targetNode.getStyleClass();
        Set<String> toAdd = new HashSet<>(styleClasses);
        toAdd.removeAll(styleClassList);	// don't add a style class that is already added.
        styleClassList.addAll(toAdd);
    }
    
    @Override 
    public void remove(Node targetNode) {
        targetNode.getStyleClass().removeAll(styleClasses);
    }
}
