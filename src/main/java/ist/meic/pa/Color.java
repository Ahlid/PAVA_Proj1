package main.java.ist.meic.pa;

import ist.meic.pa.GenericFunctions.GenericFunction;

@GenericFunction
class Color {

	public static String mix(Object c1, Object c2) {
		return "objects";
	}
	public static String mix(Color c1, Color c2) {
		return "Strong Color";
	}

	public static String mix(Red c1, Red c2) {
		return "More red";
	}

	public static String mix(Blue c1, Blue c2) {
		return "More blue";
	}

	public static String mix(Yellow c1, Yellow c2) {
		return "More yellow";
	}

	public static String mix(Red c1, Blue c2) {
		return "Magenta";
	}

	public static String mix(Red c1, Yellow c2) {
		return "Orange";
	}

	public static String mix(Blue c1, Yellow c2) {
		return "Green";
	}
}

class Red extends Color {
}

class Blue extends Color {
}

class Yellow extends Color {
}