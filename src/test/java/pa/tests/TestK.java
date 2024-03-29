package pa.tests;


import pa.tests.domain.ArrayCom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;

public class TestK extends AbstractTest {
    @Test
    public void doTest() throws Throwable {
        WithGenericFunctions.main(new String[]{"pa.tests.TestK"});
        validateOut("K");
    }

    public static void main(String[] args) {
        List<Object> a = new ArrayList<>();
        a.add("Hello");
        a.add(1);
        a.add('A');

        List<Object> b = new LinkedList<>();
        b.add(2);
        b.add('B');

        System.out.println(ArrayCom.bine(a, b));
    }
}
