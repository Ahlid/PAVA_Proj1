package ist.meic.pa.GenericFunctions;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ist.meic.pa.GenericFunctions.structure.TypeNode;
import javassist.ClassPool;
import javassist.Loader;
import javassist.Translator;

public class WithGenericFunctions {

    /**
     * Accepts, as arguments, the name of another Java program (i.e., a Java class
     * that also contains a static method main) and the arguments that should be
     * provided to that program. <br>
     * The class should (1) operate the necessary transformations to the loaded Java
     * classes so that when the classes are executed, the semantics of method calls
     * to methods defined in classes or interfaces annotated as
     * {@link GenericFunction} follow CLOS semantics, and (2) should transfer the
     * control to the main method of the program
     *
     * @param args
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable {

        if (args.length != 1) {
            System.out.println("Incorrect use of WithGenericFunctions <ProgramName>");
            System.exit(-1);
        }

        String programName = args[0];
        String[] restArgs = new String[args.length - 1];
        System.arraycopy(args, 1, restArgs, 0, restArgs.length);

        // load class with args
        init(programName, restArgs);
    }

    private static void init(String name, String[] args) throws Throwable {

        // configure classpoll and add translator to loader
        ClassPool pool = ClassPool.getDefault();
        Translator translator = new GFTranslator();
        Loader classLoader = new Loader();

        // Add the custom translator
        classLoader.addTranslator(pool, translator);
        classLoader.run(name, args);
    }

    public static HashMap<String, TypeNode> getTypeTree(Class<?> clazz) {
        Map<String, Integer> counts = getConflicts(clazz);

        // Map types
        try {
            return generateTypeTree(clazz, counts);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private static HashMap<String, TypeNode> generateTypeTree(Class<?> clazz, Map<String, Integer> counts) throws ClassNotFoundException {
        HashMap<String, TypeNode> typeTree = new HashMap<String, TypeNode>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {

            // only do conflicts
            if (entry.getValue() < 2)
                continue;

            List<Method> methods = Stream.of(clazz.getMethods())
            		.filter(m -> m.getName().equals(entry.getKey()))
                    .collect(Collectors.toList());
            
            // build the type tree
            List<Method> nonAnnotatedMethods = methods.stream()
            	.filter(m -> m.getAnnotation(BeforeMethod.class) == null)
            	.filter(m -> m.getAnnotation(AfterMethod.class) == null)
            	.collect(Collectors.toList());
            registerMethods(typeTree, nonAnnotatedMethods, entry.getKey());

            // build before methods
            List<Method> beforeMethods = methods.stream()
            	.filter(m -> m.getAnnotation(BeforeMethod.class) != null)
            	.collect(Collectors.toList());
            registerMethods(typeTree, beforeMethods, entry.getKey() + "@BeforeMethod");

            // build after methods
            List<Method> afterMethods = methods.stream()
        		.filter(m -> m.getAnnotation(AfterMethod.class) != null)
        		.collect(Collectors.toList());
        	registerMethods(typeTree, afterMethods, entry.getKey() + "@AfterMethod");
        }
        return typeTree;
    }

    /**
     * Add this group of methods to the typetree with the correct path according to their arguments.
     * @param typeTree
     * @param methods
     * @param key
     * @throws ClassNotFoundException
     */
    private static void registerMethods(HashMap<String, TypeNode> typeTree, List<Method> methods, String key) throws ClassNotFoundException {
    	for (Method m : methods) {

            TypeNode tree = typeTree.get(key);

            // if the tree doesnt exist for this method just init it
            if (tree == null) {
                tree = new TypeNode();
                typeTree.put(key, tree);
            }

            TypeNode curNode = null;
            for (Type type : m.getGenericParameterTypes()) {

                Class<?> c = null;
                try {
                    c = Class.forName(type.getTypeName());
                } catch (ClassNotFoundException e) {

                    String newClassName = "[L" + type.getTypeName().substring(0, type.getTypeName().length() - 2).replace("/", ".") + ";";
                    c = Class.forName(newClassName);
                }
                // first iteration always enters here
                if (curNode == null)
                    curNode = tree.getRoot();

                // init root node or add a new one to existing
                if (!curNode.hasType(c))
                    curNode.addNode(new TypeNode(c));
                curNode = curNode.getTypeNode(c);
            }
            curNode.setMethod(m);
        }
	}

	public static HashMap<String, Integer> getConflicts(Class<?> clazz) {
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        for (Method m : clazz.getDeclaredMethods()) {
            Integer count = counts.get(m.getName());
            counts.put(m.getName(), count == null ? 0 : new Integer(count + 1));
        }
        return counts;
    }

    public static Method findBest(TypeNode root, Class<?>[] classes, Class<?>[] startedClasses) throws NoSuchMethodException, SecurityException {
        Method ret = null;
        Class<?>[] currentClassesArgs = classes.clone();
        try {
        	ret = getMethodFrom(root, currentClassesArgs);
            //chegamos ao fim, vamos começar a chamar superclasses
        } catch (Exception e) {

            //vamos atribuir a superclass ao ultimo argumento
            currentClassesArgs[currentClassesArgs.length - 1] = currentClassesArgs[currentClassesArgs.length - 1].getSuperclass();
            boolean rebase = false;

            for (int i = 0; i < currentClassesArgs.length; i++) {
                Class<?> clazz = currentClassesArgs[i];

                if (rebase) {
                    currentClassesArgs[i] = startedClasses[i];
                } else if (clazz == null) {
                    //temos de chamar a superclasse do argumento anterior
                    //se não existe argumento anterior não existe metodo
                    if (i == 0) {
                        return null;
                    } else {
                        //atribuir a superclasse ao argumento anterior a este
                        currentClassesArgs[i - 1] = currentClassesArgs[i - 1].getSuperclass();

                        //todos os seguintes argumentos devem passar para a classe inicial
                        currentClassesArgs[i] = startedClasses[i];
                        rebase = true;
                    }
                }
            }
            return findBest(root, currentClassesArgs, startedClasses);
        }
        return ret;
    }

    /**
	 * Get the method associated to the path followed by the classes array given on
	 * the given tree.
	 * 
	 * @param tree
	 * @param classes
	 * @return
	 * @throws Exception
	 */
    private static Method getMethodFrom(TypeNode tree, Class<?>[] classes) throws Exception {
    	for (Class<?> c : classes)
    		tree = tree.getTypeNode(c);

        Method ret = tree.getMethod();
        if (ret == null) {
            throw new Exception();
        }
        return ret;
    }

    public static Method[] findAfterHooks(TypeNode typeTree, Class<?>[] classes) throws ClassNotFoundException {
    	List<Method> hooks = findAllMethods(typeTree, classes);
    	Collections.reverse(hooks);
    	
    	Method[] ret = new Method[hooks.size()];
    	hooks.toArray(ret);
    	return ret;
    }

    public static Method[] findBeforeHooks(TypeNode typeTree, Class<?>[] classes) throws ClassNotFoundException {
    	List<Method> hooks = findAllMethods(typeTree, classes);
    	
		Method[] ret = new Method[hooks.size()];
		hooks.toArray(ret);
    	return ret;
    }

    /**
     * Return a list of methods ordered by ascending order of distance. 
     * @param typeTree
     * @param classes
     * @return
     * @throws ClassNotFoundException
     */
    private static List<Method> findAllMethods(TypeNode typeTree, Class<?>[] classes) {
		List<Method> ret = new ArrayList<>();
		Map<Integer, List<Method>> methods = _findAllMethods(typeTree, classes, 0);
		methods.keySet().stream()
				.sorted()
				.forEach(
					i -> ret.addAll(methods.get(i).stream().distinct().collect(Collectors.toList())));
		return ret;
	}

    /**
     * Return a collection of methods grouped by their distance to the root class
     * @param typeTree
     * @param classes
     * @return
     */
	private static Map<Integer, List<Method>> _findAllMethods(TypeNode typeTree, Class<?>[] classes, Integer distance) {
		Map<Integer, List<Method>> ret = new HashMap<>();
		try {
			Method method = getMethodFrom(typeTree, classes);
			List<Method> list = getOrInit(ret, distance);
			list.add(method);
		} catch (Exception e) {
			// I guess theres no methods here
		}
		for (int i = 0; i < classes.length; i++) {
			Class<?>[] copy = classes.clone();
			copy[i] = copy[i].getSuperclass();
			if (copy[i]== null) continue;
			_findAllMethods(typeTree, copy, distance + 1).entrySet().stream()
			.forEach(entry -> {
				List<Method> list = getOrInit(ret, entry.getKey()); 
				list.addAll(entry.getValue());
			});
		}
		return ret;
	}

	/**
	 * Get the list. Initiate it if its empty
	 * @param ret
	 * @param distance
	 * @return
	 */
	private static List<Method> getOrInit(Map<Integer, List<Method>> ret, Integer distance) {
		List<Method> list = ret.get(distance);
		if (list == null) {
			list = new ArrayList<Method>();
			ret.put(distance, list);
		}
		return list;
	}

}
