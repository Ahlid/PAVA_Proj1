package ist.meic.pa.GenericFunctions;

import java.lang.reflect.Method;

import ist.meic.pa.GenericFunctions.structure.TypeNode;

public class MethodCache {

	static boolean active = true;

	static private TypeNode methodTree = new TypeNode();

	public static Method getCachedMethod(Class<?>[] args) {
		if (active) {
			try {
				TypeNode search = methodTree;
				for (Class<?> c : args) {
					search = search.getTypeNode(c);
				}
				return search.getMethod();
			} catch (Exception e) {
				// No cached method
			}
		}
		return null;
	}

	public static void cacheMethod(Method m, Class<?>[] args) {
		if (active) {
			TypeNode newChild = null;
			for (Class<?> c : args) {
				if (newChild == null) {
					newChild = methodTree;
				}

				if (newChild.getTypeNode(c) != null) {
					newChild = newChild.getTypeNode(c);
				} else {
					TypeNode next = new TypeNode(c);
					newChild.addNode(next);
					newChild = next;
				}
			}
			newChild.setMethod(m);
		}
	}

}
