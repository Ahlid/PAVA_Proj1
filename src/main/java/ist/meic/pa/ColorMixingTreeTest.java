package ist.meic.pa;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ist.meic.pa.GenericFunctions.structure.TypeNode;

public class ColorMixingTreeTest {

	public static void main(String[] args) throws ClassNotFoundException {
		prepare(Color.class);
	}

	public static void prepare(Class clazz) throws ClassNotFoundException {
		// Find name conflicts
		Map<String, Integer> counts = getConflicts(clazz);

		// Map types
		Map<String, TypeNode> typeTree = generateTypeTree(clazz, counts);

		Color[] colors = { new Red(), new Yellow(), new Blue() };
		for (Color c1 : colors)
			for (Color c2 : colors) {
				TypeNode node = typeTree.get("mix").getRoot().getTypeNode(c1.getClass()).getTypeNode(c2.getClass());
				if (node != null)
					System.out.println(node.generateArgumentArray());
			}
	}

	/**
	 * Create a tree which maps the logical order of arguments for each method.
	 * These trees are categorized by method name.
	 * 
	 * @param clazz
	 * @param counts
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static Map<String, TypeNode> generateTypeTree(Class clazz, Map<String, Integer> counts)
			throws ClassNotFoundException {
		HashMap<String, TypeNode> typeTree = new HashMap<String, TypeNode>();
		for (Entry<String, Integer> entry : counts.entrySet()) {

			// only do conflicts
			if (entry.getValue() < 2)
				continue;

			List<Method> methods = Stream.of(clazz.getMethods()).filter(m -> m.getName().equals(entry.getKey()))
					.collect(Collectors.toList());

			// build the type tree
			for (Method m : methods) {
				TypeNode tree = typeTree.get(entry.getKey());

				// if the tree doesnt exist for this method just init it
				if (tree == null) {
					tree = new TypeNode();
					typeTree.put(entry.getKey(), tree);
				}

				TypeNode curNode = null;
				for (Type type : m.getGenericParameterTypes()) {
					Class<? extends Object> c = Class.forName(type.getTypeName());

					// first iteration always enters here
					if (curNode == null)
						curNode = tree.getRoot();

					// init root node or add a new one to existing
					if (!curNode.hasType(c))
						curNode.addNode(new TypeNode(c));
					curNode = curNode.getTypeNode(c);
				}

			}
		}
		return typeTree;
	}

	/**
	 * Return the occurrence count for each method name.
	 * 
	 * @param clazz
	 * @return
	 */
	private static Map<String, Integer> getConflicts(Class clazz) {
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		for (Method m : clazz.getDeclaredMethods()) {
			Integer count = counts.get(m.getName());
			counts.put(m.getName(), count == null ? 0 : new Integer(count + 1));
		}
		return counts;
	}

}
