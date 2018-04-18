package pa.tests;

import pa.tests.domain.ArrayCom;
import java.util.Arrays;

import org.junit.Test;

import ist.meic.pa.GenericFunctions.WithGenericFunctions;

public class TestG  extends AbstractTest{
	@Test
	public void doTest() throws Throwable {
		WithGenericFunctions.main(new String[] { "pa.tests.TestG"});
		validateOut("G");
	}
    public static void main(String[] args) {
        println(ArrayCom.bine(1, 3));
        println(ArrayCom.bine(new Object[] { 1, 2, 3 }, new Object[] { 4, 5, 6 }));
        println(ArrayCom.bine(new Object[] { new Object[] { 1, 2 }, 3 },
                new Object[] { new Object[] { 3, 4 }, 5 }));
    }
    public static void println(Object obj) {
        if (obj instanceof Object[]) {
            System.out.println(Arrays.deepToString((Object[])obj));
        } else {
            System.out.println(obj);
        }
    }
}
