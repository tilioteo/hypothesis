/**
 * 
 */
package org.hypothesis.common.expression;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hypothesis.common.ReadOnlyHashSet;
import org.hypothesis.common.constants.StringConstants;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
enum Operator {
	
	DECLASS(StringConstants.STR_OP_DECLASS, 1),
	NOT(StringConstants.STR_OP_NOT, 2),
	MINUS(StringConstants.STR_OP_MINUS, 4), // NOTE if unary then ignore this priority
	PLUS(StringConstants.STR_OP_PLUS, 5), // NOTE if unary then ignore this priority
	MULTIPLY(StringConstants.STR_OP_MULTIPLY, 3),
	DIVIDE(StringConstants.STR_OP_DIVIDE, 3),
	EQUALS(StringConstants.STR_OP_EQUALS, 6),
	NOT_EQUALS(StringConstants.STR_OP_NOT_EQUAL, 6),
	GREATER(StringConstants.STR_OP_GREATER, 5),
	LESS(StringConstants.STR_OP_LESS, 5),
	GREATER_OR_EQUAL(StringConstants.STR_OP_GREATER_OR_EQUAL, 5),
	LESS_OR_EQUAL(StringConstants.STR_OP_LESS_OR_EQUAL, 5),
	AND(StringConstants.STR_OP_AND, 7),
	OR(StringConstants.STR_OP_OR, 8),
	XOR(StringConstants.STR_OP_XOR, 8),
	ASSIGN(StringConstants.STR_OP_ASSIGN, 9);
	
	private static final Map<String, Operator> lookup = new HashMap<String, Operator>();
	
	static {
		for(Operator op : EnumSet.allOf(Operator.class)) {
			lookup.put(op.getString(), op);
		}
	}
	
	private String string;
	private int priority;
	
	private Operator(String string, int priority) {
		this.string = string;
		this.priority = priority;
	}
	
	public String getString() {
		return string;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public boolean isUnary() {
		return UNARY.contains(this);
	}
	
	public boolean isBinary() {
		return BINARY.contains(this);
	}
	
	/*public boolean isArithmetic() {
		return ARITHMETIC.contains(this);
	}
	
	public boolean isComparison() {
		return COMPARISON.contains(this);
	}
	
	public boolean isLogical() {
		return LOGICAL.contains(this);
	}*/

	public static Operator get(String string) {
		return lookup.get(string);
	}
	
	public static final Set<Operator> UNARY = new ReadOnlyHashSet<Operator>(
			NOT, MINUS, PLUS
	);
	
	public static final Set<Operator> BINARY = new ReadOnlyHashSet<Operator>(
			ASSIGN, MINUS, PLUS, MULTIPLY, DIVIDE, EQUALS, NOT_EQUALS, GREATER, LESS,
			GREATER_OR_EQUAL, LESS_OR_EQUAL, AND, OR, XOR, DECLASS
	);
	
	/*public static final Set<Operator> ARITHMETIC = new ReadOnlyHashSet<Operator>(
			MINUS, PLUS, MULTIPLY, DIVIDE
	);
	
	public static final Set<Operator> COMPARISON = new ReadOnlyHashSet<Operator>(
			EQUALS, NOT_EQUALS, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL
	);
	
	public static final Set<Operator> LOGICAL = new ReadOnlyHashSet<Operator>(
			AND, OR, XOR
	);*/
	
	public static final Set<Character> CHARS = new ReadOnlyHashSet<Character>(
			StringConstants.CHR_NOT,
			StringConstants.CHR_MINUS,
			StringConstants.CHR_PLUS,
			StringConstants.CHR_MULTIPLY,
			StringConstants.CHR_DIVIDE,
			StringConstants.CHR_EQUALS,
			StringConstants.CHR_GREATER,
			StringConstants.CHR_LESS,
			StringConstants.CHR_AND,
			StringConstants.CHR_OR,
			StringConstants.CHR_TILDA);
}
