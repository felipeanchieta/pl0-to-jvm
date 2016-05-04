package pl0compiler.lexer;

import pl0compiler.common.Token;
import pl0compiler.common.Type;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

	static char elemento = ' ';
	static char proximo = ' ';

	public static String getValue(String s, int i) {
		int j = i;
		boolean especial = true;
		char read = ' ';
		while (j < s.length()) {
			read = s.charAt(j);
			if (Character.isLetterOrDigit(s.charAt(j))) {
				j++;
				especial = false;
			} else if (especial == false) {
				return s.substring(i, j);
			} else if (especial == true) {
				System.out.println("ERRO DETECTADO: " + read);
				j = s.length();
			}
		}
		return s.substring(i, j);
	}

	public static List<Token> lex(String input) {
		List<Token> result = new ArrayList<>();
		boolean reservado = false;

		int i = 0;
		while (i < input.length()) {
			elemento = input.charAt(i);

			if (i < (input.length() - 1)) {
				proximo = input.charAt(i + 1);
			} else {
				proximo = ' ';
			}

			switch (elemento) {
				case ' ':
					i++;
					break;
				case ';':
					result.add(new Token(Type.SC, String.valueOf(elemento)));
					i++;
					break;
				case ',':
					result.add(new Token(Type.COMMA, String.valueOf(elemento)));
					i++;
					break;
				case '(':
					result.add(new Token(Type.L_PAR, String.valueOf(elemento)));
					i++;
					break;
				case ')':
					result.add(new Token(Type.R_PAR, String.valueOf(elemento)));
					i++;
					break;
				case '+':
					result.add(new Token(Type.SUM_OP, String.valueOf(elemento)));
					i++;
					break;
				case '-':
					result.add(new Token(Type.SUB_OP, String.valueOf(elemento)));
					i++;
					break;
				case '*':
					result.add(new Token(Type.MULT_OP, String.valueOf(elemento)));
					i++;
					break;
				case '/':
					result.add(new Token(Type.DIV_OP, String.valueOf(elemento)));
					i++;
					break;
				case '.':
					result.add(new Token(Type.PERIOD, String.valueOf(elemento)));
					i++;
					break;
				case '=':
					if (proximo != '=') {
						result.add(new Token(Type.ATTR_OP, String.valueOf(elemento)));
						i++;
						break;
					} else {
						result.add(new Token(Type.EQ, "=="));
						i += 2;
						break;
					}
				case '<':
					if (proximo != '=') {
						result.add(new Token(Type.LT, String.valueOf(elemento)));
						i++;
						break;
					} else {
						result.add(new Token(Type.LET, "<="));
						i += 2;
						break;
					}
				case '>':
					if (proximo != '=') {
						result.add(new Token(Type.HT, String.valueOf(elemento)));
						i++;
						break;
					} else {
						result.add(new Token(Type.HET, ">="));
						i += 2;
						break;
					}
				case '!':
					if (proximo == '=') {
						result.add(new Token(Type.DIFF, "!="));
						i += 2;
						break;
					} else {
						result.add(new Token(Type.NOT, "!="));
						i++;
						break;
					}
				case '|':
					if (proximo == '|') {
						result.add(new Token(Type.OR, "||"));
						i += 2;
						break;
					} else {
						System.out.println("ERRO in |!");
						i++;
						break;
					}
				case '&':
					if (proximo == '&') {
						result.add(new Token(Type.AND, "&&"));
						i += 2;
						break;
					} else {
						System.out.println("ERRO in &!");
						i++;
						break;
					}
				default:
					String tok = getValue(input, i);
					i += tok.length();

					switch (tok) {
						case "program":
							result.add(new Token(Type.PROGRAM, tok.toUpperCase()));
							reservado = true;
							break;
						case "begin":
							result.add(new Token(Type.BEGIN, tok.toUpperCase()));
							reservado = true;
							break;
						case "end":
							result.add(new Token(Type.END, tok.toUpperCase()));
							reservado = true;
							break;
						case "procedure":
							result.add(new Token(Type.PROCEDURE, tok.toUpperCase()));
							reservado = true;
							break;
						case "var":
							result.add(new Token(Type.VAR, tok.toUpperCase()));
							reservado = true;
							break;
						case "call":
							result.add(new Token(Type.CALL, tok.toUpperCase()));
							reservado = true;
							break;
						case "print":
							result.add(new Token(Type.PRINT, tok.toUpperCase()));
							reservado = true;
							break;
						case "while":
							result.add(new Token(Type.WHILE, tok.toUpperCase()));
							reservado = true;
							break;
						case "if":
							result.add(new Token(Type.IF, tok.toUpperCase()));
							reservado = true;
							break;
						case "else":
							result.add(new Token(Type.ELSE, tok.toUpperCase()));
							reservado = true;
							break;
						case "then":
							result.add(new Token(Type.THEN, tok.toUpperCase()));
							reservado = true;
							break;
						case "do":
							result.add(new Token(Type.DO, tok.toUpperCase()));
							reservado = true;
							break;
					}
					if (reservado == false) {
						try {
							int tokValue = Integer.parseInt(tok);
							result.add(new Token(Type.INT, tok, String.valueOf(tokValue)));
						} catch (NumberFormatException nfe) {
							result.add(new Token(Type.ID, tok, tok));
						}
					}

					reservado = false;
			}
		}
		return result;
	}
}
