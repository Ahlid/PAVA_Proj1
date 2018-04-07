package ist.meic.pa.GenericFunctions;

import javassist.*;

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
                    System.out.println(className);
                    applyMultipleDispatch(loadedClass);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void applyMultipleDispatch(CtClass ctClass) {
        System.out.println("Aqui aplicar o multiple dispatch");
    }
}
