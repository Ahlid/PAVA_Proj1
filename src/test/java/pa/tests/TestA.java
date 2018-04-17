package pa.tests;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.Black;
import pa.tests.domain.Blue;
import pa.tests.domain.Color;
import pa.tests.domain.Red;

public class TestA extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestA"});
		validateOut("A");
	}
    public static void main(String[] args) {
        Color[] colors = new Color[] { new Red(), new Blue(), new Black()};
        for(Color c : colors)
        	System.out.println(Color.mix(c));
    }

}
