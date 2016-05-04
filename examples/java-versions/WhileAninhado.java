public class WhileAninhado {
	public static void main(String[] args) {
		int a = 1, b = 2, c = 3;

		while (a + b < c) {
            while (a + c == b + 2) {
                a = a + 1;
            }   
        }
	}	
}