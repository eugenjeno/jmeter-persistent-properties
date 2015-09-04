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

import java.util.Arrays;
import java.util.Collection;

import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.util.MenuFactory;

import bei.jmeter.persistent.properties.AbstractPersistentProperties;
import bei.jmeter.persistent.properties.ReadProperties;

public final class ReadPropertiesGui extends AbstractPersistentPropertiesGui {

	private static final long serialVersionUID = 0L;

	@Override
    public JPopupMenu createPopupMenu() {
        return MenuFactory.getDefaultExtractorMenu();
    }

    @Override
    public Collection<String> getMenuCategories() {
        return Arrays.asList(MenuFactory.PRE_PROCESSORS);
    }
    
    @Override
    AbstractPersistentProperties getPersistentUserParametersInstance() {
    	return new ReadProperties();
    }

    @Override
    String getTitle() {
    	return "Read properties from file"; // $NON-NLS-1$
    }
}
