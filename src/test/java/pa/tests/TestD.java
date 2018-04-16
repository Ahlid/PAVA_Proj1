package pa.tests;

import org.junit.Test;

import pa.tests.domain.Com;


public class TestD  extends AbstractTest{
	@Test
	public void doTest() {
		TestD.main(null);
		validateOut("D");
	}
    public static void main(String[] args) {
        Object objects = new Object[] { "Foo", new Integer[] {123, -12}};
        System.out.println(Com.bine(objects));

        Object numbers = new Object[] { 123, new Integer[] {456 , 21}};
        System.out.println(Com.bine(numbers));
    }
}
