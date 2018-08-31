/*
 * Copyright (c) 2015-2016 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.pluginDevelopersToolbox;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ProjectSettingsForm implements Disposable {
    private static final Logger logger = Logger.getInstance("com.vladsch.idea.multimarkdown.settings.css");

    private JPanel myMainPanel;
    private JBCheckBox myEnabled;

    public JComponent getComponent() {
        return myMainPanel;
    }

    public ProjectSettingsForm() {

    }

    private void createUIComponents() {

    }

    public boolean isModified(@NotNull PluginDevelopersToolboxSettings settings) {
        return myEnabled.isSelected() != settings.isEnabled();
    }

    public void apply(@NotNull PluginDevelopersToolboxSettings settings) {
        settings.setEnabled(myEnabled.isSelected());
    }

    public void reset(@NotNull PluginDevelopersToolboxSettings settings) {
        myEnabled.setSelected(settings.isEnabled());
    }

    @Override
    public void dispose() {

    }
}
