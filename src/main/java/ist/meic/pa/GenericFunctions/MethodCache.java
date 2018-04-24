package ist.meic.pa.GenericFunctions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ist.meic.pa.GenericFunctions.structure.TypeNode;

public class MethodCache {

    private HashMap<String, Method> methodsMap;
    private HashMap<String, Method[]> beforeMethodsMap;
    private HashMap<String, Method[]> afterMethodsMap;
    private List<String> cachedArgs;

    public MethodCache() {
        this.methodsMap = new HashMap<>();
        this.beforeMethodsMap = new HashMap<>();
        this.afterMethodsMap = new HashMap<>();
        this.cachedArgs = new ArrayList<>();
    }

    public Method getMethod(Class[] classes) {


        String key = getKey(classes);
        return methodsMap.get(key);

    }

    public void cacheMethod(Class[] classes, Method method) {

        String key = getKey(classes);
        methodsMap.put(key, method);
        this.registerKey(key);
    }

    public Method[] getAfterMethods(Class[] classes) {


        String key = getKey(classes);
        return afterMethodsMap.get(key);

    }

    public void cacheAfterMethods(Class[] classes, Method[] methods) {

        String key = getKey(classes);
        afterMethodsMap.put(key, methods);
        this.registerKey(key);
    }

    public Method[] getBeforeMethods(Class[] classes) {


        String key = getKey(classes);
        return beforeMethodsMap.get(key);

    }

    public void cacheBeforeMethods(Class[] classes, Method[] methods) {

        String key = getKey(classes);
        beforeMethodsMap.put(key, methods);
        this.registerKey(key);

    }

    public boolean isCached(Class[] classes) {
        String key = getKey(classes);

        return this.cachedArgs.contains(key);
    }

    private void registerKey(String key) {
        if (!cachedArgs.contains(key)) {
            cachedArgs.add(key);
        }
    }


    private String getKey(Class[] classes) {
        String key = "";

        for (Class c : classes) {
            key += c.getName();
        }

        return key;
    }

}
