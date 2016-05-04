package pl0compiler.translator;

class Instruction {

	public String cmd;
	public String arg;
	public int jump;

	public Instruction(String cmd, String arg, int jump) {
		this.cmd = cmd;
		this.arg = arg;
		this.jump = jump;
	}

	public Instruction(String cmd) {
		this(cmd, "", 0);
	}
}
