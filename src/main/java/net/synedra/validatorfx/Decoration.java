package net.synedra.validatorfx;

import javafx.scene.Node;

/** Decoration is the common interface of the various decoration strategies.
 * @author r.lichtenberger@synedra.com
 */
public interface Decoration {

	/** Add a decoration to the given target node
	 * @param target The node to decorate
	 */
	void add(Node target);

	/** Remove a decoration from the given target node
	 * @param target The node to remove decoration from
	 */
	void remove(Node target);
}
