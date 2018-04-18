package pa.tests;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.Color;
import pa.tests.domain.Red;
import pa.tests.domain.SuperBlack;

public class TestN  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestN"});
		validateOut("N");
	}
    public static void main(String[] args) {
        Object red1 = new Red(), red2 = new Red(), black = new SuperBlack();
        System.out.println(Color.mix(red1, black, red2));
        System.out.println(Color.mix(black, black, red2));
        System.out.println(Color.mix(black, black, black));
    }
}
