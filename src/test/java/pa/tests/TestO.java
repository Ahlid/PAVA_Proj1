package pa.tests;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.C1;
import pa.tests.domain.C2;
import pa.tests.domain.MakeIt;

public class TestO  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestO"});
		validateOut("O");
	}
    public static void main(String[] args) {
        Object c = new C1();
        MakeIt.ddouble(c);
    }
}
