package pl0compiler.main;

import pl0compiler.common.Node;
import pl0compiler.common.Token;
import pl0compiler.common.Type;
import pl0compiler.lexer.Lexer;
import pl0compiler.parser.Parser;
import pl0compiler.translator.Translator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Compiler {

	public static void main(String[] args) {
		String linha;
		List<Token> tokensAux;
		List<Token> tokensFinal = new ArrayList<>();

		String filename = "ProgramaSimples.pl0";

		if (args.length > 0) {
			filename = args[0];
		}

		try {

			BufferedReader alg = new BufferedReader(new FileReader(filename));

			while (alg.ready()) {
				linha = alg.readLine();
				tokensAux = Lexer.lex(linha);

				for (Token t : tokensAux) {
					tokensFinal.add(t);
				}
			}

			tokensFinal.add(new Token(Type.EOF, "$"));

			alg.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		Parser parser = new Parser(tokensFinal);
		Stack<Node> stack = parser.parse();

		System.out.println();

		Translator translator = new Translator();
		List<String> instructions = translator.translate(stack);

		for (String instruction : instructions) {
			System.out.println(instruction);
		}
	}
}
