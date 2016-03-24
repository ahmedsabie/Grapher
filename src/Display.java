import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Display extends JFrame implements ActionListener {
	int functionCount;
	
	ArrayList<JPanel> rows;
	ArrayList<JTextField> inputs;
	ArrayList<JLabel> labels;
	ArrayList<JButton> buttons;
	ArrayList<JComboBox> dropdowns;
	
	JFrame frame;
	JPanel fullPanel;
	JScrollPane scrollable;
	
	String[] buttonTexts;
	ArrayList<String> labelTexts;
	Dimension screenDimensions, fieldDimensions, largerFieldDimensions, buttonDimensions, colorButtonDimensions;
	GridLayout grid, oneBlock;
	FlowLayout flow;
	
	Font font, largerFont;
	
	Display() {
		super("Grapher");
		
		functionCount = 1;
		
		rows = new ArrayList<JPanel>();
		inputs = new ArrayList<JTextField>();
		labels = new ArrayList<JLabel>();
		buttons = new ArrayList<JButton>();
		dropdowns = new ArrayList<JComboBox>();
		fullPanel = new JPanel();
		scrollable = new JScrollPane(fullPanel);
		frame = new JFrame();

		
		buttonTexts = new String[] {
				"Graph!", "Add function", "Remove function"
		};
		labelTexts = new ArrayList<String>();
		labelTexts.add("<html> f<sub>"+1+"</sub>(x)= </html>");
		labelTexts.add("Domain:");
		labelTexts.add("<= X <=");
		labelTexts.add("Range:");
		labelTexts.add("<= Y <=");
		
		screenDimensions = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		fieldDimensions = new Dimension(80, 80);
		largerFieldDimensions = new Dimension(400,80);
		buttonDimensions = new Dimension(350, 100);
		colorButtonDimensions = new Dimension(20, 20);
		grid = new GridLayout(4, 3);
		oneBlock = new GridLayout(1, 1);
		font = new Font("Times new Roman", Font.BOLD, 20);
		largerFont = new Font("Georgia", Font.ITALIC, 25);
		flow = new FlowLayout(FlowLayout.CENTER);
		setLayout();
		
	}
	public void setLayout() {
		frame.setSize(screenDimensions);
		//frame.setResizable(false);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setLayout(oneBlock);
		fullPanel.setLayout(grid);

		for (int i = 0 ; i < 4 ; i++) {
			rows.add(new JPanel());
			rows.get(i).setLayout(flow);
		}
		
		for (int i = 0 ; i < 3 ; i++) {
			JButton button = new JButton();
			
			button.setText(buttonTexts[i]);
			button.setFont(font);
			button.addActionListener(this);
			button.setPreferredSize(buttonDimensions);
			
			buttons.add(button);
			
		}
		
		for (int i = 0 ; i < 5 ; i++) {
			JTextField field = new JTextField();
			field.setFont(font);
			field.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			if (i == 0 ) {
				field.setPreferredSize(largerFieldDimensions);
			} else {
				field.setPreferredSize(fieldDimensions);
			}
			inputs.add(field);
		}
			
		for (int i = 0 ; i < 5 ; i++) {
			JLabel label = new JLabel(labelTexts.get(i), SwingConstants.CENTER);
			
			label.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			label.setPreferredSize(fieldDimensions);
			if (i == 0) {
				label.setFont(largerFont);
			} else {
				label.setFont(font);
			}
			
			labels.add(label);
		}
		
		rows.get(0).add(labels.get(0));
		rows.get(0).add(inputs.get(0));
		addDropDownList(rows.get(0), 1);
		addColorButton(rows.get(0));
		
		rows.get(1).add(labels.get(1));
		rows.get(1).add(inputs.get(1));
		rows.get(1).add(labels.get(2));
		rows.get(1).add(inputs.get(2));
		
		rows.get(2).add(labels.get(3));
		rows.get(2).add(inputs.get(3));
		rows.get(2).add(labels.get(4));
		rows.get(2).add(inputs.get(4));
		
		for (int i = 0 ; i < 3 ; i++) {
			rows.get(3).add(buttons.get(i));
		}
		for (int i = 0 ; i < rows.size() ; i++) {
			fullPanel.add(rows.get(i));
		}
		
		frame.add(scrollable);
		frame.setVisible(true);
	
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//graph button
		if (arg0.getSource().equals(buttons.get(0))) {
			try {
				ArrayList<String> functions = new ArrayList<String>();
				ArrayList<Color> colors = new ArrayList<Color>();
				ArrayList<Integer> derivatives = new ArrayList<Integer>();
				for (int i = 0 ; i < inputs.size() - 4 ; i++) {
					functions.add(inputs.get(i).getText());
					colors.add(buttons.get(i + 3).getBackground());
					derivatives.add(dropdowns.get(i).getSelectedIndex());
				}
				double xMin = Double.parseDouble(inputs.get(inputs.size() - 4).getText());
				double xMax = Double.parseDouble(inputs.get(inputs.size() - 3).getText());
				double yMin = Double.parseDouble(inputs.get(inputs.size() - 2).getText());
				double yMax = Double.parseDouble(inputs.get(inputs.size() - 1).getText());
				new Grapher(functions, colors, derivatives, xMin, xMax, yMin, yMax);
			}
			catch (NumberFormatException e) {
				System.out.println("Enter real numbers for domain and range bounds!");
			}
			catch (ArithmeticException e){
				System.out.println(e.getMessage());
			}
			catch (Exception e) {
				System.out.println("ERROR!");
				System.out.println(e.getMessage());
			}
		} else if (arg0.getSource().equals(buttons.get(1))) {
			
			functionCount++;
			
			for (int i = 0 ; i < rows.size() ; i++) {
				fullPanel.remove(rows.get(i));
			}
			
			JTextField input = new JTextField();
			input.setFont(font);
			input.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			input.setPreferredSize(largerFieldDimensions);
			
			inputs.add(inputs.size() - 4, input);
			
			JLabel label = new JLabel("<html> f<sub>"+functionCount+"</sub>(x)= </html>", 
					SwingConstants.CENTER);
			label.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			label.setPreferredSize(fieldDimensions);
			label.setFont(largerFont);
			labels.add(labels.size() - 4, label);
			
			JPanel row = new JPanel();
			row.add(label);
			row.add(input);
			addDropDownList(row, functionCount);
			addColorButton(row);
			
			rows.add(rows.size() - 3, row);
			
			grid = new GridLayout(grid.getRows() + 1, 3);
			fullPanel.setLayout(grid);
			
			for (int i = 0 ; i < rows.size(); i++) {
				fullPanel.add(rows.get(i));
			}
			
			frame.revalidate();
			frame.repaint();
		} else if (arg0.getSource().equals(buttons.get(2)) && functionCount > 1) {
			
			functionCount--;
			
			for (int i = 0 ; i < rows.size() ; i++) {
				fullPanel.remove(rows.get(i));
			}
			
			inputs.remove(inputs.size() - 5);
			labels.remove(labels.size() - 5);
			dropdowns.remove(dropdowns.size() - 1);
			for (int i=0;i<labels.size();i++)System.out.println(labels.get(i).getText());
			rows.remove(rows.size() - 4);
			
			grid = new GridLayout(grid.getRows() - 1, 3);
			fullPanel.setLayout(grid);
			
			for (int i = 0 ; i < rows.size(); i++) {
				fullPanel.add(rows.get(i));
			}
			
			frame.revalidate();
			frame.repaint();
		}
	}
	public void addColorButton(JPanel panel) {
		JButton button = new JButton();
		button.setPreferredSize(colorButtonDimensions);
		button.setBackground(Color.BLACK);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame colorframe = new JFrame();
				Color c = JColorChooser.showDialog(colorframe, "Choose a Color", Color.BLACK);
				button.setBackground(c);
			}
			
		});
		
		buttons.add(button);
		panel.add(button);
	}
	public void addDropDownList(JPanel panel, int subScript) {
		JComboBox dropdown = new JComboBox();
		for (int i = 0 ; i < 5 ; i++) {
			String html = "<html>Graph: f<sub>"+subScript+"</sub>";//
			for (int j = 0 ; j < i ; j++) {
				html += "<sup>'</sup>";
			}
			html += "(x)</html>";
			JLabel label  = new JLabel(html);
			dropdown.addItem(html);
		}
		panel.add(dropdown);
		dropdowns.add(dropdown);
		
	}
}