package ist.meic.pa;

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
            try {
                typeTree = WithGenericFunctions.getTypeTree(Color.class);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        TypeNode root = typeTree.get("_mix");

        Class[] classes = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
        }



        try {
            Method method = WithGenericFunctions.findBest(root, classes, classes);
            return (String) method.invoke(Color.class, args);
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

}

class Red extends Color {
}

class Blue extends Color {
}

class Yellow extends Color {
}
