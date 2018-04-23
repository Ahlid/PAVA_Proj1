package pa.tests;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;
import pa.tests.domain.Color;
import pa.tests.domain.Red;
import pa.tests.domain.SuperBlack;

public class CacheTimeGainTest {

	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.CacheTimeGainTest" });
		// validateOut("cache");
	}

	public static void main(String[] args) {
		Object[] colors = new Color[] { new Red(), new SuperBlack(), new Red() };
		for (int i = 0; i < 20000; i++)
		for (Object c1 : colors)
			for (Object c2 : colors)
				for (Object c3 : colors)
							Color.mix(c1,c2,c3);

	}
}
