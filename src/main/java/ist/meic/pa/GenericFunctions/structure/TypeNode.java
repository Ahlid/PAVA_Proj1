package ist.meic.pa.GenericFunctions.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeNode {

	private boolean root = false;
	private Class<? extends Object> clazz;
	private TypeNode parentNode;
	private Map<Class<? extends Object>, TypeNode> children = new HashMap<Class<? extends Object>, TypeNode>();

	public TypeNode() {
		this.root = true;
	}

	public TypeNode(Class<? extends Object> clazz) {
		this.clazz = clazz;
	}
	
	public TypeNode getRoot() {
		if (isRoot())
			return this;
		else
			return getParent().getRoot();
	}

	public boolean isRoot() {
		return root;
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

	/**
	 * 
	 * @return
	 */
	public List<Class<? extends Object>> generateArgumentArray() {
		List<Class<? extends Object>> classes = new ArrayList<Class<? extends Object>>();

		TypeNode tn = this;
		while (!tn.isRoot()) {
			classes.add(tn.getMappedType());
			tn = tn.getParent();
		}

		Collections.reverse(classes);
		return classes;
	}

	public Class<? extends Object> getMappedType() {
		return clazz;
	}

	public void setParent(TypeNode n) {
		parentNode = n;
	}

	public TypeNode getParent() {
		return parentNode;
	}

	public boolean hasParent() {
		return parentNode != null;
	}

	public TypeNode getTypeNode(Class<? extends Object> clazz) {
		return children.get(clazz);
	}

	public boolean hasType(Class<? extends Object> clazz) {
		return children.containsKey(clazz);
	}

	public void addNode(TypeNode n) {
		n.setParent(this);
		children.put(n.getMappedType(), n);
	}

	public boolean hasChildren() {
		return children.size() == 0;
	}

}
