package ist.meic.pa.tests;

import ist.meic.pa.tests.domain.*;

public class TestC {
    public static void main(String[] args) {
        Object colors = new Object[]{new Red(), 2.9, new Black(), "Holla!"};

        System.out.println(Color.mix(colors));
    }
}

