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

	private JRadioButton sequenceDevice;
	private JRadioButton gpuDevice;
	private JRadioButton cpuDevice;

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
		
		// content
		advancedConfigPanel.add(new java.awt.Label("Choose method:"),
				gridConstraint(0, 2, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL));
		netFilteringMethod = new JComboBox<NetFilteringMethod>(NetFilteringMethod.values());
		advancedConfigPanel.add(netFilteringMethod,
				gridConstraint(1, 2, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL));
//		this.netFilteringMethod.addItemListener(new ItemListener() {
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				updateAttributePanel();
//				 Object item = e.getItem();
//				 if(item == NetFilteringMethod.FILTER_BY_HC || item == NetFilteringMethod.FILTER_BY_BIO){
//						cpuDevice.setSelected(true);
//						gpuDevice.setSelected(false);
//						gpuDevice.setVisible(false);
//					}
//				 else{
//					 cpuDevice.setVisible(true);
//					 gpuDevice.setVisible(true);
//				 }
//				
//			}
//		});

		
		sequenceDevice = new JRadioButton("Sequence");
		advancedConfigPanel.add(sequenceDevice,
				gridConstraint(0, 3, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL));
		
		cpuDevice = new JRadioButton("CPU");
		advancedConfigPanel.add(cpuDevice,
				gridConstraint(0, 4, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL));

		gpuDevice = new JRadioButton("GPU");
		// set action
		sequenceDevice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (sequenceDevice.isSelected()) {
					cpuDevice.setSelected(false);
					gpuDevice.setSelected(false);
				} else {
//					gpuDevice.setSelected(true);
				}
			}
		});
		cpuDevice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (cpuDevice.isSelected()) {
					sequenceDevice.setSelected(false);
					gpuDevice.setSelected(false);
				} else {
//					gpuDevice.setSelected(true);
				}
			}
		});
		gpuDevice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (gpuDevice.isSelected()) {
					sequenceDevice.setSelected(false);
					cpuDevice.setSelected(false);
				} else {
//					cpuDevice.setSelected(true);
				}
			}
		});

		advancedConfigPanel.add(gpuDevice,
				gridConstraint(0, 5, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL));
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
			//K-core sequence
			if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_KCORE && !cpuDevice.isSelected()
					&& !gpuDevice.isSelected() && sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "");
				alg.runKcore();
			} 
			//R-core sequence
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_RCORE
					&& !cpuDevice.isSelected() && !gpuDevice.isSelected() && sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "");
				alg.runRcore();
				
			} 
			//K-core CPU
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_KCORE
					&& cpuDevice.isSelected() && !gpuDevice.isSelected() && !sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "CPU");
				alg.runKcoreGPU();
				
			} 
			//K-core GPU
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_KCORE
					&& !cpuDevice.isSelected() && gpuDevice.isSelected() && !sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "GPU");
				alg.runKcoreGPU();
				
			} 
			//R-core CPU
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_RCORE
					&& cpuDevice.isSelected() && !gpuDevice.isSelected() && !sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "CPU");
				alg.runRcoreGPU();
			}
			//R-core GPU
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_RCORE
					&& !cpuDevice.isSelected() && gpuDevice.isSelected() && !sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "GPU");
				alg.runRcoreGPU();
			}
			//HC sequence
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_HC
					&& !cpuDevice.isSelected() && !gpuDevice.isSelected() && sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "");
				alg.runHc();
			}
			//HC CPU
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_HC
					&& cpuDevice.isSelected() && !gpuDevice.isSelected() && !sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "CPU");
				alg.runHcParallel(false);
			}
			//HC GPU
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_HC
					&& !cpuDevice.isSelected() && gpuDevice.isSelected() && !sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "GPU");
				alg.runHcParallel(false);
			}
			//BiomarkerGene sequence
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_BIO
					&& !cpuDevice.isSelected() && !gpuDevice.isSelected() && sequenceDevice.isSelected()) {
					KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
							(NetFilteringMethod) netFilteringMethod.getSelectedItem());
					KcoreRunner alg = new KcoreRunner(params, path, "");
					alg.runBiomaker();
			}
			//BiomarkerGene CPU
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_BIO
					&& cpuDevice.isSelected() && !gpuDevice.isSelected() && !sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "CPU");
				alg.runBiomakerParallel();
//				alg.runRcoreGPU();
//				alg.runHcParallel(true);
			}
			//BiomarkerGene GPU
			else if (netFilteringMethod.getSelectedItem() == NetFilteringMethod.FILTER_BY_BIO
					&& !cpuDevice.isSelected() && gpuDevice.isSelected() && !sequenceDevice.isSelected()) {
				KcoreParameters params = new KcoreParameters(networkSelectorPanel.getSelectedNetwork(), 10,
						(NetFilteringMethod) netFilteringMethod.getSelectedItem());
				KcoreRunner alg = new KcoreRunner(params, path, "GPU");
				alg.runBiomakerParallel();
//				alg.runRcoreGPU();
//				alg.runHcParallel(true);
			}
			 else {
				JOptionPane.showMessageDialog(null, "Chọn một thiết bị thực thi.");
				return;
			}
		}

	}

}
