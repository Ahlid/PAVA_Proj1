package ist.meic.pa.GenericFunctions.structure;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeNode {

    private boolean root = false;
    private Class<?> clazz;
    private TypeNode parentNode;
    private Map<Class<?>, TypeNode> children = new HashMap<>();
    private Method method;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public TypeNode() {
        this.root = true;
    }

    public TypeNode(Class<?> clazz) {
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
     * @return
     */
    public List<Class<?>> generateArgumentArray() {
        List<Class<?>> classes = new ArrayList<>();

        TypeNode tn = this;
        while (!tn.isRoot()) {
            classes.add(tn.getMappedType());
            tn = tn.getParent();
        }

        Collections.reverse(classes);
        return classes;
    }

    public Class<?> getMappedType() {
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

    public TypeNode getTypeNode(Class<?> clazz) {
        return children.get(clazz);
    }

    public boolean hasType(Class<?> clazz) {
        return children.containsKey(clazz);
    }

    public void addNode(TypeNode n) {
        n.setParent(this);
        children.put(n.getMappedType(), n);
    }

    public boolean hasChildren() {
        return children.size() == 0;
    }

    public String toStringChildren() {
		String childs = String.join("\n", children.values()
												.stream()
												.map(c -> c.preetyPrint())
												.collect(Collectors.toList()));
        return childs;
    }

    private int getDepth() {
    	int i= getParent() != null ? getParent().getDepth() : 0;
    	i += 1;
    	return i;
    }

	private String getDepthTabs() {
		int depth = getDepth();
		String ret = "";
		for (int i = 0; i < depth; i++)
			ret += "\t";
		return ret;
	}

	public String preetyPrint() {
		String tabs = getDepthTabs();
		
		String out = String.format("[%s]%s\n%s",
        		clazz,
        		this.method != null ? "*" : "",
        		toStringChildren());
		out = out.replaceAll("^(.*)", tabs + "$1");
		return out;
	}
	
    @Override
    public String toString() {
        return preetyPrint();
    }
}
