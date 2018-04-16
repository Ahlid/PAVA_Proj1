package pa.tests;


import org.junit.Test;

import pa.tests.domain.*;

public class TestI  extends AbstractTest{
	@Test
	public void doTest() {
		TestI.main(null);
		validateOut("I");
	}
    public static void main(String[] args) {
        Object[] colors = new Color[]{new SuperBlack(), new Red()};
        for (Object o : colors) What.is(o);
    }
}
