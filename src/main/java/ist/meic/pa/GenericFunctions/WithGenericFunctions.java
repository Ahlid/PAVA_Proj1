package ist.meic.pa.GenericFunctions;

import ist.meic.pa.GenericFunctions.structure.TypeNode;
import javassist.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Object[] s = new Object[]{};


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

    public static HashMap<String, TypeNode> getTypeTree(Class clazz) {
        Map<String, Integer> counts = getConflicts(clazz);

        // Map types

        try {
            return generateTypeTree(clazz, counts);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }


    }

    private static HashMap<String, TypeNode> generateTypeTree(Class clazz, Map<String, Integer> counts) throws ClassNotFoundException {

        HashMap<String, TypeNode> typeTree = new HashMap<String, TypeNode>();

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {

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

                //the tree for the BeforeMethod
                if (m.getAnnotation(BeforeMethod.class) != null) {
                    if (typeTree.containsKey(entry.getKey() + "@BeforeMethod")) {
                        tree = typeTree.get(entry.getKey() + "@BeforeMethod");
                    } else {
                        tree = new TypeNode();
                        typeTree.put(entry.getKey() + "@BeforeMethod", tree);
                    }
                }

                //the tree for the AfterMethod
                if (m.getAnnotation(AfterMethod.class) != null) {
                    if (typeTree.containsKey(entry.getKey() + "@AfterMethod")) {
                        tree = typeTree.get(entry.getKey() + "@AfterMethod");
                    } else {
                        tree = new TypeNode();
                        typeTree.put(entry.getKey() + "@AfterMethod", tree);
                    }
                }


                TypeNode curNode = null;
                for (Type type : m.getGenericParameterTypes()) {

                    Class<? extends Object> c = null;
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

       // System.out.println(typeTree);
        return typeTree;
    }

    public static HashMap<String, Integer> getConflicts(Class clazz) {
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        for (Method m : clazz.getDeclaredMethods()) {
            Integer count = counts.get(m.getName());
            counts.put(m.getName(), count == null ? 0 : new Integer(count + 1));
        }
        return counts;
    }

    public static Method findBest(TypeNode root, Class<?>[] classes, Class<?>[] startedClasses) throws NoSuchMethodException, SecurityException {

        Method ret = null;
        Class[] currentClassesArgs = classes.clone();

/*
        for (Class c : currentClassesArgs) {
            System.out.print(c + "-----------");
        }
        System.out.println();

*/
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


            return findBest(root, currentClassesArgs, startedClasses);


        }

        return ret;
    }

}
