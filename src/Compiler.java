import java.io.File;

public class Compiler {

	private static ErrorType errorType;

	public static void main(String[] args) {
		Compiler compiler = new Compiler();
		compiler.run(args);
	}

	private static void run(String[] args) {
		if (args.length < 1) {
			System.err.println(Error.quickError(errorType.LINE_ARGUMENT));
			System.exit(-1);
		} else {
			startCompilerProcesses(args[0]);
		}
	}

	private static void startCompilerProcesses(String fileName) {
		File file = null; 
		try {
			file = new File(fileName);
		} catch (Exception e) {
			System.err.println("Fucked");
		}
	}
}