package pa.tests;

import pa.tests.domain.*;

import java.util.NoSuchElementException;

import org.junit.Test;

public class TestJ  extends AbstractTest{
	@Test
	public void doTest() {
		TestJ.main(null);
		validateOut("J");
	}
    public static void main(String[] args) {
        Color blue = new Blue();
        What.is(blue);
    }

}
