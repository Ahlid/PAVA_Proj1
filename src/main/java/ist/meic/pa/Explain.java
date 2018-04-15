package ist.meic.pa;

import ist.meic.pa.GenericFunctions.AfterMethod;
import ist.meic.pa.GenericFunctions.BeforeMethod;
import ist.meic.pa.GenericFunctions.GenericFunction;
import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import ist.meic.pa.GenericFunctions.structure.TypeNode;

import java.lang.reflect.Method;
import java.util.Map;

@GenericFunction
interface Explain {

    static Map<String, TypeNode> typeTree = WithGenericFunctions.getTypeTree(Explain.class);

    public static void it(Object... args) {

        TypeNode root = typeTree.get("_it");
        TypeNode beforeRoot = typeTree.get("_it@BeforeMethod");
        TypeNode afterRoot = typeTree.get("_it@AfterMethod");

        Class[] classes = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
        }


        try {

            Method beforeMethod = WithGenericFunctions.findBest(beforeRoot, classes, classes);
            if (beforeMethod != null)
                beforeMethod.invoke(Color.class, args);

            Method method = WithGenericFunctions.findBest(root, classes, classes);
            String result = (String) method.invoke(Color.class, args);

            Method afterMethod = WithGenericFunctions.findBest(afterRoot, classes, classes);
            if (afterMethod != null)
                afterMethod.invoke(Color.class, args);


        } catch (Exception e) {

        }

    }

    public static void _it(Integer i) {
        System.out.print(i + " is an integer");
    }

    public static void _it(Double i) {
        System.out.print(i + " is a double");
    }

    public static void _it(String s) {
        System.out.print(s + " is a string");
    }

    @BeforeMethod
    public static void _it(Number n) {
        System.out.print("The number ");
    }

    @AfterMethod
    public static void _it(Object o) {
        System.out.println(".");
    }
}
