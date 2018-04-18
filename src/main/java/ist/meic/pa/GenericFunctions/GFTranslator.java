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

           /* CtField ctField =
                    CtField.make("static java.util.Hashtable cachedResults = " +
                                    " new java.util.Hashtable();",
                            ctClass);*/


            ctClass.addField(ctField);

            //check the methods to apply the genericFunction method
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {

                //if there is no conflit continue
                if (entry.getValue() < 2) {
                    continue;
                }

                //get the methtods
                List<CtMethod> methods = Stream.of(ctClass.getMethods()).filter(m -> m.getName().equals(entry.getKey()))
                        .collect(Collectors.toList());

                String methodName = methods.get(0).getName();

                //rename the methods
                for (CtMethod ctMethod : methods) {
                    ctMethod.setName(ctMethod.getName() + "$original");
                }

                CtMethod aux = methods.get(0);

                //add the new method
                CtMethod ctMethod = CtMethod.make("public " + (Modifier.isStatic(methods.get(0).getModifiers()) ? "static " : "") + "Object" + " " + methodName + "(Object[] hmm){" +
                        " return null;}", ctClass);
                ctMethod.setBody(getInjectedCode(entry.getKey(), className, Modifier.isStatic(methods.get(0).getModifiers())));
                ctMethod.setModifiers(aux.getModifiers() | Modifier.TRANSIENT);
                ctClass.addMethod(ctMethod);


            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new InitializationException("Class " + className + " was not able to make generic.");
        }

        try {
            ctClass.writeFile("C:\\Users\\tiago\\Documents\\PAVA_Proj1\\ist\\class" + className + ".java");
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private String getInjectedCode(String name, String className, boolean isStatic) {

        return "{\n" +
                "\n" +
                "        ist.meic.pa.GenericFunctions.structure.TypeNode root = (ist.meic.pa.GenericFunctions.structure.TypeNode) typeTree.get(\"" + name + "$original\");\n" +
                "        ist.meic.pa.GenericFunctions.structure.TypeNode beforeRoot = (ist.meic.pa.GenericFunctions.structure.TypeNode) typeTree.get(\"" + name + "$original@BeforeMethod\");\n" +
                "       ist.meic.pa.GenericFunctions.structure.TypeNode afterRoot = (ist.meic.pa.GenericFunctions.structure.TypeNode) typeTree.get(\"" + name + "$original@AfterMethod\");\n" +
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
                "            java.lang.reflect.Method beforeMethod = ist.meic.pa.GenericFunctions.WithGenericFunctions.findBest(beforeRoot, classes, classes);\n" +
                "            if (beforeMethod != null)\n" +
                "                beforeMethod.invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "\n" +
                "            java.lang.reflect.Method method = ist.meic.pa.GenericFunctions.WithGenericFunctions.findBest(root, classes, classes);\n" +
                " System.out.println(method);\n" +
                "            Object result =  method.invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                " System.out.println(method);\n" +
                "            java.lang.reflect.Method afterMethod = ist.meic.pa.GenericFunctions.WithGenericFunctions.findBest(afterRoot, classes, classes);\n" +
                "            if (afterMethod != null)\n" +
                "                afterMethod.invoke(" + (isStatic ? className + ".class" : "this") + ", $1);\n" +
                "\n" +
                "            return result;\n" +
                "\n" +
                "        } catch (Exception e) {\n" +
                "            return null;\n" +
                "        }" +
                "}";
    }
}

