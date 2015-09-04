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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.util.JMeterUtils;

public final class WriteProperties extends AbstractPersistentProperties
		implements PostProcessor {

	private static final long serialVersionUID = 0L;

	public static final String INCLUDE_SAMPLER_PROPERTIES = "PersistentProperties.include.sampler.properties";// $NON-NLS-1$

	public static final String INCLUDE_JMETER_VARIABLES = "PersistentProperties.include.JMeter.variables";// $NON-NLS-1$

	public static final String INCLUDE_JMETER_PROPERTIES = "PersistentProperties.include.JMeter.properties";// $NON-NLS-1$

	public static final String INCLUDE_SYSTEM_PROPERTIES = "PersistentProperties.include.system.properties";// $NON-NLS-1$

	public boolean includeSamplerProperties() {
		return getPropertyAsBoolean(INCLUDE_SAMPLER_PROPERTIES);
	}

	public void setIncludeSamplerProperties(boolean includeSamplerProperties) {
		setProperty(new BooleanProperty(INCLUDE_SAMPLER_PROPERTIES,
				includeSamplerProperties));
	}

	public boolean includeJMeterVariables() {
		return getPropertyAsBoolean(INCLUDE_JMETER_VARIABLES);
	}

	public void setIncludeJMeterVariables(boolean includeJMeterVariables) {
		setProperty(new BooleanProperty(INCLUDE_JMETER_VARIABLES,
				includeJMeterVariables));
	}

	public boolean includeJMeterProperties() {
		return getPropertyAsBoolean(INCLUDE_JMETER_PROPERTIES);
	}

	public void setIncludeJMeterProperties(boolean includeJMeterProperties) {
		setProperty(new BooleanProperty(INCLUDE_JMETER_PROPERTIES,
				includeJMeterProperties));
	}

	public boolean includeSystemProperties() {
		return getPropertyAsBoolean(INCLUDE_SYSTEM_PROPERTIES);
	}

	public void setIncludeSystemProperties(boolean includeSystemProperties) {
		setProperty(new BooleanProperty(INCLUDE_SYSTEM_PROPERTIES,
				includeSystemProperties));
	}

	@Override
	public void process() {
		handleProcess();
	}

	@SuppressWarnings("rawtypes")
	private List<Map.Entry> formatPropertyIterator(PropertyIterator iter) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		while (iter.hasNext()) {
			JMeterProperty item = iter.next();
			map.put(item.getName(), item.getObjectValue());
		}
		return formatSet(map.entrySet());
	}

	@SuppressWarnings("rawtypes")
	private List<Map.Entry> formatSet(Set set) {

		@SuppressWarnings("unchecked")
		List<Map.Entry> propertiesList = new ArrayList<Entry>(set);
		Collections.sort(propertiesList, new Comparator<Map.Entry>() {
			@Override
			public int compare(Map.Entry entry1, Map.Entry entry2) {
				String key1 = (String) entry1.getKey();
				String key2 = (String) entry2.getKey();
				return key1.compareTo(key2);
			}
		});

		return propertiesList;
	}

	@SuppressWarnings("rawtypes")
	private List<Map.Entry> getAllProperties() {
		JMeterContext threadContext = getThreadContext();

		List<Map.Entry> allProperties = new ArrayList<Map.Entry>();

		if (includeSamplerProperties()) {
			Sampler sampler = threadContext.getCurrentSampler();
			if (sampler != null) {
				List<Map.Entry> samplerProperties = formatPropertyIterator(threadContext
						.getCurrentSampler().propertyIterator());
				allProperties.addAll(samplerProperties);
			}
		}

		if (includeJMeterVariables()) {
			List<Map.Entry> variables = formatSet(threadContext.getVariables()
					.entrySet());
			allProperties.addAll(variables);
		}

		if (includeJMeterProperties()) {
			List<Map.Entry> properties = formatSet(JMeterUtils
					.getJMeterProperties().entrySet());
			allProperties.addAll(properties);
		}

		if (includeSystemProperties()) {
			List<Map.Entry> systemProperties = formatSet(System.getProperties()
					.entrySet());
			allProperties.addAll(systemProperties);
		}

		return allProperties;
	}

	void handleProcess() {
		synchronized (lock) {

			writeProperties(filterProperties(getAllProperties()));
		}
	}

	@SuppressWarnings("rawtypes")
	private void writeProperties(List<Map.Entry> filteredProperties) {
		Properties properties = new Properties();
		OutputStream outputStream = null;

		String filepath = getActualFilepath();

		try {

			log.info("Use default property filepath: " + useDefaultFilepath());
			log.info("Writing properties to: " + filepath);

			outputStream = new FileOutputStream(filepath);

			for (Entry entry : filteredProperties) {
				properties.setProperty(entry.getKey().toString(), entry
						.getValue().toString());
			}

			properties.store(outputStream, null);

		} catch (IOException exception) {
			log.error("Writing properties file failed: " + filepath, exception);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException exception) {
					log.error(
							"Finalizing the writing of the properties file failed: "
									+ filepath, exception);
				}
			}

		}
	}

	@Override
	public String toString() {
		return "[getFilters()=" + getFilters() + ", getFilepath()="
				+ getFilepath() + ", useDefaultFilepath()="
				+ useDefaultFilepath() + ", includeSamplerProperties()="
				+ includeSamplerProperties() + ", includeJMeterVariables()="
				+ includeJMeterVariables() + ", includeJMeterProperties()="
				+ includeJMeterProperties() + ", includeSystemProperties()="
				+ includeSystemProperties() + "]";
	}

}
