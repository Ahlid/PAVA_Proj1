package pa.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
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
	public void validateOut(String testName) {
		File expectedOut = new File("src/test/resources/", testName + ".out");
		if (expectedOut.exists()) {
			String got = dumpFile(new File(temp.getRoot(), "temp.log"));
			String expected = dumpFile(expectedOut);
			assertEquals(expected, got);
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
