/* handles the compiler errors */
public class Error {

	private ErrorType errorType;

	public static String quickError(ErrorType e) {
		switch (e) {
		case LINE_ARGUMENT:
			return "Fatal error: Line arguments don\'t match the pattern";
		case FILE_NAME:
			return "Fatal error: File doesn\'t exist or it can\'t be accessed";
		default:
			return "";
		}
	}
	
}