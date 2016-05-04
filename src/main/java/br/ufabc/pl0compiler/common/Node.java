package pl0compiler.common;

import java.util.Stack;

public class Node {

	public Type operator;
	public Node[] operands;
	public Stack stack;

	public Node(Type operator) {
		this.operator = operator;
		operands = new Node[0];
	}

	public Node(Type operator, Node... operands) {
		this.operator = operator;
		this.operands = operands;
	}

	public Node(Stack stack) {
		this.stack = stack;
	}
}
