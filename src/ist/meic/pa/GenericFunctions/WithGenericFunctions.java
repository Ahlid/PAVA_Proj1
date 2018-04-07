package ist.meic.pa.GenericFunctions;

import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class WithGenericFunctions {

    public static void main(String[] args) throws Throwable {

        if (args.length != 1) {
            System.out.println("Incorrect use of WithGenericFunctions <ProgramName>");
            System.exit(0);
        }

        String programName = args[0];
        String[] restArgs = new String[args.length - 1];
        System.arraycopy(args, 1, restArgs, 0, restArgs.length);
        //load class with args

        init(programName, restArgs);

    }

    private static void init(String name, String[] args) throws Throwable {

        //configure classpoll and add translator to loader
        ClassPool pool = ClassPool.getDefault();
        Translator translator = new GFTranslator();
        Loader classLoader = new Loader();
        classLoader.addTranslator(pool, translator);
        classLoader.run(name, args);

    /*    //get the class to run the main method
        CtClass ctClass = pool.get(name);
        Class<?> rtClass = ctClass.toClass();

        Method main = rtClass.getMethod("main", args.getClass());
        main.invoke(null, new Object[]{args});
        */
    }
}
