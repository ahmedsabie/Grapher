import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Grapher extends JFrame {
	private final Exception MatchingException = new Exception("Brackets not matched");
	private final Exception IntervalException = new Exception("Intervals must satsify left < right");
	//Larger divisions generate more accurate graphs,but slow down the program
	private final int DIVISIONS = 100000;
	private final double EPS = 0.0000001;
	private final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private final double HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	double xMin, xMax, yMin, yMax;
	boolean isValid = false;
	ArrayList<String> functions;
	ArrayList<Color> colors;
	ArrayList<Integer> derivatives;
	ArrayList<ArrayList<Double>> xValues = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> yValues;
	Grapher(ArrayList<String> functions, ArrayList<Color> colors, ArrayList<Integer> derivatives, double xMin, double xMax, double yMin, double yMax) throws Exception {
		super("Hi");
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.functions = functions;
		this.colors = colors;
		this.derivatives = derivatives;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		
		process();
		generateXValues();
		generateYValues();
		findDerivatives();
		setVisible(true);
	}
	public boolean isBracketMatched(String function) {
		Stack<Character> brackets = new Stack<Character>();
		for (int i = 0 ; i < function.length() ; i++) {
			char ch = function.charAt(i);
			if (ch == '(') {
				brackets.push(ch);
			} else if (ch == ')') {
				if (brackets.isEmpty()) {
					return false;
				}
				char match = brackets.pop();
				if (match != '(') {
					return false;
				}
			}
		}
		return brackets.isEmpty();
	}
	
	public boolean isValidInterval(double a, double b) {
		return a < b;
	}
	
	public void generateXValues() {
		double gap = (xMax - xMin) / DIVISIONS;
		ArrayList<Double> xs = new ArrayList<Double>();
		for (double x = xMin ; x <= xMax + EPS ; x += gap) {
			xs.add(x);
		}
		for (int i = 0 ; i < functions.size() ; i++) {
			xValues.add(xs);
		}
	}
	public void generateYValues() throws Exception {
		yValues = new ArrayList< ArrayList<Double> >();
		for (int i = 0 ; i < functions.size() ; i++) {
			Parser parser = new Parser(functions.get(i), yMin, yMax, xValues.get(i));
			yValues.add(parser.getYValues());
		}
		isValid = true;
	}
	public void findDerivatives() {
		int numDerivatives;
		for (int i = 0 ; i < functions.size() ; i++) {
			numDerivatives = derivatives.get(i);
			ArrayList<Double> xs = xValues.get(i);
			ArrayList<Double> ys = yValues.get(i);
			for (int j = 0 ; j < numDerivatives ; j++) {
				takeDerivative(xs, ys);
			}
		}
	}
	public void drawAxes(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		if (xMin <= 0 && 0 <= xMax) {
			double distLeft = -xMin / (xMax - xMin) * WIDTH;
			Line2D.Double yAxis = new Line2D.Double(distLeft, 0, distLeft, HEIGHT);
			g2D.draw((Shape) yAxis);
		}
		if (yMin <= 0 && 0 <= yMax) {
			double distTop = -yMin / (yMax - yMin) * HEIGHT;
			distTop = HEIGHT - distTop;
			Line2D.Double xAxis = new Line2D.Double(0, distTop, WIDTH, distTop);
			g2D.draw((Shape) xAxis);
		}
	}
	public void drawPoints(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		double distLeft, distTop, x, y;
		for (int i = 0 ; i < functions.size() ; i++) {
			g2D.setColor(colors.get(i));
			ArrayList<Double> xValuesHere = xValues.get(i);
			ArrayList<Double> yValuesHere = yValues.get(i);
			for (int j = 0 ; j < xValuesHere.size() ; j++) {
				x = xValuesHere.get(j);
				y = yValuesHere.get(j);
				if (yMin <= y && y <= yMax && y != Parser.SENTINEL) {
					distLeft = (x - xMin) / (xMax - xMin) * WIDTH;
					distTop = (y - yMin) / (yMax - yMin) * HEIGHT;
					distTop = HEIGHT - distTop;
					Line2D.Double point = new Line2D.Double(distLeft, distTop, distLeft, distTop);
					g2D.draw((Shape) point);
				}
			}
		}
	}
	public void paint(Graphics g) {
		if (isValid) {
	        super.paint(g);
	        drawAxes(g);
	        drawPoints(g);
		}
    }
	public void process() throws Exception {
		for (int i = 0 ; i < functions.size() ; i++) {
			String function = functions.get(i);
			function = function.replace(" ", "");
			function = function.toLowerCase();
			functions.set(i, function);
			if (!isBracketMatched(function)) {
				throw MatchingException;
			}
			if (!isValidInterval(xMin, xMax) || !isValidInterval(yMin, yMax)) {
				throw IntervalException;
			}
		}
	}
	public void takeDerivative(ArrayList<Double> xs, ArrayList<Double> ys) {
		double x,y;
		ArrayList<Double> newXs = new ArrayList<Double>();
		ArrayList<Double> newYs = new ArrayList<Double>();
		for (int i = 0 ; i < xs.size() - 1 ; i++) {
			x = (xs.get(i) + xs.get(i+1)) / 2.0;
			if (ys.get(i+1).equals(Parser.SENTINEL) || ys.get(i).equals(Parser.SENTINEL)) {
				y = Parser.SENTINEL;
			} else {
				y = (ys.get(i+1) - ys.get(i)) / (xs.get(i+1) - xs.get(i));
			}
			newXs.add(x);
			newYs.add(y);
		}
		xs.clear();
		ys.clear();
		for (int i = 0 ; i < newXs.size() ; i++) {
			xs.add(newXs.get(i));
			ys.add(newYs.get(i));
		}
		System.out.println(xs.get(0) + " "+ys.get(0));
	}
}
