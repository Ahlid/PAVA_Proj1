package ist.meic.pa.GenericFunctions;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    /**
     * Method that inits the class loader and the changes in the classes
     *
     * @param name - the class to take control after the transformations
     * @param args - the arguments to send to the class that will take control
     * @throws Throwable - an error if something goes wrong in the transformations
     */
    private static void init(String name, String[] args) throws Throwable {

        // configure classpoll and add translator to loader
        ClassPool pool = ClassPool.getDefault();
        Translator translator = new GFTranslator();
        Loader classLoader = new Loader();

        // Add the custom translator
        classLoader.addTranslator(pool, translator);
        classLoader.run(name, args);
    }

    /**
     * Gets the type tree based on the class methods
     *
     * @param clazz - the class to get the type tree
     * @return the tree for the class
     */
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

    /**
     * Generates the tree
     *
     * @param clazz  - the class
     * @param counts - map with repeated methods
     * @return the type tree
     * @throws ClassNotFoundException
     */
    private static HashMap<String, TypeNode> generateTypeTree(Class<?> clazz, Map<String, Integer> counts) throws ClassNotFoundException {
        HashMap<String, TypeNode> typeTree = new HashMap<String, TypeNode>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {

            // only do conflicts
            if (entry.getValue() < 2)
                continue;

            List<Method> methods = Stream.of(clazz.getDeclaredMethods())
                    .filter(m -> m.getName().equals(entry.getKey()))
                    .collect(Collectors.toList());


            // build the type tree
            List<Method> nonAnnotatedMethods = methods.stream()
                    .filter(m -> m.getAnnotation(BeforeMethod.class) == null)
                    .filter(m -> m.getAnnotation(AfterMethod.class) == null)
                    .collect(Collectors.toList());

            registerMethods(typeTree, nonAnnotatedMethods, entry.getKey());
            typeTree.get(entry.getKey()).preetyPrint();

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
     *
     * @param typeTree
     * @param methods
     * @param key
     * @throws ClassNotFoundException
     */
    private static void registerMethods(HashMap<String, TypeNode> typeTree, List<Method> methods, String key) throws ClassNotFoundException {
        for (Method m : methods) {

            // System.out.println(m);

            TypeNode tree = typeTree.get(key);

            // if the tree doesnt exist for this method just init it
            if (tree == null) {
                tree = new TypeNode();
                typeTree.put(key, tree);
            }

            TypeNode curNode = null;
            for (Type type : m.getGenericParameterTypes()) {

                Class<?> c = determineTypeClass(type);

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

    /**
     * Find the class related to the given type. Diferentiate between collections and arrays here.
     *
     * @param type
     * @return
     * @throws ClassNotFoundException
     */
    private static Class<?> determineTypeClass(Type type) throws ClassNotFoundException {
        Class<?> c = null;
        try {
            c = Class.forName(type.getTypeName());
        } catch (ClassNotFoundException e) {
            // When here, its not a simple type
            // Declare patterns here

            // matches tokens like java.util.List<java.lang.Object>
            Matcher parameterizedTypeMatcher = Pattern.compile("(.*)<(.*)>").matcher(type.getTypeName());

            // matches tokens like java.lang.Object[]
            Matcher arrayMatcher = Pattern.compile("(.*)\\[\\]").matcher(type.getTypeName());

            if (parameterizedTypeMatcher.matches()) {
                String className = parameterizedTypeMatcher.group(1);
                String parameterType = parameterizedTypeMatcher.group(2);
                c = Class.forName(className);
            } else if (arrayMatcher.matches()) {
                String className = String.format("[L%s;", arrayMatcher.group(1));
                c = Class.forName(className);
            }
        }
        return c;
    }

    /**
     * Gets the conflicts (methods with same name)
     *
     * @param clazz - the class to get the conflicts
     * @return a map with name-number of conflicts
     */
    public static HashMap<String, Integer> getConflicts(Class<?> clazz) {
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        for (Method m : clazz.getDeclaredMethods()) {
            Integer count = counts.get(m.getName());
            counts.put(m.getName(), count == null ? 0 : new Integer(count + 1));
        }
        return counts;
    }

    // i is used for recursion, for the initial call this should be 0
    private static List<List<Class>> combine(List<List<Class>> input, int i) {

        // stop condition
        if (i == input.size()) {
            // return a list with an empty list
            List<List<Class>> result = new ArrayList();
            result.add(new ArrayList());
            return result;
        }

        List<List<Class>> result = new ArrayList();
        List<List<Class>> recursive = combine(input, i + 1); // recursive call


        // for each element of the first list of input
        for (int j = 0; j < input.get(i).size(); j++) {
            // add the element to all combinations obtained for the rest of the lists
            for (int k = 0; k < recursive.size(); k++) {
                // copy a combination from recursive
                List<Class> newList = new ArrayList();
                for (Class integer : recursive.get(k)) {
                    newList.add(integer);
                }
                // add element of the first list
                newList.add(input.get(i).get(j));
                // add new combination to result
                result.add(newList);
            }
        }
        return result;
    }

    /**
     * Method that finds the best method in a root for the arguments
     *
     * @param root    - the search root
     * @param classes - the arguments types
     * @return the best method
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public static Method findBest2(TypeNode root, Class<?>[] classes) throws NoSuchMethodException, SecurityException {


        Method method = null;

        List<List<Class>> argumentsCombinations = getAllClassCombinations(classes);
        for (List<Class> arguments : argumentsCombinations) {
            try {
                method = getMethodFrom(root, getArrayClassFromList(arguments));
                break;
            } catch (Exception e) {
                continue;
            }
        }

        return method;


    }

    /**
     * Transforms a list into an array
     *
     * @param classesList - the list to transform
     * @return the array from the list
     */
    private static Class[] getArrayClassFromList(List<Class> classesList) {

        Class[] classes = new Class[classesList.size()];

        for (int i = 0; i < classesList.size(); i++) {
            classes[i] = classesList.get(i);
        }

        return classes;

    }

    /**
     * Gets all the possible combinations
     *
     * @param classes - the lists classes to combine
     * @return the combinations result
     */
    private static List<List<Class>> getAllClassCombinations(Class[] classes) {

        //the list where we will store our class type and all supertypes per array
        List<List<Class>> classesSet = new ArrayList<>();

        //loop all classes
        for (Class currentClass : classes) {

            //the list for the class and the superclass
            List<Class> superList = new ArrayList<>();

            //add the class
            superList.add(currentClass);
            //add interfaces
            superList.addAll(new ArrayList(Arrays.asList(currentClass.getInterfaces())));

            //add the superclasses
            while (currentClass.getSuperclass() != null) {
                superList.add(currentClass.getSuperclass());
                currentClass = currentClass.getSuperclass();
                //add interfaces
                superList.addAll(new ArrayList(Arrays.asList(currentClass.getInterfaces())));
            }

            //add it to the list
            classesSet.add(superList);

        }


        //combine all classes in order that we start from the right arguments superclass call to the left argument
        List<List<Class>> result = combine(classesSet, 0).stream().map(i -> {
            Collections.reverse(i);
            return i;
        }).collect(Collectors.toList());

        return result;

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

        for (Class<?> c : classes) {
            tree = tree.getTypeNode(c);
        }

        Method ret = tree.getMethod();

        if (ret == null) {
            throw new Exception();
        }
        return ret;
    }

    /**
     * Method that returns a list of after methods from a root that match with de arguments types
     *
     * @param typeTree - the search tree
     * @param classes  - the arguments
     * @return the list of matched methods
     * @throws ClassNotFoundException
     */
    public static Method[] findAfterHooks(TypeNode typeTree, Class<?>[] classes) throws ClassNotFoundException {
        List<Method> hooks = findAllMethods(typeTree, classes);
        Collections.reverse(hooks);

        Method[] ret = new Method[hooks.size()];
        hooks.toArray(ret);
        return ret;
    }


    /**
     * Method that returns a list of before methods from a root that match with de arguments types
     *
     * @param typeTree - the search tree
     * @param classes  - the arguments
     * @return the list of matched methods
     * @throws ClassNotFoundException
     */
    public static Method[] findBeforeHooks(TypeNode typeTree, Class<?>[] classes) throws ClassNotFoundException {
        List<Method> hooks = findAllMethods(typeTree, classes);

        Method[] ret = new Method[hooks.size()];
        hooks.toArray(ret);
        return ret;
    }

    /**
     * Return a list of methods ordered by ascending order of distance.
     *
     * @param typeTree
     * @param classes
     * @return
     * @throws ClassNotFoundException
     */
    private static List<Method> findAllMethods(TypeNode typeTree, Class<?>[] classes) {

        List<Method> methods = new ArrayList<>();
        List<List<Class>> argumentsCombinations = getAllClassCombinations(classes);

        for (List<Class> arguments : argumentsCombinations) {

            // System.out.println(arguments);

            try {
                methods.add(getMethodFrom(typeTree, getArrayClassFromList(arguments)));
            } catch (Exception e) {
                continue;
            }

        }

        return methods;

      /*  Set<Method> ret = new LinkedHashSet<>();
        Map<Integer, List<Method>> methods = _findAllMethods(typeTree, classes, 0);
        methods.keySet()
                .stream()
                .sorted()
                .forEach(
                        i -> ret.addAll(methods.get(i)
                                .stream()
                                .distinct()
                                .collect(Collectors.toList())));
        return ret.stream().collect(Collectors.toList());
        */
    }


}
