package pa.tests;

import org.junit.Test;

import pa.tests.domain.ExplainMe;

public class TestP  extends AbstractTest{
	@Test
	public void doTest() {
		TestP.main(null);
		validateOut("P");
	}
    public static void main(String[] args) {
        Object o1 = 2, o2 = 91;
        ExplainMe.twoThings(o1, o2);
    }
}
