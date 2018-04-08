package ist.meic.pa.GenericFunctions.structure;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeNode {

	private boolean root = false;
	private Type t;
	private TypeNode parentNode;
	private Map<Type, TypeNode> children = new HashMap<Type, TypeNode>();

	public TypeNode() {
		this.root = true;
	}

	public TypeNode(Type t) {
		this.t = t;
	}

	public Type getType() {
		return t;
	}

	public TypeNode getParent() {
		return parentNode;
	}

	public boolean hasParent() {
		return parentNode != null;
	}

	public TypeNode getTypeNode(Type t) {
		return children.get(t);
	}

	public boolean hasType(Type t) {
		return children.containsKey(t);
	}

	public void addNode(TypeNode n) {
		children.put(n.getType(), n);
	}

	public boolean hasChildren() {
		return children.size() == 0;
	}

}
