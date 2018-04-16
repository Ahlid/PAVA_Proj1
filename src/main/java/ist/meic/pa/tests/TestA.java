package ist.meic.pa.tests;

import ist.meic.pa.tests.domain.*;

import java.lang.reflect.Method;

public class TestA {
    public static void main(String[] args) {
        Color[] colors = new Color[]{new Red(), new Blue(), new Black()};


        Method[] methods = Color.class.getDeclaredMethods();

        for (Method m : methods) {
          //  System.out.println(m);
        }

        for (Color c : colors) System.out.println(Color.mix(new Object[]{c}));
    }

}
