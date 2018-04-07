package ist.meic.pa;

import ist.meic.pa.GenericFunctions.GenericFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;

@GenericFunction
class Color {


    // test stuff
    public static HashMap myMap = new HashMap();


    public static void init() throws ClassNotFoundException {

        Class color = Color.class;

        Method[] methods = color.getDeclaredMethods();

        for (Method m : methods) {

            Type[] types = m.getGenericParameterTypes();
            HashMap lastMap = myMap;

            for (int i = 0; i < types.length; i++) {

                Type t = types[i];

                if (i == types.length - 1) {
                    lastMap.put(Class.forName(t.getTypeName()), m);
                    break;
                }


                HashMap typeMap = (HashMap) lastMap.get(Class.forName(t.getTypeName()));
                if (typeMap == null) {
                    typeMap = new HashMap();
                }

                lastMap.put(Class.forName(t.getTypeName()), typeMap);
                lastMap = typeMap;
            }
        }

    }


    public static String mixTest(Object c1, Object c2) throws InvocationTargetException, IllegalAccessException {


        try {
            return (String) ((Method) ((HashMap) myMap.get(c1.getClass())).get(c2.getClass())).invoke(null, c1, c2);
        } catch (NullPointerException e) {
            return (String) ((Method) ((HashMap) myMap.get(c2.getClass())).get(c1.getClass())).invoke(null, c2, c1);

        }

    }


    //end test stuff


    public static String mix(Color c1, Color c2) {
        return mix(c2, c1);
    }

    public static String mix(Red c1, Red c2) {
        return "More red";
    }

    public static String mix(Blue c1, Blue c2) {
        return "More blue";
    }

    public static String mix(Yellow c1, Yellow c2) {
        return "More yellow";
    }

    public static String mix(Red c1, Blue c2) {
        return "Magenta";
    }

    public static String mix(Red c1, Yellow c2) {
        return "Orange";
    }

    public static String mix(Blue c1, Yellow c2) {
        return "Green";
    }
}

class Red extends Color {
}

class Blue extends Color {
}

class Yellow extends Color {
}