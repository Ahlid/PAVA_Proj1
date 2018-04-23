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

    private String getInjectedCode(String name, String className, boolean isStatic) {

        return "{\n" +
                "\n" +
                "        ist.meic.pa.GenericFunctions.structure.TypeNode root = (ist.meic.pa.GenericFunctions.structure.TypeNode) typeTree.get(\"" + name + "$original\");\n" +
                "        ist.meic.pa.GenericFunctions.structure.TypeNode beforeRoot = (ist.meic.pa.GenericFunctions.structure.TypeNode) typeTree.get(\"" + name + "$original@BeforeMethod\");\n" +
                "        ist.meic.pa.GenericFunctions.structure.TypeNode afterRoot = (ist.meic.pa.GenericFunctions.structure.TypeNode) typeTree.get(\"" + name + "$original@AfterMethod\");\n" +
                "\n" +
                "        Class[] classes = new Class[$1.length];\n" +
                "\n" +
                "        for (int i = 0; i < $1.length; i++) {\n" +
                "            classes[i] = $1[i].getClass();\n" +
                "        }\n" +
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
                "            return ($r) result;\n" +
                "\n" +
                "        } catch (Exception e) {\n" +
                "            return null;\n" +
                "        }" +
                "}";
    }
}

