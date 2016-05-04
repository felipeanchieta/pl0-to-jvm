package pl0compiler.parser;

import pl0compiler.common.Leaf;
import pl0compiler.common.Node;
import pl0compiler.common.Type;
import pl0compiler.common.Token;

import java.util.List;
import java.util.Stack;

public class Parser {

	List<Token> tokens;
	Token lookahead;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		nextToken();
	}

	//pega a lista de tokens do tokenizer

	public Stack<Node> parse()  {
		Stack<Node> stack = null;
		try {
			stack = main();
		} catch (ParserException e) {
			e.printStackTrace();
			System.err.println("Impossible to parse file");
		}

		return stack;
	}

	public void nextToken() {
		lookahead = tokens.remove(0);
		System.out.printf(lookahead.c + " ");
	}

	// Non-terminals
	private Stack<Node> main() throws ParserException {
		Stack<Node> stack;

		programDecl();
		stack = mainBlock();
		_period();

		return stack;
	}

	private void programDecl() throws ParserException {
		nextToken();
		_id();
		_sc();
	}

	private Stack<Node> mainBlock() throws ParserException {
		Stack<Node> stack;

		nextToken();
		stack = mainCode();
		_end();

		return stack;
	}

	private Stack<Node> mainCode() throws ParserException {
		Stack<Node> stack;

		switch (lookahead.type) {
			case VAR:
			case ID:
			case CALL:
			case PRINT:
			case WHILE:
			case IF:
				Node stmt = stmt();
				stack = mainCode();
				stack.push(stmt);
				break;
			case PROCEDURE:
				Node procedure = declProcStmt();
				stack = mainCode();
				stack.push(procedure);
				break;
			default:
				stack = new Stack<Node>();
		}

		return stack;
	}

	private Node block() throws ParserException {
		Stack<Node> stack;

		nextToken();
		stack = code();
		_end();

		return new Node(stack);
	}

	private Stack<Node> code() throws ParserException {
		Stack<Node> stack;

		switch (lookahead.type) {
			case VAR:
			case ID:
			case CALL:
			case PRINT:
			case WHILE:
			case IF:
				Node stmt = stmt();
				stack = code();
				stack.push(stmt);
				break;
			default:
				stack = new Stack<Node>();
		}

		return stack;
	}

	private Node stmt() throws ParserException {
		Node syn;

		switch (lookahead.type) {
			case VAR:
				syn = declVarStmt();
				break;
			case ID:
				syn = attrStmt();
				break;
			case CALL:
				syn = callStmt();
				break;
			case PRINT:
				syn = printStmt();
				break;
			case WHILE:
				syn = whileStmt();
				break;
			default: // IF
				syn = ifStmt();
				break;
		}

		return syn;
	}

	// statements
	private Node declProcStmt() throws ParserException {
		nextToken();
		Node id = _id();
		_sc();
		Node block = block();

		return new Node(Type.PROCEDURE, id, block);
	}

	private Node declVarStmt() throws ParserException {
		Stack<Node> stack;

		nextToken();
		stack = list();
		_sc();

		return new Node(stack);
	}

	private Stack<Node> list() throws ParserException {
		return list_(_id());
	}

	private Stack<Node> list_(Node inh) throws ParserException {
		Stack<Node> stack;

		switch (lookahead.type) {
			case COMMA:
				nextToken();
				stack = list();
				break;
			case ATTR_OP:
				nextToken();
				stack = list__(inh);
				//syn = new Node(Type.ATTR_OP, inh, list__());
				break;
			default:
				stack = new Stack<Node>();
		}

		return stack;
	}

	private Stack<Node> list__(Node inh) throws ParserException {
		Node exp = intExp();
		Stack stack = list___();
		stack.push(new Node(Type.ATTR_OP, inh, exp));
		return stack;
	}

	private Stack<Node> list___() throws ParserException {
		Stack<Node> stack;
		switch (lookahead.type) {
			case COMMA:
				nextToken();
				stack = list();
				break;
			default:
				stack = new Stack<Node>();
		}

		return stack;
	}

	private Node attrStmt() throws ParserException {
		Node id = _id();
		_attrOp();
		Node exp = intExp();
		_sc();

		return new Node(Type.ATTR_OP, id, exp);
	}

	private Node callStmt() throws ParserException {
		nextToken();
		Node id = _id();
		_sc();

		return new Node(Type.CALL, id);
	}

	private Node printStmt() throws ParserException {
		nextToken();
		Node exp = intExp();
		_sc();

		return new Node(Type.PRINT, exp);
	}

	private Node whileStmt() throws ParserException {
		nextToken();
		Node cond = boolExp();
		_do();
		Node ifTrue = block();

		return new Node(Type.WHILE, cond, ifTrue);
	}

	private Node ifStmt() throws ParserException {
		nextToken();
		Node cond = boolExp();
		_then();
		Node ifTrue = block();
		Node ifFalse = elseStmt();

		return new Node(Type.IF, cond, ifTrue, ifFalse);
	}

	private Node elseStmt() throws ParserException {
		Node syn;

		switch (lookahead.type) {
			case ELSE:
				nextToken();
				syn = block();
				break;
			default:
				return null;
		}

		return syn;
	}

	// integer expression
	private Node intExp() throws ParserException {
		return intExp_(term());
	}

	private Node intExp_(Node inh) throws ParserException {
		Node syn;

		switch (lookahead.type) {
			case SUB_OP:
				nextToken();
				syn = new Node(Type.SUB_OP, inh, intExp_(term()));
				break;
			case SUM_OP:
				nextToken();
				syn = new Node(Type.SUM_OP, inh, intExp_(term()));
				break;
			default:
				syn = inh;
		}

		return syn;
	}

	private Node term() throws ParserException {
		return term_(factor());
	}

	private Node term_(Node inh) throws ParserException {
		Node syn;

		switch (lookahead.type) {
			case DIV_OP:
				nextToken();
				syn = new Node(Type.DIV_OP, inh, term_(factor()));
				break;
			case MULT_OP:
				nextToken();
				syn = new Node(Type.MULT_OP, inh, term_(factor()));
				break;
			default:
				syn = inh;
		}

		return syn;
	}

	private Node factor() throws ParserException {
		Node syn;

		switch (lookahead.type) {
			case ID:
				syn = _id();
				break;
			case INT:
				syn = _int();
				break;
			case L_PAR:
				nextToken();
				syn = intExp();
				_rPar();
				break;
			default:
				throw new ParserException("fator inválido");
		}

		return syn;
	}

	// boolean expression
	private Node boolExp() throws ParserException {
		return boolExp_(orFactor());
	}

	private Node boolExp_(Node inh) throws ParserException {
		Node syn;

		switch (lookahead.type) {
			case OR:
				nextToken();
				Node child = boolExp_(orFactor());
				syn = new Node(Type.OR, inh, child);
				break;
			default:
				syn = inh;
		}

		return syn;
	}

	private Node orFactor() throws ParserException {
		return orFactor_(andFactor());
	}

	private Node orFactor_(Node inh) throws ParserException {
		Node syn;

		switch (lookahead.type) {
			case AND:
				nextToken();
				Node child = orFactor_(andFactor());
				syn = new Node(Type.AND, inh, child);
				break;
			default:
				syn = inh;
		}

		return syn;
	}

	private Node andFactor() throws ParserException {
		Node syn;

		switch (lookahead.type) {
			case NOT:
				nextToken();
				syn = new Node(Type.NOT, boolExp());
				break;
			default:
				Node child0 = intExp();
				Type op = compOp();
				Node child1 = intExp();
				syn = new Node(op, child0, child1);
		}

		return syn;
	}

	private Type compOp() throws ParserException {
		Type type = lookahead.type;
		switch (lookahead.type) {
			case EQ:
			case DIFF:
			case HT:
			case LT:
			case HET:
			case LET:
				nextToken();
				return type;
			default:
				throw new ParserException("operador esperado");
		}
	}

	// terminals

	private Node _id() throws ParserException {
		Node leaf;

		if (lookahead.type == Type.ID) {
			leaf = new Leaf(Type.ID, lookahead.value);
			nextToken();
		} else {
			throw new ParserException(String.format("'%s' esperado", "identificador"));
		}

		return leaf;
	}

	private void _sc() throws ParserException {
		if (lookahead.type == Type.SC)
			nextToken();
		else
			throw new ParserException(String.format("'%s' esperado", ";"));
	}

	private void _end() throws ParserException {
		if (lookahead.type == Type.END)
			nextToken();
		else
			throw new ParserException(String.format("'%s' esperado", "end"));
	}

	private void _attrOp() throws ParserException {
		if (lookahead.type == Type.ATTR_OP)
			nextToken();
		else
			throw new ParserException(String.format("'%s' esperado", "="));
	}

	private void _do() throws ParserException {
		if (lookahead.type == Type.DO)
			nextToken();
		else
			throw new ParserException(String.format("'%s' esperado", "do"));
	}

	private void _then() throws ParserException {
		if (lookahead.type == Type.THEN)
			nextToken();
		else
			throw new ParserException(String.format("'%s' esperado", "then"));
	}

	private void _rPar() throws ParserException {
		if (lookahead.type == Type.R_PAR)
			nextToken();
		else
			throw new ParserException(String.format("'%s' esperado", ")"));
	}

	private Node _int() throws ParserException {
		Node leaf;

		if (lookahead.type == Type.INT) {
			leaf = new Leaf(Type.INT, lookahead.value);
			nextToken();
		} else {
			throw new ParserException(String.format("'%s' esperado, encontrado '%s'", Type.INT, lookahead.type));
		}

		return leaf;
	}

	private void _period() throws ParserException {
		if (lookahead.type == Type.PERIOD)
			nextToken();
		else
			throw new ParserException(String.format("'%s' esperado", "."));
	}

}

