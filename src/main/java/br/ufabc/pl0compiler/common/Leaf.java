package pl0compiler.common;

public class Leaf extends Node {

	public Type type;
	public String lexval;

	public Leaf(Type type, String lexval) {
		super(type);
		this.type = type;
		this.lexval = lexval;
	}
}
