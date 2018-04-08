package ist.meic.pa.GenericFunctions.exception;

public class InitializationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InitializationException(String msg) {
		super(msg);
	}

	public InitializationException(String msg, Exception e) {
		super(msg, e);
	}

}
