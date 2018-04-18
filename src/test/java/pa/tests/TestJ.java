package pa.tests;

import pa.tests.domain.*;

import java.util.NoSuchElementException;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;

public class TestJ  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestJ"});
		validateOut("J");
	}
    public static void main(String[] args) {
        Color blue = new Blue();
        What.is(blue);
    }

}
