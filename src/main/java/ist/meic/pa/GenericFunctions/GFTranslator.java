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
                List<CtMethod> methods = Stream.of(ctClass.getMethods()).filter(m -> m.getName().equals(entry.getKey()))
                        .collect(Collectors.toList());

                //rename the methods
                for (CtMethod ctMethod : methods) {
//                	CtMethod proxy = generateProxy(ctMethod, ctClass);
                    ctMethod.setName(ctMethod.getName() + "$original");
//                    ctClass.addMethod(proxy);
                }

                //add the new method
				CtMethod ctMethod = CtMethod
						.make("public " + (Modifier.isStatic(methods.get(0).getModifiers()) ? "static " : "")
								+ methods.get(0).getReturnType().getName() + " " + entry.getKey() + "(Object[] hmm){"
								+ " return null;}", ctClass);
                ctMethod.setBody(getInjectedCode(entry.getKey(), className, Modifier.isStatic(methods.get(0).getModifiers())));
                ctClass.addMethod(ctMethod);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InitializationException("Class " + className + " was not able to make generic.");
        }
    }

    /**
     * Generate a method that redirects the call to a generic function
     * @throws CannotCompileException 
     * @throws NotFoundException 
     */
    private CtMethod generateProxy(CtMethod ctMethod, CtClass ctClass) throws NotFoundException, CannotCompileException {
    	CtMethod methodProxy = new CtMethod(ctMethod.getReturnType(), ctMethod.getName(), ctMethod.getParameterTypes(), ctClass);
        methodProxy.setModifiers(ctMethod.getModifiers());
        
        StringBuilder sb = new StringBuilder("{ return ");
        	
        if (javassist.Modifier.isStatic(ctMethod.getModifiers()))
        	sb.append(String.format("%s.%s.", ctClass.getPackageName(), ctClass.getSimpleName()));
        sb.append(String.format("%s(new Object[] {$$});", ctMethod.getName()));
        sb.append("}");
        methodProxy.setBody(sb.toString());
        
        return methodProxy;
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
                "            return ($r) result;\n" +
                "\n" +
                "        } catch (Exception e) {\n" +
                "            return null;\n" +
                "        }" +
                "}";
    }
}

