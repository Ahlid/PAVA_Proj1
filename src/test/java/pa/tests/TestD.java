package pa.tests;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.Com;


public class TestD  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestD"});
		validateOut("D");
	}
    public static void main(String[] args) {
        Object objects = new Object[] { "Foo", new Integer[] {123, -12}};
        System.out.println(Com.bine(objects));

        Object numbers = new Object[] { 123, new Integer[] {456 , 21}};
        System.out.println(Com.bine(numbers));
    }
}
