package ist.meic.pa.tests;


import ist.meic.pa.tests.domain.*;

public class TestI {
    public static void main(String[] args) {
        Object[] colors = new Color[]{new SuperBlack(), new Red()};
        for (Object o : colors) What.is(o);
    }
}
