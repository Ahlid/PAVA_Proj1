package pa.tests;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.Identify;

public class TestE extends AbstractTest {
    @Test
    public void doTest() throws Throwable {
        WithGenericFunctions.main(new String[]{"pa.tests.TestE"});
        validateOut("E.out");
    }

    public static void main(String[] args) {
        Object objects = new Object[]{123, "Foo", 1.2};
        System.out.println(Identify.it(objects));
    }
}
