package pa.tests;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.*;

public class TestC  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestC"});
		validateOut("C");
	}
    public static void main(String[] args) {
        Object colors = new Object[] { new Red(), 2.9, new Black(), "Holla!"};
        System.out.println(Color.mix(colors));
    }
}

