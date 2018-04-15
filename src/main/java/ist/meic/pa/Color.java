package ist.meic.pa;

import ist.meic.pa.GenericFunctions.AfterMethod;
import ist.meic.pa.GenericFunctions.BeforeMethod;
import ist.meic.pa.GenericFunctions.GenericFunction;
import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import ist.meic.pa.GenericFunctions.structure.TypeNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@GenericFunction
class Color {


    static Map<String, TypeNode> typeTree;

    public static String _mix(Object c1, Object c2) {
        return "objects";
    }

    public static String mix(Object... args) {

        if (typeTree == null) {

            typeTree = WithGenericFunctions.getTypeTree(Color.class);

        }

        TypeNode root = typeTree.get("_mix");
        TypeNode beforeRoot = typeTree.get("_mix@BeforeMethod");
        TypeNode afterRoot = typeTree.get("_mix@AfterMethod");

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

            return result;

        } catch (Exception e) {
            return null;
        }

    }

    public static String _mix(Color c1, Color c2) {
        return mix(c2, c1);
    }

    public static String _mix(Color c1, Color c2, Color c3) {
        return "Super Strong Color";
    }

    public static String _mix(Color c1, Color c2, Color c3, Color c4) {
        return "Super Super Strong Color";
    }

    public static String _mix(Red c1, Red c2) {
        return "More red";
    }

    public static String _mix(Blue c1, Blue c2) {
        return "More blue";
    }

    public static String _mix(Yellow c1, Yellow c2) {
        return "More yellow";
    }

    public static String _mix(Red c1, Blue c2) {
        return "Magenta";
    }

    public static String _mix(Red c1, Yellow c2) {
        return "Orange";
    }

    public static String _mix(Blue c1, Yellow c2) {
        return "Green";
    }

    @BeforeMethod
    public static void _mix(Color c1, Blue c2) {
        System.out.print("Here comes a blue: ");
    }

    @AfterMethod
    public static void _mix(Object c1, Blue c2) {
        System.out.print(" ---> result of mixing blue");
    }

}

class Red extends Color {
}

class Blue extends Color {
}

class Yellow extends Color {
}
