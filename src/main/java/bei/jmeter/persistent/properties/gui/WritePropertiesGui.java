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

import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jorphan.gui.GuiUtils;

import bei.jmeter.persistent.properties.AbstractPersistentProperties;
import bei.jmeter.persistent.properties.WriteProperties;

public final class WritePropertiesGui extends
		AbstractPersistentPropertiesGui {

	private static final long serialVersionUID = 0L;

	private JCheckBox includeSamplerProperties;

	private JCheckBox includeJMeterVariables;

	private JCheckBox includeJMeterProperties;

	private JCheckBox includeSystemProperties;

	@Override
	protected void init() {
		super.init();

		JPanel panelIncludes = new JPanel(new GridLayout(2, 4));

		includeSamplerProperties = new JCheckBox();
		JLabel includeSamplerPropertiesLabel = new JLabel(
				"Include sampler properties: "); // $NON-NLS-1$
		includeSamplerPropertiesLabel.setLabelFor(includeSamplerProperties);
		panelIncludes.add(includeSamplerPropertiesLabel);
		panelIncludes.add(includeSamplerProperties);

		includeJMeterVariables = new JCheckBox();
		JLabel includeJMeterVariablesLabel = new JLabel(
				"Include JMeter variables: "); // $NON-NLS-1$
		includeJMeterVariablesLabel.setLabelFor(includeJMeterVariables);
		panelIncludes.add(includeJMeterVariablesLabel);
		panelIncludes.add(includeJMeterVariables);

		includeJMeterProperties = new JCheckBox();
		JLabel includeJMeterPropertiesLabel = new JLabel(
				"Include JMeter properties: "); // $NON-NLS-1$
		includeJMeterPropertiesLabel.setLabelFor(includeJMeterProperties);
		panelIncludes.add(includeJMeterPropertiesLabel);
		panelIncludes.add(includeJMeterProperties);

		includeSystemProperties = new JCheckBox();
		JLabel includeSystemPropertiesLabel = new JLabel(
				"Include system properties: "); // $NON-NLS-1$
		includeSystemPropertiesLabel.setLabelFor(includeSystemProperties);
		panelIncludes.add(includeSystemPropertiesLabel);
		panelIncludes.add(includeSystemProperties);

		optionsPanel.add(panelIncludes);
	}

	@Override
	public Collection<String> getMenuCategories() {
		return Arrays.asList(MenuFactory.POST_PROCESSORS);
	}

	@Override
	public JPopupMenu createPopupMenu() {
		return MenuFactory.getDefaultAssertionMenu();
	}

	@Override
	AbstractPersistentProperties getPersistentUserParametersInstance() {
		return new WriteProperties();
	}

	@Override
	String getTitle() {
		return "Write properties to file"; // $NON-NLS-1$
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

		WriteProperties persistentProp = (WriteProperties) element;
		CollectionProperty paramFiletrs = persistentProp.getFilters();
		paramTableModel.setColumnData(0,
				(List<?>) paramFiletrs.getObjectValue());
		useDefaultParamFile.setSelected(persistentProp.useDefaultFilepath());
		paramFilepath.setText(persistentProp.getFilepath());

		includeSamplerProperties.setSelected(persistentProp
				.includeSamplerProperties());
		includeJMeterVariables
				.setSelected(persistentProp.includeJMeterVariables());
		includeJMeterProperties.setSelected(persistentProp
				.includeJMeterProperties());
		includeSystemProperties.setSelected(persistentProp
				.includeSystemProperties());
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

		WriteProperties persistentProp = ((WriteProperties) element);

		persistentProp.setFilters(paramTableModel.getColumnData(PARAMETER_COL_RESOURCE));
		persistentProp.setUseDefaultFilepath(useDefaultParamFile.isSelected());
		persistentProp.setFilepath(paramFilepath.getText().trim());	

		persistentProp.setIncludeSamplerProperties(includeSamplerProperties
				.isSelected());
		persistentProp.setIncludeJMeterVariables(includeJMeterVariables
				.isSelected());
		persistentProp.setIncludeJMeterProperties(includeJMeterProperties
				.isSelected());
		persistentProp.setIncludeSystemProperties(includeSystemProperties
				.isSelected());
	}

	@Override
	public void clearGui() {
		super.clearGui();

		includeSamplerProperties.setSelected(true);
		includeJMeterVariables.setSelected(true);
		includeJMeterProperties.setSelected(true);
		includeSystemProperties.setSelected(false);
	}
}
