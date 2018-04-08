package main.java.ist.meic.pa;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ColorMixingTreeTest {

    public static void main(String[] args) {
        Color[] colors = {new Red(), new Yellow(), new Blue()};

        for (Color c1 : colors)
            for (Color c2 : colors) {
                String out = Color.mix(c1, c2);
                System.out.println(String.format("%s + %s = %s", c1.getClass().getSimpleName(),
                        c2.getClass().getSimpleName(), out));
            }
    }

    // test stuff
    public static HashMap myMap = new HashMap();

    public static void init() throws ClassNotFoundException {
        Class<Color> color = Color.class;
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

    static private class Node {
        HashMap lastMap;
        int lastIndex;
        Node prevNode;
    }

    public static String mixTest(Object... args) throws InvocationTargetException, IllegalAccessException {

        HashMap lastMap = myMap;
        Method resultedMethod = null;
        Node lastNode = null;
        int mapsEncontred = 0;
        List<Integer> argsInvokeOrder = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {

            Object arg = args[i];

            if (argsInvokeOrder.contains(i)) {
                continue;
            }

            if (mapsEncontred == args.length - 1) {

                Method method = (Method) lastMap.get(arg.getClass());
                if (method != null) {
                    resultedMethod = method;
                    argsInvokeOrder.add(i);
                    break;
                }

            } else {
                HashMap argMap = (HashMap) lastMap.get(arg.getClass());
                if (argMap != null) {

                    Node node = new Node();
                    node.lastIndex = i;
                    node.lastMap = lastMap;
                    node.prevNode = lastNode;
                    lastNode = node;
                    lastMap = argMap;
                    argsInvokeOrder.add(i);
                    mapsEncontred++;
                    i = -1;
                    continue;

                }

            }

            if (i == args.length - 1) {
                lastMap = lastNode.lastMap;
                i = lastNode.lastIndex;
                mapsEncontred--;
                for (int j = 0; j < argsInvokeOrder.size(); j++) {
                    if (argsInvokeOrder.get(j) == lastNode.lastIndex) {
                        argsInvokeOrder.remove(j);
                        break;
                    }
                }

                lastNode = lastNode.prevNode;
            }

        }

        Object[] argumentsToInvoke = new Object[argsInvokeOrder.size()];

        for (int i = 0; i < argsInvokeOrder.size(); i++) {
            int index = argsInvokeOrder.get(i);
            argumentsToInvoke[i] = args[index];
        }

        return (String) resultedMethod.invoke(null, argumentsToInvoke);

    }

    // end test stuff
}
