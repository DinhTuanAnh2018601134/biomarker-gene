// ActivePathsParametersPopupDialog
//-----------------------------------------------------------------------------
// $Date: 2019 - 17 - 07
// $Author: quang ares
//-----------------------------------------------------------------------------
package kcore.plugin.alg;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Collection;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JButton;

import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kcore.plugin.alg.param.KcoreParameters;
import kcore.plugin.alg.param.NetFilteringMethod;
import kcore.plugin.service.ServicesUtil;
import kcore.plugin.ui.NetworkSelectorPanel;
import java.awt.EventQueue;

public class KcoreParametersPanel extends JPanel implements ColumnCreatedListener, CytoPanelComponentSelectedListener {

	private static final Logger logger = LoggerFactory.getLogger(KcoreParametersPanel.class);

	private boolean isPanelSelected = false;

	private NetworkSelectorPanel networkSelectorPanel;

	private JComboBox<NetFilteringMethod> netFilteringMethod;

	private JRadioButton gpuDevice;
	private JRadioButton cpuDevice;
	
	private final ButtonGroup buttonGroupProcess = new ButtonGroup();
	private final ButtonGroup buttonGroupMethod = new ButtonGroup();
	private final ButtonGroup buttonGroupProcessMethod = new ButtonGroup();

	public KcoreParametersPanel(NetworkSelectorPanel gtaNetworkSelectorPanel) {
		this.networkSelectorPanel = gtaNetworkSelectorPanel;
		this.networkSelectorPanel.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateAttributePanel();	 
				
			}
		});
		
		

		// Set global parameters
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(32, 42));

		initComponents();
	}

	private void initComponents() {
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gridBagConstraints;
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5); // 5-5-5-5
		add(networkSelectorPanel, gridBagConstraints);
//
		initAttrSelectionTable();
//
		javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
		buttonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		javax.swing.JButton runButton = new javax.swing.JButton("Run");
		runButton.addActionListener(new FindModulesAction());
		buttonPanel.add(runButton, gridBagConstraints);

		add(buttonPanel, gridBagConstraints);
		// demo temp panel
		// demo
		javax.swing.JPanel jPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints gridBagConstraint = new java.awt.GridBagConstraints();
		gridBagConstraint.gridy = 3;
		gridBagConstraint.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraint.weightx = 1.0;
		gridBagConstraint.weighty = 1.0;
		gridBagConstraint.insets = new java.awt.Insets(0, 0, 0, 0);
		add(jPanel, gridBagConstraint);

	}

	private static GridBagConstraints gridConstraint(int gridx, int gridy, int gridwidth, int gridheight, int anchor,
			int fill) {
		final Insets insets = new Insets(0, 0, 0, 0);

		return new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0, anchor, fill, insets, 0, 0);
	}

	private void initAttrSelectionTable() {
		
		java.awt.GridBagConstraints gridBagConstraints;

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		add(networkSelectorPanel, gridBagConstraints);

		javax.swing.JPanel bothAttributeSelectorPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());

		javax.swing.JPanel configPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
		bothAttributeSelectorPanel.setPreferredSize(new Dimension(32, 20));

		javax.swing.JPanel advancedConfigPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
		advancedConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced Options"));
		//new content
//		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
//		tabbedPane.setBounds(5, 5, 424, 245);
//		advancedConfigPanel.add(tabbedPane);
//		
//		JPanel panel = new JPanel();
//		tabbedPane.addTab("Biomaker gene", null, panel, null);
//		panel.setLayout(null);
//		
//		JRadioButton rdbtnNewRadioButton = new JRadioButton("CPU");
//		buttonGroupProcess.add(rdbtnNewRadioButton);
//		rdbtnNewRadioButton.setBounds(23, 33, 109, 23);
//		panel.add(rdbtnNewRadioButton);
//		
//		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("GPU");
//		buttonGroupProcess.add(rdbtnNewRadioButton_1);
//		rdbtnNewRadioButton_1.setBounds(23, 71, 109, 23);
//		panel.add(rdbtnNewRadioButton_1);
//		
//		JButton btnNewButton_1 = new JButton("RUN");
//		btnNewButton_1.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//			}
//		});
//		btnNewButton_1.setBounds(170, 183, 89, 23);
//		panel.add(btnNewButton_1);
//		
//		JRadioButton rdbtnNewRadioButton_8 = new JRadioButton("Sequence");
//		buttonGroupProcess.add(rdbtnNewRadioButton_8);
//		rdbtnNewRadioButton_8.setBounds(23, 111, 109, 23);
//		panel.add(rdbtnNewRadioButton_8);
//		
//		JPanel panel_1 = new JPanel();
//		tabbedPane.addTab("Extend function", null, panel_1, null);
//		panel_1.setLayout(null);
//		
//		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("K core");
//		buttonGroupMethod.add(rdbtnNewRadioButton_2);
//		rdbtnNewRadioButton_2.setBounds(24, 54, 109, 23);
//		panel_1.add(rdbtnNewRadioButton_2);
//		
//		JRadioButton rdbtnNewRadioButton_3 = new JRadioButton("R core");
//		buttonGroupMethod.add(rdbtnNewRadioButton_3);
//		rdbtnNewRadioButton_3.setBounds(24, 88, 109, 23);
//		panel_1.add(rdbtnNewRadioButton_3);
//		
//		JRadioButton rdbtnNewRadioButton_4 = new JRadioButton("HC");
//		buttonGroupMethod.add(rdbtnNewRadioButton_4);
//		rdbtnNewRadioButton_4.setBounds(24, 127, 109, 23);
//		panel_1.add(rdbtnNewRadioButton_4);
//		
//		JRadioButton rdbtnNewRadioButton_5 = new JRadioButton("CPU");
//		buttonGroupProcessMethod.add(rdbtnNewRadioButton_5);
//		rdbtnNewRadioButton_5.setBounds(153, 54, 109, 23);
//		panel_1.add(rdbtnNewRadioButton_5);
//		
//		JRadioButton rdbtnNewRadioButton_6 = new JRadioButton("GPU");
//		buttonGroupProcessMethod.add(rdbtnNewRadioButton_6);
//		rdbtnNewRadioButton_6.setBounds(153, 88, 109, 23);
//		panel_1.add(rdbtnNewRadioButton_6);
//		
//		JLabel lblNewLabel = new JLabel("Choose method");
//		lblNewLabel.setBounds(24, 33, 94, 14);
//		panel_1.add(lblNewLabel);
//		
//		JLabel lblNewLabel_1 = new JLabel("Process");
//		lblNewLabel_1.setBounds(153, 33, 109, 14);
//		panel_1.add(lblNewLabel_1);
//		
//		JButton btnNewButton = new JButton("RUN");
//		btnNewButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//			}
//		});
//		btnNewButton.setBounds(170, 183, 89, 23);
//		panel_1.add(btnNewButton);
//		
//		JRadioButton rdbtnNewRadioButton_7 = new JRadioButton("Sequence");
//		buttonGroupProcessMethod.add(rdbtnNewRadioButton_7);
//		rdbtnNewRadioButton_7.setBounds(153, 127, 109, 23);
//		panel_1.add(rdbtnNewRadioButton_7);
		
		
		// content
		advancedConfigPanel.add(new java.awt.Label("Choose method:"),
				gridConstraint(0, 2, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL));
		netFilteringMethod = new JComboBox<NetFilteringMethod>(NetFilteringMethod.values());
		advancedConfigPanel.add(netFilteringMethod,
				gridConstraint(1, 2, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL));
		this.netFilteringMethod.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateAttributePanel();
				 Object item = e.getItem();
				 if(item == NetFilteringMethod.FILTER_BY_HC || item == NetFilteringMethod.FILTER_BY_BIO){
						cpuDevice.setSelected(true);
						gpuDevice.setSelected(false);
						gpuDevice.setVisible(false);
					}
				 else{
					 cpuDevice.setVisible(true);
					 gpuDevice.setVisible(true);
				 }
				
			}
		});

		cpuDevice = new JRadioButton("CPU1");

		advancedConfigPanel.add(cpuDevice,
				gridConstraint(0, 3, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL));

		gpuDevice = new JRadioButton("GPU");
		// set action
		cpuDevice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (cpuDevice.isSelected()) {
					gpuDevice.setSelected(false);
				} else {
					gpuDevice.setSelected(true);
				}
			}
		});
		gpuDevice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (gpuDevice.isSelected()) {
					cpuDevice.setSelected(false);
				} else {
					cpuDevice.setSelected(true);
				}
			}
		});

		advancedConfigPanel.add(gpuDevice,
				gridConstraint(0, 4, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL));
		// demo
		advancedConfigPanel.add(new java.awt.Label(""),
				gridConstraint(0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));
		advancedConfigPanel.add(new java.awt.Label(""),
				gridConstraint(1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));
		advancedConfigPanel.add(new java.awt.Label(""),
				gridConstraint(0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));

		advancedConfigPanel.add(new java.awt.Label(""),
				gridConstraint(1, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));
		// end content

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		bothAttributeSelectorPanel.add(advancedConfigPanel, gridBagConstraints);

		configPanel.add(bothAttributeSelectorPanel,
				gridConstraint(0, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		add(configPanel, gridBagConstraints);

	}

	private String showSaveFileDialog() {
		String url = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");

		int userSelection = fileChooser.showSaveDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			url = fileToSave.getAbsolutePath();
		}

		return url;
	}

	private void populateAttributeTable(Vector<String[]> dataVect) {
		logger.debug("populateAttributeTable");
	}

	private void updateAttributePanel() {
		if (!this.isPanelSelected) {
			return;
		}
		Vector<String[]> data = this.getDataVector();
		this.populateAttributeTable(data);
	}

	@Override
	public void handleEvent(ColumnCreatedEvent e) {
		updateAttributePanel();
	}

	public void handleEvent(CytoPanelComponentSelectedEvent e) {
		logger.debug("Event Occured " + e.toString() + " " + (e.getCytoPanel().getSelectedComponent() == this));
		if (e.getCytoPanel().getSelectedComponent() == this) {
			this.isPanelSelected = true;
			updateAttributePanel();
		} else {
			this.isPanelSelected = false;
		}
	}

	private Vector<String[]> getDataVector() {
		Vector<String[]> dataVect = new Vector<String[]>();

		if (networkSelectorPanel.getSelectedNetwork() == null) {
			return dataVect;
		}

		CyTable table = networkSelectorPanel.getSelectedNetwork().getDefaultNodeTable();
		Collection<CyColumn> columns = table.getColumns();

		return dataVect;
	}

	public class FindModulesAction extends AbstractAction {

		public FindModulesAction() {
			super("Find Modules");
		}

		public void actionPerformed(ActionEvent e) {
			String path = showSaveFileDialog();
			if (path == null || path.equals("")) {
				return;
			}
			runAlgorithm(path);

		}

		private void putMessage(String name) {
			JOptionPane.showMessageDialog(ServicesUtil.cySwingApplicationServiceRef.getJFrame(),
					"Compute " + name + " Success, open text file to see the result!", "Infor",
					JOptionPane.INFORMATION_MESSAGE);
		}

		private void runAlgorithm(String path) {

			if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_KCORE && cpuDevice.isSelected()
					&& !gpuDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path);
				alg.runKcore();
			} else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_RCORE
					&& cpuDevice.isSelected() && !gpuDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path);
				alg.runRcore();
				
			} else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_KCORE
					&& !cpuDevice.isSelected() && gpuDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path);
				alg.runKcoreGPU();
				
			} else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_RCORE
					&& !cpuDevice.isSelected() && gpuDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path);
//				alg.runRcoreGPU();
				alg.runHcParallel();
				
			} else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_HC) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path);
				alg.runHc();
			}else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_BIO) {
					KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
							(NetFilteringMethod) netFilteringMethod.getSelectedItem());
					KcoreRunner alg = new KcoreRunner(params, path);
					alg.runBiomaker();
			} else {
				JOptionPane.showMessageDialog(null, "Chọn một thiết bị thực thi.");
				return;
			}

		}

	}

}
