package ist.meic.pa;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
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

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        prepare(Color.class);
    }

    static Map<String, TypeNode> typeTree;

    public static void prepare(Class clazz) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        /*Find name conflicts
       // Map<String, Integer> counts = getConflicts(clazz);

        // Map types
        typeTree = generateTypeTree(clazz, counts);
        */


        Object[] objs = new Object[]{"Hello", 1, 2.0};
        for (Object o : objs) {
            Explain.it(o);
        }

/*
        Color[] colors = {new Red(), new Yellow(), new Blue()};

        for (Color c1 : colors)
            for (Color c2 : colors) {
                System.out.println(Color.mix(c1, c2));
            }*/

    }

    /**
     * Find the best suited method from the type tree
     *
     * @param methodName
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    private static Method findBest(String methodName, Class<? extends Object>... classes) throws NoSuchMethodException, SecurityException {
        TypeNode root = typeTree.get(methodName);

        if (root == null)
            return null;

        return _findBest(root, classes, classes);
    }

    private static Method _findBest(TypeNode root, Class<?>[] classes, Class<?>[] startedClasses) throws NoSuchMethodException, SecurityException {

        Method ret = null;
        Class[] currentClassesArgs = classes.clone();

        for (Class c : currentClassesArgs) {
            System.out.print(c + "-----------");
        }
        System.out.println();


        try {

            TypeNode tempRoot = root;
            for (Class c : currentClassesArgs)
                tempRoot = tempRoot.getTypeNode(c);

            ret = tempRoot.getMethod();
            if (ret == null) {
                throw new Exception();
            }

            //chegamos ao fim, vamos começar a chamar superclasses
        } catch (Exception e) {

            //vamos atribuir a superclass ao ultimo argumento
            currentClassesArgs[currentClassesArgs.length - 1] = currentClassesArgs[currentClassesArgs.length - 1].getSuperclass();

            boolean rebase = false;

            for (int i = 0; i < currentClassesArgs.length; i++) {

                Class clazz = currentClassesArgs[i];


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


            return _findBest(root, currentClassesArgs, startedClasses);


        }

        return ret;
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

                curNode.setMethod(m);

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
