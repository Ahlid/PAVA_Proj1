package ist.meic.pa.GenericFunctions;

import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class WithGenericFunctions {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Incorrect use of WithGenericFunctions <ProgramName>");
            System.exit(0);
        }

        String programName = args[0];
        String[] restArgs = new String[args.length - 1];
        System.arraycopy(args, 1, restArgs, 0, restArgs.length);
        //load class with args
        try {
            loadClass(programName, restArgs);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void loadClass(String name, String[] args) throws NotFoundException, CannotCompileException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassPool pool = ClassPool.getDefault();
        Loader classLoader = new Loader(pool);
        CtClass ctClass = pool.get(name);
        CtMethod ctMethod = ctClass.getDeclaredMethod("main");
        ctMethod.setBody("{\n" +
                "        System.out.println(\"NOT Running\");\n" +
                "    }");
        Class<?> rtClass = ctClass.toClass();

        Method main = rtClass.getMethod("main", args.getClass());
        main.invoke(null, new Object[]{args});
    }
}
