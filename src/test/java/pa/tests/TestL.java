package pa.tests;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.Com;

public class TestL  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestL"});
		validateOut("L");
	}
    public static void main(String[] args) {
        Object[] objs1 = new Object[] { "Hello", 1, 'A' };
        Object[] objs2 = new Object[] { "World", 2, 'B' };

        for (Object o1 : objs1)
            for (Object o2 : objs2)
                System.out.println("Combine(" + o1 + ", " + o2 + ") -> " + Com.bine(o1, o2));
    }
}
