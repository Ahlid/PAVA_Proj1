package pa.tests;

import org.junit.Test;

import pa.tests.domain.Explain;

public class TestH  extends AbstractTest{
	@Test
	public void doTest() {
		TestH.main(null);
		validateOut("H");
	}
    public static void main(String[] args) {
        Object[] objs = new Object[]{"Hello", 1, 2.0};
        for (Object o : objs) Explain.it(o);
    }
}
