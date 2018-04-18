package pa.tests;

import org.junit.Test;

import pa.tests.domain.C1;
import pa.tests.domain.C2;
import pa.tests.domain.MakeIt;

public class TestO  extends AbstractTest{
	@Test
	public void doTest() {
		TestO.main(null);
		validateOut("O");
	}
    public static void main(String[] args) {
        Object c = new C1();
        MakeIt.ddouble(c);
    }
}
