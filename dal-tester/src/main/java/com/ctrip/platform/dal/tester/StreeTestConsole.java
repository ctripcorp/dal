package com.ctrip.platform.dal.tester;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class StreeTestConsole {

	private JFrame frame;
	private JTextField threadCountTF;
	private JTextField logicDbTF;
	private JTextField portsTF;
	private JTextField sqlTF;
	private JTextField poolSizeTF;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StreeTestConsole window = new StreeTestConsole();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StreeTestConsole() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 850, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(10, 10));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(10, 10));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.WEST);
		panel_1.setLayout(new GridLayout(5, 1, 0, 5));
		
		JLabel lblNewLabel = new JLabel("Dal Client IPs");
		panel_1.add(lblNewLabel);
		
		JLabel lblMaxpoolsize = new JLabel("Max Socket Pool Size");
		panel_1.add(lblMaxpoolsize);
		
		JLabel lblNewLabel_1 = new JLabel("Dal Client Count");
		panel_1.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Das Worker Ports");
		panel_1.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Duration(second)");
		panel_1.add(lblNewLabel_3);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new GridLayout(5, 0, 0, 5));
		
		threadCountTF = new JTextField();
		threadCountTF.setText("");
		panel_2.add(threadCountTF);
		threadCountTF.setColumns(10);
		
		logicDbTF = new JTextField();
		panel_2.add(logicDbTF);
		logicDbTF.setColumns(10);
		
		portsTF = new JTextField();
		panel_2.add(portsTF);
		portsTF.setColumns(10);
		
		sqlTF = new JTextField();
		panel_2.add(sqlTF);
		sqlTF.setColumns(10);
		
		poolSizeTF = new JTextField();
		panel_2.add(poolSizeTF);
		poolSizeTF.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		panel.add(panel_3, BorderLayout.EAST);
		panel_3.setLayout(new GridLayout(4, 1, 0, 0));
		
		JButton btnStart = new JButton("Accept");
		panel_3.add(btnStart);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		frame.getContentPane().add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		panel_4.add(textArea, BorderLayout.CENTER);
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_4.add(panel_5, BorderLayout.NORTH);
		
		JLabel lblNewLabel_4 = new JLabel("Request Count");
		panel_5.add(lblNewLabel_4);
		
		textField = new JTextField();
		panel_5.add(textField);
		textField.setColumns(10);
		
		JButton btnStart_1 = new JButton("Start");
		panel_5.add(btnStart_1);
	}

}
