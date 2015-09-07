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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.threads.JMeterVariables;

public final class ReadProperties extends AbstractPersistentProperties
		implements PreProcessor {

	private static final long serialVersionUID = 0L;

	@Override
	public void process() {
		handleProcess();
	}

	@SuppressWarnings("rawtypes")
	void handleProcess() {
		synchronized (lock) {

			List<Map.Entry> filteredProperties = filterProperties(readProperties());

			JMeterVariables properties = getThreadContext().getVariables();

			for (Map.Entry entry : filteredProperties) {
				String name = entry.getKey().toString();
				String value = entry.getValue().toString();

				properties.put(name, value);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private List<Map.Entry> readProperties() {

		Properties properties = new Properties();
		InputStream inputStream = null;

		String filepath = getActualFilepath();

		List<Map.Entry> readProperties = new ArrayList<Map.Entry>();
		try {

			inputStream = new FileInputStream(filepath);

			properties.load(inputStream);

			readProperties.addAll(properties.entrySet());

		} catch (IOException exception) {
			log.error("Reading property file failed: " + filepath, exception);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException exception) {
					log.error(
							"Finalizing the reading of the properties file failed: "
									+ filepath, exception);
				}
			}
		}
		
		return readProperties;
	}

}
