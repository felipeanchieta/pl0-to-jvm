
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Compiler {

    public static void main(String[] args) throws ParserException {
        String linha;
        List<Token> tokensAux;
        List<Token> tokensFinal = new ArrayList<>();

        try {
            BufferedReader alg = new BufferedReader(new FileReader(""));

            while (alg.ready()) {
                linha = alg.readLine();
                tokensAux = Lexer.lex(linha);
                for (Token t : tokensAux) {
                    tokensFinal.add(t);
                }
            }

            tokensFinal.add(new Token(Token.Type.EOF, "$"));

            alg.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Parser parser = new Parser(tokensFinal);
        parser.parse();
        
    }
}
