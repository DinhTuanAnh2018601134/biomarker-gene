package kcore.plugin.alg;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class giaodien extends JFrame {

	private JPanel contentPane;
	private final ButtonGroup buttonGroupProcess = new ButtonGroup();
	private final ButtonGroup buttonGroupMethod = new ButtonGroup();
	private final ButtonGroup buttonGroupProcessMethod = new ButtonGroup();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					giaodien frame = new giaodien();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public giaodien() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(5, 5, 424, 245);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Biomaker gene", null, panel, null);
		panel.setLayout(null);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("CPU");
		buttonGroupProcess.add(rdbtnNewRadioButton);
		rdbtnNewRadioButton.setBounds(23, 33, 109, 23);
		panel.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("GPU");
		buttonGroupProcess.add(rdbtnNewRadioButton_1);
		rdbtnNewRadioButton_1.setBounds(23, 71, 109, 23);
		panel.add(rdbtnNewRadioButton_1);
		
		JButton btnNewButton_1 = new JButton("RUN");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_1.setBounds(170, 183, 89, 23);
		panel.add(btnNewButton_1);
		
		JRadioButton rdbtnNewRadioButton_8 = new JRadioButton("Sequence");
		buttonGroupProcess.add(rdbtnNewRadioButton_8);
		rdbtnNewRadioButton_8.setBounds(23, 111, 109, 23);
		panel.add(rdbtnNewRadioButton_8);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Extend function", null, panel_1, null);
		panel_1.setLayout(null);
		
		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("K core");
		buttonGroupMethod.add(rdbtnNewRadioButton_2);
		rdbtnNewRadioButton_2.setBounds(24, 54, 109, 23);
		panel_1.add(rdbtnNewRadioButton_2);
		
		JRadioButton rdbtnNewRadioButton_3 = new JRadioButton("R core");
		buttonGroupMethod.add(rdbtnNewRadioButton_3);
		rdbtnNewRadioButton_3.setBounds(24, 88, 109, 23);
		panel_1.add(rdbtnNewRadioButton_3);
		
		JRadioButton rdbtnNewRadioButton_4 = new JRadioButton("HC");
		buttonGroupMethod.add(rdbtnNewRadioButton_4);
		rdbtnNewRadioButton_4.setBounds(24, 127, 109, 23);
		panel_1.add(rdbtnNewRadioButton_4);
		
		JRadioButton rdbtnNewRadioButton_5 = new JRadioButton("CPU");
		buttonGroupProcessMethod.add(rdbtnNewRadioButton_5);
		rdbtnNewRadioButton_5.setBounds(153, 54, 109, 23);
		panel_1.add(rdbtnNewRadioButton_5);
		
		JRadioButton rdbtnNewRadioButton_6 = new JRadioButton("GPU");
		buttonGroupProcessMethod.add(rdbtnNewRadioButton_6);
		rdbtnNewRadioButton_6.setBounds(153, 88, 109, 23);
		panel_1.add(rdbtnNewRadioButton_6);
		
		JLabel lblNewLabel = new JLabel("Choose method");
		lblNewLabel.setBounds(24, 33, 94, 14);
		panel_1.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Process");
		lblNewLabel_1.setBounds(153, 33, 109, 14);
		panel_1.add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("RUN");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(170, 183, 89, 23);
		panel_1.add(btnNewButton);
		
		JRadioButton rdbtnNewRadioButton_7 = new JRadioButton("Sequence");
		buttonGroupProcessMethod.add(rdbtnNewRadioButton_7);
		rdbtnNewRadioButton_7.setBounds(153, 127, 109, 23);
		panel_1.add(rdbtnNewRadioButton_7);
	}
}
