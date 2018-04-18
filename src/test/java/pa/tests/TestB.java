package pa.tests;


import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.Com;

public class TestB  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestB"});
		validateOut("B");
	}
    public static void main(String[] args) {
        Object[] objects = new Object[] { new Object(), "Foo", 123};
        for(Object c : objects) System.out.println(Com.bine(c));
    }
}
