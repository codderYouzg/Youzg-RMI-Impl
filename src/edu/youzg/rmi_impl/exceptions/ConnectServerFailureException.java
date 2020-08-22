package edu.youzg.rmi_impl.exceptions;

public class ConnectServerFailureException extends Exception {
	private static final long serialVersionUID = 6593999239060169503L;

	public ConnectServerFailureException() {
		super();
	}

	public ConnectServerFailureException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConnectServerFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectServerFailureException(String message) {
		super(message);
	}

	public ConnectServerFailureException(Throwable cause) {
		super(cause);
	}

}
