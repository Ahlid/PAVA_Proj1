package ist.meic.pa;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        System.out.println("Running");
        Color[] colors = new Color[] { new Red(), new Blue(), new Yellow() };

        Color.init();
     //   System.out.println(Color.myMap);


        for (Color c1 : colors) {
            for (Color c2 : colors) {
                System.out.println(Color.mixTest(c1, c2));
            }
        }

    }
}
