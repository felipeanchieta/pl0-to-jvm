package pl0compiler.translator;

import pl0compiler.common.Leaf;
import pl0compiler.common.Node;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

// Possui o método translate, responsável por traduzir a saída do Parser
public class Translator {
	// variável temporária utilizada para armazenar instruções de escopo
	Stack<List<Instruction>> tempInstructions;
	List<Instruction> instructions;
	int addr;

	Map<String, Integer> variables;
	int variablePos;

	public Translator() {
		instructions = new ArrayList<Instruction>();
		variables = new HashMap<String, Integer>();
		tempInstructions = new Stack<List<Instruction>>();
	}

	// traduz a pilha de Nodes recebida
	public List<String> translate(Stack<Node> stack) {
		dequeue(stack);

		// adiciona última instrução de finalizar o programa
		instructions.add(new Instruction("return"));

		List<String> r = new ArrayList<String>();
		int pos = 0;
		for (Instruction instruction : instructions) {
			r.add(String.format("\t%d: %s\t\t%s", pos, instruction.cmd, instruction.arg));
			pos += instruction.jump;
		}

		return r;
	}

	// desempinha
	void dequeue(Stack<Node> stack) {
		while (!stack.empty()) {
			Node node = stack.pop();
			postOrder(node);
		}
	}

	// varredura pós-ordem, de baixo para cima, da esquerda para direita
	void postOrder(Node node) {
		// operador null indica que o nó contém uma pilha
		if (node.operator == null) {
			dequeue(node.stack);
		} else {
			switch (node.operator) {
				case IF:
					tempInstructions.push(new ArrayList<Instruction>());
					List<Instruction> temp = tempInstructions.peek();
					int tempAddr = addr;

					// código if
					postOrder(node.operands[0]);

					// calcula a posição da label após o conjunto de condições
					int condOffset = 0;
					for (Instruction instruction : temp) {
						condOffset += instruction.jump;
					}

					tempAddr += condOffset;

					// código bloco
					postOrder(node.operands[1]);

					// calcula a posição da label após a geração do bloco
					int blockOffset = 0;
					for (Instruction instruction : temp) {
						blockOffset += instruction.jump;
					}

					blockOffset -= condOffset;

					// obtém a lista de instruções que gerou esse trecho
					if (!tempInstructions.empty())
						tempInstructions.pop();

					List<Instruction> parent;
					if (tempInstructions.empty())
						parent = instructions;
					else
						parent = tempInstructions.peek();

					// atualiza as instruções
					int pos = tempAddr - condOffset;
					for (Instruction instruction : temp) {
						switch (instruction.arg) {
							case "true":
								instruction.arg = Integer.toString(tempAddr);
								break;
							case "false":
								instruction.arg = Integer.toString(tempAddr + blockOffset);
								break;
							case "jumpgoto":
								instruction.arg = Integer.toString(pos + 6);
								break;
						}
						parent.add(instruction);
						pos += instruction.jump;
					}
					break;
				case WHILE:
					// lógica similar ao if
					tempInstructions.push(new ArrayList<Instruction>());
					temp = tempInstructions.peek();
					tempAddr = addr;

					// condição
					postOrder(node.operands[0]);

					condOffset = 0;
					for (Instruction instruction : temp) {
						condOffset += instruction.jump;
					}

					tempAddr += condOffset;
					addr = tempAddr;

					// código bloco
					postOrder(node.operands[1]);

					// adiciona goto ao final do bloco para retorna ao início das condições
					gen("goto", Integer.toString(tempAddr - condOffset), 3);

					blockOffset = 0;
					for (Instruction instruction : temp) {
						blockOffset += instruction.jump;
					}

					blockOffset -= condOffset;

					// atualiza os endereços
					if (!tempInstructions.empty())
						tempInstructions.pop();

					if (tempInstructions.empty())
						parent = instructions;
					else
						parent = tempInstructions.peek();

					pos = tempAddr - condOffset;
					for (Instruction instruction : temp) {
						switch (instruction.arg) {
							case "true":
								instruction.arg = Integer.toString(tempAddr);
								break;
							case "false":
								instruction.arg = Integer.toString(tempAddr + blockOffset);
								break;
							case "jumpgoto":
								instruction.arg = Integer.toString(pos + 6);
								break;
						}
						parent.add(instruction);
						pos += instruction.jump;
					}
					addr = pos;

					break;
				case PROCEDURE:
					postOrder(node.operands[1]);
					break;
				case ATTR_OP:
					postOrder(node.operands[1]);
					istore(((Leaf) node.operands[0]).lexval);
					break;
				case SUB_OP:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("isub");
					break;
				case SUM_OP:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("iadd");
					break;
				case DIV_OP:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("idiv");
					break;
				case MULT_OP:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("imul");
					break;
				case OR:
					temp = tempInstructions.peek();
					postOrder(node.operands[0]);
					temp.remove(temp.size() - 1);
					addr -= 3;
					temp.get(temp.size() - 1).arg = "true";
					postOrder(node.operands[1]);
					if (temp.get(temp.size() - 1).cmd != "goto") {
						temp.get(temp.size() - 1).arg = "true";
						gen("goto", "false", 3);
					}
					break;
				case AND:
					temp = tempInstructions.peek();
					postOrder(node.operands[0]);
					// flag para pular o goto é criado logo abaixo
					temp.get(temp.size() - 2).arg = "jumpgoto";
					temp.get(temp.size() - 1).arg = "false";

					postOrder(node.operands[1]);

					if (temp.get(temp.size() - 1).cmd != "goto") {
						temp.get(temp.size() - 1).arg = "true";
						gen("goto", "false", 3);
					}
					break;
				case EQ:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("if_acmpeq", "true", 3);
					gen("goto", "false", 3);
					break;
				case DIFF:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("if_acmpne", "true", 3);
					gen("goto", "false", 3);
					break;
				case HT:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("if_icmgt", "true", 3);
					gen("goto", "false", 3);
					break;
				case LT:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("if_icmlt", "true", 3);
					gen("goto", "false", 3);
					break;
				case HET:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("if_icmge", "true", 3);
					gen("goto", "false", 3);
					break;
				case LET:
					postOrder(node.operands[0]);
					postOrder(node.operands[1]);
					gen("if_icmle", "true", 3);
					gen("goto", "false", 3);
					break;
				case ID:
					iload(((Leaf) node).lexval);
					break;
				case INT:
					bipush(((Leaf) node).lexval);
					break;
			}
		}
	}

	void iload(String variable) {
		if (!variables.containsKey(variable)) {
			variables.put(variable, variablePos);
			variablePos++;
		}

		String id = Integer.toString(variables.get(variable));

		gen("iload", id, 2);
	}

	void istore(String variable) {
		if (!variables.containsKey(variable)) {
			variables.put(variable, variablePos);
			variablePos++;
		}

		String id = Integer.toString(variables.get(variable));

		gen("istore", id, 2);
	}

	void bipush(String value) {
		gen("bipush", value, 2);
	}

	void gen(String cmd, String arg, int jump) {
		if (tempInstructions.empty()) {
			instructions.add(new Instruction(cmd, arg, jump));
		} else {
			tempInstructions.peek().add(new Instruction(cmd, arg, jump));
		}
		addr += jump;
	}

	void gen(String cmd) {
		gen(cmd, "", 1);
	}
}
