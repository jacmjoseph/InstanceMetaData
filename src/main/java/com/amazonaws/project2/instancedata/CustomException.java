package com.amazonaws.project2.instancedata;

/**
 * This class is a custom exception class created to throw unchecked exceptions
 * 
 * @author jacmj
 *
 */
public class CustomException extends RuntimeException {

	/**
	 * SerialverisonUID for serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is the constructor for the CustomException class to initialize
	 * 
	 * @param message
	 * @param cause
	 */
	public CustomException(String message, Throwable cause) {
		super(message, cause);
	}
}