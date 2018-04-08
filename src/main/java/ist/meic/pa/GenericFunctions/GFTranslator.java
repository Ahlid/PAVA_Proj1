package ist.meic.pa.GenericFunctions;

import ist.meic.pa.GenericFunctions.exception.InitializationException;
import ist.meic.pa.GenericFunctions.strategies.GFTMapStrategy;
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
                    System.out.println(String.format("Generic funtion: %s", className));
                    applyMultipleDispatch(loadedClass);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InitializationException e) {
			e.printStackTrace();
		}
    }

    public void applyMultipleDispatch(CtClass ctClass) throws InitializationException {
    	GFTMapStrategy.prepare(ctClass);
        System.out.println("Aqui aplicar o multiple dispatch");
    }
}
