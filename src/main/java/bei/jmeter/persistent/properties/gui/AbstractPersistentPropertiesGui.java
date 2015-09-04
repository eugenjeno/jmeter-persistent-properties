/*******************************************************************************
 * Copyright 2015 Eugen Bodolak
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package bei.jmeter.persistent.properties.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import bei.jmeter.persistent.properties.AbstractPersistentProperties;

abstract class AbstractPersistentPropertiesGui extends
		AbstractJMeterGuiComponent {

	private static final long serialVersionUID = 0L;

	protected static final Logger log = LoggingManager.getLoggerForClass();

	protected static final String PARAMETER_COL_RESOURCE = "name"; // $NON-NLS-1$

	protected static final String DEFAULT_PARAM_FILTER = ".*"; // $NON-NLS-1$

	protected JPanel optionsPanel;
	
	protected JTable paramTable;

	protected PowerTableModel paramTableModel;

	private JButton addButton, deleteButton;

	private JPanel paramPanel;

	protected JTextField paramFilepath;

	protected JCheckBox useDefaultParamFile;
	
	public AbstractPersistentPropertiesGui() {
		super();
		init();
		clearGui();
	}

	protected void init() {
		setBorder(makeBorder());
		setLayout(new BorderLayout());

		add(makeTitlePanel(), BorderLayout.NORTH);

		JPanel mainPanel = new JPanel(new BorderLayout());

		Box mainBox = Box.createVerticalBox();
		mainBox.add(makeOptionsPanel());
		mainPanel.add(mainBox, BorderLayout.NORTH);

		mainPanel.add(makeParameterPanel(), BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);
	}

	private JPanel makeOptionsPanel() {

		JPanel panelParamFilepath = new JPanel(new BorderLayout(5, 0));
		paramFilepath = new JTextField("", 20);
		JLabel paramFilepathLabel = new JLabel("Property file path: "); // $NON-NLS-1$
		paramFilepathLabel.setLabelFor(paramFilepath);
		panelParamFilepath.add(paramFilepathLabel, BorderLayout.WEST);
		panelParamFilepath.add(paramFilepath, BorderLayout.CENTER);

		JPanel panelUseDefaultParamFile = new JPanel(new BorderLayout(5, 0));
		useDefaultParamFile = new JCheckBox();
		JLabel useDefaultParamFileLabel = new JLabel(
				"Use default parameter file: "); // $NON-NLS-1$
		useDefaultParamFileLabel.setLabelFor(useDefaultParamFile);
		panelUseDefaultParamFile.add(useDefaultParamFileLabel,
				BorderLayout.WEST);
		panelUseDefaultParamFile.add(useDefaultParamFile, BorderLayout.CENTER);

		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setBorder(BorderFactory.createEtchedBorder());

		optionsPanel.add(panelParamFilepath);
		optionsPanel.add(panelUseDefaultParamFile);

		return optionsPanel;
	}

	private JPanel makeParameterPanel() {
		JLabel tableLabel = new JLabel("user_parameters_table"); // $NON-NLS-1$
		initTableModel();
		paramTable = new JTable(paramTableModel);
		paramTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		paramTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		paramTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		paramPanel = new JPanel(new BorderLayout());
		paramPanel.add(tableLabel, BorderLayout.NORTH);
		JScrollPane scroll = new JScrollPane(paramTable);
		scroll.setPreferredSize(scroll.getMinimumSize());
		paramPanel.add(scroll, BorderLayout.CENTER);
		paramPanel.add(makeButtonPanel(), BorderLayout.SOUTH);
		return paramPanel;
	}

	abstract String getTitle();

	@Override
	public String getLabelResource() {
		return getTitle(); // $NON-NLS-1$
	}

	@Override
	public String getStaticLabel() {
		return getLabelResource();
	}

	/**
	 * Modifies a given GUI to mirror the data in the TestElement.
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(TestElement)
	 */
	@Override
	public void configure(TestElement element) {		
		super.configure(element);
		
		initTableModel();
		paramTable.setModel(paramTableModel);

		AbstractPersistentProperties persistentProp = (AbstractPersistentProperties) element;
		CollectionProperty paramFiletrs = persistentProp.getFilters();
		paramTableModel.setColumnData(0,
				(List<?>) paramFiletrs.getObjectValue());
		useDefaultParamFile.setSelected(persistentProp.useDefaultFilepath());
		paramFilepath.setText(persistentProp.getFilepath());

		
	}

	abstract AbstractPersistentProperties getPersistentUserParametersInstance();

	/**
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
	 */
	@Override
	public TestElement createTestElement() {
		AbstractPersistentProperties persistentProp = getPersistentUserParametersInstance();
		modifyTestElement(persistentProp);
		return persistentProp;
	}

	/**
	 * Modifies a given TestElement to mirror the data in the GUI components.
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
	 */
	@Override
	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
		
		GuiUtils.stopTableEditing(paramTable);

		AbstractPersistentProperties persistentProp = ((AbstractPersistentProperties) element);

		persistentProp.setFilters(paramTableModel.getColumnData(PARAMETER_COL_RESOURCE));
		persistentProp.setUseDefaultFilepath(useDefaultParamFile.isSelected());
		persistentProp.setFilepath(paramFilepath.getText().trim());
	}

	/**
	 * Implements JMeterGUIComponent.clearGui
	 */
	@Override
	public void clearGui() {
		super.clearGui();

		clearParamTable();
		
		addDefaultParamFilter();
		
		paramFilepath.setText("");
		useDefaultParamFile.setSelected(true);
	}

	private void addDefaultParamFilter() {

		paramTableModel.addRow(new Object[] { DEFAULT_PARAM_FILTER }); // $NON-NLS-1$
		paramTableModel.fireTableDataChanged();
	}

	private void clearParamTable() {
		initTableModel();
		paramTable.setModel(paramTableModel);
		HeaderAsPropertyRenderer defaultRenderer = new HeaderAsPropertyRenderer() {
			private static final long serialVersionUID = 240L;

			@Override
			protected String getText(Object value, int row, int column) {
				return super.getText(value, row, column);
			}
		};
		paramTable.getTableHeader().setDefaultRenderer(defaultRenderer);
	}

	protected void initTableModel() {
		paramTableModel = new PowerTableModel(
				new String[] { PARAMETER_COL_RESOURCE } // $NON-NLS-1$
				, new Class[] { String.class });
	}

	private JPanel makeButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 2));
		addButton = new JButton("Add parameter filter"); // $NON-NLS-1$
		deleteButton = new JButton("Delete parameter filter"); // $NON-NLS-1$
		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);
		addButton.addActionListener(new AddAction());
		deleteButton.addActionListener(new DeleteAction());
		return buttonPanel;
	}

	private class AddAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			GuiUtils.stopTableEditing(paramTable);

			paramTableModel.addNewRow();
			paramTableModel.fireTableDataChanged();

			// Enable DELETE (which may already be enabled, but it won't hurt)
			deleteButton.setEnabled(true);

			// Highlight (select) the appropriate row.
			int rowToSelect = paramTableModel.getRowCount() - 1;
			paramTable.setRowSelectionInterval(rowToSelect, rowToSelect);
		}
	}

	private class DeleteAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (paramTable.isEditing()) {
				TableCellEditor cellEditor = paramTable.getCellEditor(
						paramTable.getEditingRow(),
						paramTable.getEditingColumn());
				cellEditor.cancelCellEditing();
			}

			int rowSelected = paramTable.getSelectedRow();
			if (rowSelected >= 0) {
				paramTableModel.removeRow(rowSelected);
				paramTableModel.fireTableDataChanged();

				// Disable DELETE if there are no rows in the table to delete.
				if (paramTableModel.getRowCount() == 0) {
					deleteButton.setEnabled(false);
				}

				// Table still contains one or more rows, so highlight (select)
				// the appropriate one.
				else {
					int rowToSelect = rowSelected;

					if (rowSelected >= paramTableModel.getRowCount()) {
						rowToSelect = rowSelected - 1;
					}

					paramTable
							.setRowSelectionInterval(rowToSelect, rowToSelect);
				}
			}
		}
	}

}
