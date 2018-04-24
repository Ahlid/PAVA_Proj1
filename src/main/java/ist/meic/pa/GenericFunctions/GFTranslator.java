package ist.meic.pa.GenericFunctions;

import ist.meic.pa.GenericFunctions.exception.InitializationException;
import javassist.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GFTranslator implements Translator {
    @Override
    public void start(ClassPool classPool) throws NotFoundException, CannotCompileException {
        //??
    }


    @Override
    public void onLoad(ClassPool classPool, String className) throws NotFoundException, CannotCompileException {

        CtClass loadedClass = classPool.get(className);

        try {
            Object[] annotations = loadedClass.getAnnotations();
            for (Object annotation : annotations) {
                if (annotation instanceof GenericFunction) {
                    applyMultipleDispatch(loadedClass, className);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies the multidispatch to the class
     * @param ctClass - the class to apply it
     * @param className - the name of the class
     * @throws InitializationException
     */
    public void applyMultipleDispatch(CtClass ctClass, String className) throws InitializationException {

        try {
            Class clazz = Class.forName(className);
            Map<String, Integer> counts = WithGenericFunctions.getConflicts(clazz);

            //add the tree map to the class
            CtField ctField = CtField.make(
                    " java.util.Map typeTree =" +
                            " ist.meic.pa.GenericFunctions.WithGenericFunctions.getTypeTree(" + className + ".class);",
                    ctClass);

            ctField.setModifiers(Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC);
            ctClass.addField(ctField);

            //add the tree map to the class
            CtField ctFieldCache = CtField.make(
                    "ist.meic.pa.GenericFunctions.MethodCache my$cache =" +
                            " new ist.meic.pa.GenericFunctions.MethodCache();",
                    ctClass);

            ctFieldCache.setModifiers(Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC);
            ctClass.addField(ctFieldCache);

            //check the methods to apply the genericFunction method
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {

                //if there is no conflict continue
                if (entry.getValue() < 2) {
                    continue;
                }

                //get the methods
                List<CtMethod> methods = Stream.of(ctClass.getDeclaredMethods()).filter(m -> m.getName().equals(entry.getKey()))
                        .collect(Collectors.toList());


                //add the new method
                CtMethod ctMethodC = CtMethod
                        .make("public " + (Modifier.isStatic(methods.get(0).getModifiers()) ? "static " : "")
                                + "Object" + " " + entry.getKey() + "$dispatcher" + "(Object[] hmm){"
                                + " return null;}", ctClass);
                ctMethodC.setBody(getInjectedCode(entry.getKey(), className, Modifier.isStatic(methods.get(0).getModifiers())));

                ctMethodC.setModifiers(methods.get(0).getModifiers() | Modifier.TRANSIENT);
                ctClass.addMethod(ctMethodC);

                //rename the methods
                for (CtMethod ctMethod : methods) {
                    ctMethod.setName(ctMethod.getName() + "$original");
                }

                for (CtMethod ctMethod : methods) {
                    CtMethod proxy = generateProxy(ctMethod, ctClass, entry.getKey());
                    ctClass.addMethod(proxy);
                }
            }

            try {
                ctClass.writeFile("C:\\Users\\tiago\\Documents\\PAVA_Proj1\\ist\\" + ctClass.getName() + ".java");
            } catch (Exception e) {

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new InitializationException("Class " + className + " was not able to make generic.");
        }
    }

    /**
     * Generate a method that redirects the call to a generic function
     *
     * @throws CannotCompileException
     * @throws NotFoundException
     */
    private CtMethod generateProxy(CtMethod ctMethod, CtClass ctClass, String name) throws NotFoundException, CannotCompileException {


        CtMethod methodProxy = new CtMethod(ctMethod.getReturnType(), name, ctMethod.getParameterTypes(), ctClass);
        methodProxy.setModifiers(ctMethod.getModifiers());

        StringBuilder sb = new StringBuilder("{ return ($r)");

        if (javassist.Modifier.isStatic(ctMethod.getModifiers()))
            sb.append(String.format("%s.%s.", ctClass.getPackageName(), ctClass.getSimpleName()));
        sb.append(String.format("%s($args);", name + "$dispatcher"));
        sb.append("}");

        methodProxy.setBody(sb.toString());

        return methodProxy;
    }

    /**
     * Return the code to inject in the dispatch method
     * @param name - name of the method
     * @param className - name of the class
     * @param isStatic - is the method is static
     * @return - the string with de code to inject
     */
    private String getInjectedCode(String name, String className, boolean isStatic) {


        return "{\n" +
                "        Class[] classes = new Class[$1.length];\n" +
                "\n" +
                "        for (int i = 0; i < $1.length; i++) {\n" +
                "            classes[i] = $1[i].getClass();\n" +
                "        }\n" +
                "\n" +
                //"if(false){\n" +
                "if(my$cache.isCached(classes)){\n" +

                "            java.lang.reflect.Method[] beforeMethods = my$cache.getBeforeMethods(classes);\n" +
                "            java.lang.reflect.Method method = my$cache.getMethod(classes);\n" +
                "            java.lang.reflect.Method[] afterMethods = my$cache.getAfterMethods(classes);\n"+
                "\n"+
                "\n" +
                "            if (beforeMethods != null)\n" +
                "                for (int i = 0; i < beforeMethods.length; i++) {\n" +
                "                	beforeMethods[i].invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "        		 }\n" +
                "\n" +
                "\n" +
                "            Object result =  method.invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "\n" +
                "            if (afterMethods != null)\n" +
                "                for (int i = 0; i < afterMethods.length; i++) {\n" +
                "                	afterMethods[i].invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "        		 }\n" +
                "\n" +
               // "System.out.println(my$cache);\n"+
                "            return ($r) result;\n" +
                "\n" +
                "}\n" +
                // "  if(my$cache.isCached(classes)){\n" +

               /* "            java.util.List<java.lang.reflect.Method> beforeMethods = my$cache.getBeforeMethods(classes);\n" +

                "            if (beforeMethods != null)\n" +
                "                for (java.lang.reflect.Method beforeMethod : beforeMethods) {\n" +
                "                	beforeMethod.invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "        		 }\n" +
                "\n" +
                "\n" +
                "            Object result =  method.invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "\n" +
                "            if (afterMethods != null)\n" +
                "                for (java.lang.reflect.Method afterMethod : afterMethods) {\n" +
                "                	afterMethod.invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "        		 }\n" +
                "\n" +
                "            return ($r) result;\n" +*/
                // "}\n"+
                "\n" +
                "        ist.meic.pa.GenericFunctions.structure.TypeNode root = (ist.meic.pa.GenericFunctions.structure.TypeNode) typeTree.get(\"" + name + "$original\");\n" +
                "        ist.meic.pa.GenericFunctions.structure.TypeNode beforeRoot = (ist.meic.pa.GenericFunctions.structure.TypeNode) typeTree.get(\"" + name + "$original@BeforeMethod\");\n" +
                "        ist.meic.pa.GenericFunctions.structure.TypeNode afterRoot = (ist.meic.pa.GenericFunctions.structure.TypeNode) typeTree.get(\"" + name + "$original@AfterMethod\");\n" +
                "\n" +
                "\n" +
                "        try {\n" +
                "\n" +
                "            java.lang.reflect.Method[] beforeMethods = ist.meic.pa.GenericFunctions.WithGenericFunctions.findBeforeHooks(beforeRoot, classes);\n" +
                "            if (beforeMethods != null)\n" +
                "                for (int i = 0; i < beforeMethods.length; i++) {\n" +
                "                	beforeMethods[i].invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "        		 }\n" +
                "\n" +
                "            java.lang.reflect.Method method = ist.meic.pa.GenericFunctions.WithGenericFunctions.findBest2(root, classes);\n" +
                "            Object result =  method.invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "             java.lang.reflect.Method[] afterMethods = ist.meic.pa.GenericFunctions.WithGenericFunctions.findAfterHooks(afterRoot, classes);\n" +
                "            if (afterMethods != null)\n" +
                "                for (int i = 0; i < afterMethods.length; i++) {\n" +
                "                	afterMethods[i].invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "        		 }\n" +
                "\n" +
                "my$cache.cacheMethod(classes, method);\n" +
                "my$cache.cacheBeforeMethods(classes, beforeMethods);\n" +
                "my$cache.cacheAfterMethods(classes, afterMethods);\n" +
                "\n" +
                "            return ($r) result;\n" +
                "\n" +
                "        } catch (Exception e) {\n" +
                "            return null;\n" +
                "        }" +
                "}";
    }
}

