package pa.tests;

import org.junit.Test;

import pa.tests.domain.Bug;
import pa.tests.domain.C1;
import pa.tests.domain.C2;

public class TestF  extends AbstractTest{
	@Test
	public void doTest() {
		TestF.main(null);
		validateOut("F");
	}
    public static void main(String[] args) {
        Object c1 = new C1(), c2 = new C2();
        Bug.bug(c1);
        Bug.bug(c2);
    }
}
