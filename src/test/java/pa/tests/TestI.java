package pa.tests;


import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.*;

public class TestI  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestI"});
		validateOut("I");
	}
    public static void main(String[] args) {
        Object[] colors = new Color[]{new SuperBlack(), new Red()};
        for (Object o : colors) What.is(o);
    }
}
