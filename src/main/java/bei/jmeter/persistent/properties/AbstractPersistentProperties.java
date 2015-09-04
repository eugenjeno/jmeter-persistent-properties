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

package bei.jmeter.persistent.properties;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public abstract class AbstractPersistentProperties extends AbstractTestElement
		implements Serializable {

	private static final long serialVersionUID = 0L;

	protected static final Logger log = LoggingManager.getLoggerForClass();

	public static final String FILTERS = "PersistentProperties.filters";// $NON-NLS-1$

	public static final String FILEPATH = "PersistentProperties.filepath";// $NON-NLS-1$

	public static final String USE_DEFAULT_FILEPATH = "PersistentProperties.use.default.filepath";// $NON-NLS-1$

	public static final String DEFAULT_FILEPATH = "/tmp/jmeter.persistent.properties";

	protected File propertyFile = null;

	protected transient Object lock = new Object();

	private Object readResolve() {
		lock = new Object();
		return this;
	}

	public CollectionProperty getFilters() {
		JMeterProperty property = getProperty(FILTERS);
		return (property instanceof NullProperty) ? new CollectionProperty()
				: (CollectionProperty) property;
	}

	public String getFilepath() {
		return getPropertyAsString(FILEPATH);
	}

	public String getActualFilepath() {
		return useDefaultFilepath() ? DEFAULT_FILEPATH : getFilepath();
	}

	public void setFilters(List<?> list) {
		setProperty(new CollectionProperty(
				AbstractPersistentProperties.FILTERS, list));
	}

	public void setFilepath(String filepath) {
		setProperty(new StringProperty(FILEPATH, filepath));
	}

	public boolean useDefaultFilepath() {
		return getPropertyAsBoolean(USE_DEFAULT_FILEPATH);
	}

	public void setUseDefaultFilepath(boolean useDefaultFilepath) {
		setProperty(new BooleanProperty(USE_DEFAULT_FILEPATH,
				useDefaultFilepath));
	}

	// @Override
	// public void process() {
	// handleProcess();
	// }

	abstract void handleProcess();

	@SuppressWarnings("rawtypes")
	protected List<Map.Entry> filterProperties(List<Map.Entry> list) {
		List<Map.Entry> filteredProperties = new ArrayList<Map.Entry>();

		PropertyIterator filterIter = getFilters().iterator();
		while (filterIter.hasNext()) {
			String filter = filterIter.next().getStringValue();

			log.info("Processing filter: " + filter);

			String key = "";
			for (Entry entry : list) {
				key = entry.getKey().toString();
				if (key.matches(filter)) {
					log.info("Adding filetered property: " + key + " = "
							+ entry.getValue().toString());
					filteredProperties.add(entry);
				}
			}
		}

		return filteredProperties;
	}

	@Override
	public Object clone() {
		AbstractPersistentProperties persistentProp = (AbstractPersistentProperties) super
				.clone();
		persistentProp.lock = lock;
		return persistentProp;
	}

	/**
	 * @see org.apache.jmeter.testelement.AbstractTestElement#mergeIn(TestElement)
	 */
	@Override
	protected void mergeIn(TestElement element) {
		// super.mergeIn(element);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[getFilters()="
				+ getFilters() + ", getFilepath()=" + getFilepath()
				+ ", useDefaultFilepath()=" + useDefaultFilepath() + "]";
	}

}
