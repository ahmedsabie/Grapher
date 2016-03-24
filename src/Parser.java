import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public class Parser {
	private static final Exception InvalidFunction = new Exception("Invalid function!");
	public static final double SENTINEL = Double.POSITIVE_INFINITY;
	String function;
	double yMin, yMax;
	ArrayList<Double> xValues, yValues;
	Parser(String function, double yMin, double yMax, ArrayList<Double> xValues) throws Exception {
		this.function = function;
		this.yMin = yMin;
		this.yMax = yMax;
		this.xValues = xValues;
		this.yValues = evaluateFunction(function);
		/*for (int i = 0 ; i < yValues.size() ; i++) {
			System.out.print(yValues.get(i) + " ");
		}
		System.out.println();*/
	}
	public ArrayList<Double> evaluateFunction(String function) throws Exception {
		if (function.isEmpty()) {
			throw InvalidFunction;
		}
		if (function.charAt(0) == '(' && function.charAt(function.length() - 1) == ')') {
			return evaluateFunction(function.substring(1, function.length() - 1));
		}
		int stackDepth = 0;
		ArrayList<Double> result = new ArrayList<Double>();
		if (isBinaryFunction(function)) {
			evaluateBinaryFunction(function, result);
		} else if (isConstantFunction(function)) {
			evaluateConstantFunction(function, result);
		} else if (isIdentityFunction(function)) {
			evaluateIdentityFunction(function, result);
		} else if (isAbsFunction(function)) {
			evaluateAbsFunction(function, result);
		} else if (isArcCosFunction(function)) {
			evaluateArcCosFunction(function, result);
		} else if (isArcSinFunction(function)) {
			evaluateArcSinFunction(function, result);
		} else if (isArcTanFunction(function)) {
			evaluateArcTanFunction(function, result);
		} else if (isCeilFunction(function)) {
			evaluateCeilFunction(function, result);
		} else if (isFloorFunction(function)) {
			evaluateFloorFunction(function, result);
		} else if (isCosFunction(function)) {
			evaluateCosFunction(function, result);
		} else if (isSinFunction(function)) {	
			evaluateSinFunction(function, result);
		} else if (isTanFunction(function)) {
			evaluateTanFunction(function, result);
		} else if (isLogFunction(function)) {
			evaluateLogFunction(function, result);
		} else if (isLnFunction(function)) {
			evaluateLnFunction(function, result);
		} else if (isSqrtFunction(function)) {
			evaluateSqrtFunction(function, result);
		} else if (isNegateFunction(function)) {
			evaluateNegateFunction(function, result);
		} else {
			throw InvalidFunction;
		}
		return result;
		
	}
	public void evaluateBinaryFunction(String function, ArrayList<Double> result) throws Exception {
		int stackDepth = 0;
		HashMap<Character, Integer> binaryOperators = new HashMap<Character, Integer>();
		for (int i = 0 ; i < function.length() ; i++) {
			char ch = function.charAt(i);
			if (ch == '(') {
				stackDepth++;
			} else if (ch == ')') {
				stackDepth--;
			} else if (stackDepth == 0 && isBinaryOperator(ch)) {
				binaryOperators.put(ch, i);
			}
		}
		if (!binaryOperators.isEmpty()) {
			Character doOperator = null;
			Integer splitIndex = null;
			for (Map.Entry<Character, Integer> pair : binaryOperators.entrySet()) {
				Character operator = pair.getKey();
				Integer index = pair.getValue();
				if (doOperator == null || 
					isLowerPriority(operator, doOperator) ||
					(isSamePriority(operator, doOperator) && index > splitIndex)) {
					doOperator = operator;
					splitIndex = index;
				} 
			}
			if (splitIndex == 0 && doOperator == '-') {
				evaluateNegateFunction(function, result);
			} else {
				ArrayList<Double> splitLeft = evaluateFunction(function.substring(0, splitIndex));
				ArrayList<Double> splitRight = evaluateFunction(function.substring(splitIndex + 1, function.length()));
				for (int i = 0 ; i < xValues.size() ; i++) {
					double leftY = splitLeft.get(i);
					double rightY = splitRight.get(i);
					if (leftY == SENTINEL || rightY == SENTINEL) {
						result.add(SENTINEL);
					} else {
						result.add(evaluateBinaryOperation(doOperator, leftY, rightY));
					}
				}
			}
		} 
	}
	public void evaluateConstantFunction(String function, ArrayList<Double> result) {
		double value = Double.valueOf(function);
		for (int i = 0 ; i < xValues.size() ; i++) {
			result.add(value);
		}
	}
	public void evaluateIdentityFunction(String function, ArrayList<Double> result) {
		for (int i = 0 ; i < xValues.size() ; i++) {
			result.add(xValues.get(i));
		}
	}
	public void evaluateAbsFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(4, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL) {
				result.add(SENTINEL);
			} else {
				result.add(Math.abs(innerFunction.get(i)));
			}
		}
	}
	public void evaluateArcCosFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(5, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL || 
				innerFunction.get(i) < -1 || 
				innerFunction.get(i) > 1) {
				result.add(SENTINEL);
			} else {
				result.add(Math.acos(innerFunction.get(i)));
			}
		}
	}
	public void evaluateArcSinFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(5, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL || 
					innerFunction.get(i) < -1 || 
					innerFunction.get(i) > 1) {
					result.add(SENTINEL);
				} else {
					result.add(Math.asin(innerFunction.get(i)));
				}
		}
	}
	public void evaluateArcTanFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(5, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL) {
					result.add(SENTINEL);
				} else {
					result.add(Math.atan(innerFunction.get(i)));
				}
		}
	}
	public void evaluateCeilFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(5, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL) {
				result.add(SENTINEL);
			} else {
				result.add(Math.ceil(innerFunction.get(i)));
			}
		}
	}
	public void evaluateFloorFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(6, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL) {
				result.add(SENTINEL);
			} else {
				result.add(Math.floor(innerFunction.get(i)));
			}
		}
	}
	public void evaluateCosFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(4, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL) {
				result.add(SENTINEL);
			} else {
				result.add(Math.cos(innerFunction.get(i)));
			}
		}
	}
	public void evaluateSinFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(4, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL) {
				result.add(SENTINEL);
			} else {
				result.add(Math.sin(innerFunction.get(i)));
			}
		}
	}
	public void evaluateTanFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(4, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL) {
				result.add(SENTINEL);
			} else {
				result.add(Math.tan(innerFunction.get(i)));
			}
		}
	}
	public void evaluateLogFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(4, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL ||
				innerFunction.get(i) <= 0) {
				result.add(SENTINEL);
			} else {
				result.add(Math.log10(innerFunction.get(i)));
			}
		}
	}
	public void evaluateLnFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(3, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL ||
					innerFunction.get(i) <= 0) {
					result.add(SENTINEL);
				} else {
					result.add(Math.log(innerFunction.get(i)));
				}
		}
	}
	public void evaluateSqrtFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(5, function.length() - 1));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL ||
					innerFunction.get(i) < 0) {
					result.add(SENTINEL);
				} else {
					result.add(Math.sqrt(innerFunction.get(i)));
				}
		}
	}
	public void evaluateNegateFunction(String function, ArrayList<Double> result) throws Exception {
		ArrayList<Double> innerFunction = evaluateFunction(function.substring(1, function.length()));
		for (int i = 0 ; i < xValues.size() ; i++) {
			if (innerFunction.get(i) == SENTINEL) {
				result.add(SENTINEL);
			} else {
				result.add(-innerFunction.get(i));
			}
		}
	}
	public double evaluateBinaryOperation(char operator, double first, double second) {
		double result = -1;
		switch(operator) {
			case '+':
				result = first + second;
				break;
			case '-':
				result = first - second;
				break;
			case '*':
				result = first * second;
				break;
			case '/':
				result = first / second;
				break;
			case '^':
				result = Math.pow(first, second);
				break;
		}
		return result;
	}
	public boolean isBinaryFunction(String function) {
		int stackDepth = 0;
		for (int i = 0 ; i < function.length() ; i++) {
			char ch = function.charAt(i);
			if (ch == '(') {
				stackDepth++;
			} else if (ch == ')') {
				stackDepth--;
			} else if (stackDepth == 0 && isBinaryOperator(ch)) {
				return true;
			}
		}
		return false;
	}
	public boolean isConstantFunction(String function) {
		boolean constant = true;
		for (int i = 0 ; i < function.length() ; i++) {
			if (!isDigit(function.charAt(i)) && function.charAt(i) != '.') {
				constant = false;
			}
		}
		return constant;
	}
	public boolean isIdentityFunction(String function) {
		return function.equals("x");
	}
	public boolean isAbsFunction(String function) {
		return function.startsWith("abs(") && function.endsWith(")");
	}
	public boolean isArcCosFunction(String function) {
		return function.startsWith("acos(") && function.endsWith(")");
	}
	public boolean isArcSinFunction(String function) {
		return function.startsWith("asin(") && function.endsWith(")");
	}
	public boolean isArcTanFunction(String function) {
		return function.startsWith("atan(") && function.endsWith(")");
	}
	public boolean isCeilFunction(String function) {
		return function.startsWith("ceil(") && function.endsWith(")");
	}
	public boolean isFloorFunction(String function) {
		return function.startsWith("floor(") && function.endsWith(")");
	}
	public boolean isCosFunction(String function) {
		return function.startsWith("cos(") && function.endsWith(")");
	}
	public boolean isSinFunction(String function) {
		return function.startsWith("sin(") && function.endsWith(")");
	}
	public boolean isTanFunction(String function) {
		return function.startsWith("tan(") && function.endsWith(")");
	}
	public boolean isLogFunction(String function) {
		return function.startsWith("log(") && function.endsWith(")");
	}
	public boolean isLnFunction(String function) {
		return function.startsWith("ln(") && function.endsWith(")");
	}
	public boolean isSqrtFunction(String function) {
		return function.startsWith("sqrt(") && function.endsWith(")");
	}
	public boolean isNegateFunction(String function) {
		return function.startsWith("-");
	}
	public boolean isBinaryOperator(char ch) {
		return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
	}
	public boolean isLowerPriority(Character a, Character b) {
		if (a == '+' || a == '-') {
			if (b == '/' || b == '*' || b == '^')return true;
			return false;
		} else if (a == '*' || a == '/') {
			if (b == '^')return true;
			return false;
		}
		return false;
	}
	public boolean isSamePriority(Character a, Character b) {
		return (a == '+' || a == '-') && (b == '+' || b == '-') ||
				(a == '*' || a == '/' && (b == '*' || b == '/')) ||
				(a == '^') && (b == '^');
	}
	public boolean isDigit(char ch) {
		return '0' <= ch && ch <= '9';
	}
	public ArrayList<Double> getYValues(){
		return yValues;
	}
}
