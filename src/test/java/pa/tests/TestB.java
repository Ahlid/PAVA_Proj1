package pa.tests;


import org.junit.Test;

import pa.tests.domain.Com;

public class TestB  extends AbstractTest{
	@Test
	public void doTest() {
		TestB.main(null);
		validateOut("B");
	}
    public static void main(String[] args) {
        Object[] objects = new Object[] { new Object(), "Foo", 123};
        for(Object c : objects) System.out.println(Com.bine(c));
    }
}
