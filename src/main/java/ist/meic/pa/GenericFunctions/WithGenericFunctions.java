package ist.meic.pa.GenericFunctions;

import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WithGenericFunctions {

	/**
	 * Accepts, as arguments, the name of another Java program (i.e., a Java class
	 * that also contains a static method main) and the arguments that should be
	 * provided to that program. <br>
	 * The class should (1) operate the necessary transformations to the loaded Java
	 * classes so that when the classes are executed, the semantics of method calls
	 * to methods defined in classes or interfaces annotated as
	 * {@link GenericFunction} follow CLOS semantics, and (2) should transfer the
	 * control to the main method of the program
	 * 
	 * 
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {

		if (args.length != 1) {
			System.out.println("Incorrect use of WithGenericFunctions <ProgramName>");
			System.exit(-1);
		}

		String programName = args[0];
		String[] restArgs = new String[args.length - 1];
		System.arraycopy(args, 1, restArgs, 0, restArgs.length);

		// load class with args
		init(programName, restArgs);
	}

	private static void init(String name, String[] args) throws Throwable {

		// configure classpoll and add translator to loader
		ClassPool pool = ClassPool.getDefault();
		Translator translator = new GFTranslator();
		Loader classLoader = new Loader();

		// Add the custom translator
		classLoader.addTranslator(pool, translator);
		classLoader.run(name, args);
	}
}
