package pa.tests;

import org.junit.Test;

import pa.tests.domain.Black;
import pa.tests.domain.Blue;
import pa.tests.domain.Color;
import pa.tests.domain.Red;

public class TestA extends AbstractTest{
	@Test
	public void doTest() {
		TestA.main(null);
		validateOut("A");
	}
    public static void main(String[] args) {
        Color[] colors = new Color[] { new Red(), new Blue(), new Black()};
        for(Color c : colors) System.out.println(Color.mix(c));
    }

}
