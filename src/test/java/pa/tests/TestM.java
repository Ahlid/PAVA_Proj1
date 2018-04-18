package pa.tests;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.Blue;
import pa.tests.domain.Color;
import pa.tests.domain.Red;
import pa.tests.domain.Yellow;

public class TestM  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestM"});
		validateOut("M");
	}
    public static void main(String[] args) {
        Color[] colors = new Color[]{new Red(), new Blue(), new Yellow()};
        for (Color c1 : colors)
            for (Color c2 : colors)
                System.out.println(Color.mix(c1, c2));
    }
}
