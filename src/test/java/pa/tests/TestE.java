package pa.tests;

import org.junit.Test;

import pa.tests.domain.Identify;

public class TestE  extends AbstractTest{
	@Test
	public void doTest() {
		TestE.main(null);
		validateOut("E");
	}
    public static void main(String[] args) {
        Object objects = new Object[] { 123, "Foo", 1.2};
        System.out.println(Identify.it(objects));
    }
}
