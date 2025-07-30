package project.controller.error;

@SuppressWarnings("serial")
public class DuplicateEntryException extends RuntimeException {

	public DuplicateEntryException(String string) { // A Custom Error For If The Value Trying To Use Already Exists
		super(string);
	}

}
