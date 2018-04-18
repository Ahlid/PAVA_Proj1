package pa.tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class AbstractTest {
	@Rule public TemporaryFolder temp = new TemporaryFolder();
	@Before
	public void captureStdout() throws FileNotFoundException {
		System.setOut(new PrintStream(new File(temp.getRoot(), "temp.log")));
	}

	//@After
	public void validateOut(String testName) throws Exception {
		File expectedOut = new File("src/test/resources/", testName + ".out");
		String got = null;
		String expected = null;
		try {
			if (expectedOut.exists()) {
				got = dumpFile(new File(temp.getRoot(), "temp.log"));
				expected = dumpFile(expectedOut);
				assertEquals(expected, got);
			}
		} catch (AssertionError e) {
			throw new Exception(String.format(
					"\n---- got ----\n%s---------"
				  + "---- expected ----\n%s---------\n%s", got, expected, e.getMessage()));
		}
	}

	private String dumpFile(File outFile) {
		BufferedReader reader = null;
	    try {
	    	reader = new BufferedReader(new FileReader(outFile));
	    	String line = null;
	    	StringBuilder stringBuilder = new StringBuilder();
	        
	    	while((line = reader.readLine()) != null) {
	            stringBuilder.append(line + System.lineSeparator());
	        }

	    	return stringBuilder.toString();
	    } catch (Exception e) {
	    	// force failure
	    	assertEquals(e.getMessage(), 1, 2);
		} finally {
	        try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		return null;
	}

}
