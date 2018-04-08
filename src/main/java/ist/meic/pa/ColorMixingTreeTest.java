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

	public static void main(String[] args) {
		prepare(Color.class);

		Color[] colors = { new Red(), new Yellow(), new Blue() };

		for (Color c1 : colors)
			for (Color c2 : colors) {
				String out = Color.mix(c1, c2);
				System.out.println(String.format("%s + %s = %s", c1.getClass().getSimpleName(),
						c2.getClass().getSimpleName(), out));
			}
	}

	public static void prepare(Class clazz) {
		Map<String, TypeNode> typeTree = new HashMap<String, TypeNode>();

		// Find name conflicts
		Map<String, Integer> counts = new HashMap<String, Integer>();
		for (Method m : clazz.getDeclaredMethods()) {
			Integer count = counts.get(m.getName());
			counts.put(m.getName(), count == null ? 0 : new Integer(count + 1));
		}

		// Map types
		for (Entry<String, Integer> entry : counts.entrySet()) {

			// only do conflicts
			if (entry.getValue() < 2)
				continue;

			List<Method> methods = Stream.of(clazz.getMethods()).filter(m -> m.getName().equals(entry.getKey()))
					.collect(Collectors.toList());

			// build the type tree
			for (Method m : methods) {
				TypeNode tree = typeTree.get(entry.getKey());
				if (tree == null)
					tree = new TypeNode();

				TypeNode curNode = null;
				for(Type type : m.getGenericParameterTypes()) {

					// first iteration always enters here
					if (curNode == null)
						curNode = tree.getTypeNode(type);

					// init root node or add a new one to existing
					TypeNode newNode = new TypeNode(type);
					if (curNode == null) {
						tree.addNode(newNode);
						curNode = newNode;
						continue;
					} else {
						if (!curNode.hasType(type))
							curNode.addNode(newNode);
					}
					curNode = curNode.getTypeNode(type);
				}

				// Register the tree
				typeTree.put(entry.getKey(), tree);
			}
		}

		Color[] colors = { new Red(), new Yellow(), new Blue() };
		for (Color c1 : colors)
			for (Color c2 : colors) {
				typeTree.get( c1.getClass());
			}
	}

}
