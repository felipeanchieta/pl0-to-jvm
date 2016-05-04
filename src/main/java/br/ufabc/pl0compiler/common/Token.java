package pl0compiler.common;

public class Token {

	public final Type type;
	public final String c;
	public final String value;

	public Token(Type t, String c) {
		this.type = t;
		this.c = c;
		this.value = null;
	}

	public Token(Type t, String c, String v) {
		this.type = t;
		this.c = c;
		this.value = v;
	}

}