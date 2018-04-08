package ist.meic.pa.GenericFunctions.strategies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ist.meic.pa.GenericFunctions.exception.InitializationException;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class GFTMapStrategy {

	public static String staticMethod() {
		System.out.println("static method called");
		return "";
	}
	
	public static void prepare(CtClass ctClass) throws InitializationException {
		try {
			CtMethod[] methods = ctClass.getDeclaredMethods();
			List<CtMethod> newMethods = new ArrayList<CtMethod>();

			for (CtMethod method : methods) {
				newMethods.add(replaceMethod(method, ctClass));
			}
			
			for (CtMethod method : newMethods)
				ctClass.addMethod(method);

			ctClass.writeFile("c:\\dev\\class.txt");
		} catch(NotFoundException | CannotCompileException e) {
			throw new InitializationException("Prepare method failure", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}

	/**
	 * Creates a copy of the old method and renames the original method to methodName$original
	 * @param m
	 * @param ctClass
	 * @throws NotFoundException 
	 * @throws CannotCompileException 
	 */
	private static CtMethod replaceMethod(CtMethod method, CtClass ctClass) throws NotFoundException, CannotCompileException {
		String methodName = method.getName();
		method.setName(methodName + "$original");

		CtMethod newMethod = new CtMethod(method.getReturnType(), methodName, method.getParameterTypes().clone(), ctClass);
		newMethod.setModifiers(method.getModifiers());

		CtClass[] parameterList = method.getParameterTypes();
		List<String> args = new ArrayList<String>();

		StringBuilder sb = new StringBuilder("{");

		for (int i = 1; i<=parameterList.length; i++) {
			sb.append(String.format("Class c%s = $%s.getClass();",i,i));
			args.add("c"+i+".cast($"+i+")");
		}
		
		sb.append(String.format("return %s$original(%s);", methodName, String.join(",", args)));
		sb.append("}");
		newMethod.setBody(sb.toString());
//ctClass.getClass().getInterfaces()
		return newMethod;
	}

	
}
