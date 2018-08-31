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
package com.vladsch.PluginDevelopersToolbox;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class ProjectConfigurable implements SearchableConfigurable {
    @NotNull final protected Project myProject;
    @Nullable private ProjectSettingsForm myForm = null;
    @Nullable final private PluginDevelopersToolboxSettings mySettings;

    private ProjectConfigurable(@NotNull Project project) {
        this.myProject = project;
        this.mySettings = PluginDevelopersToolboxSettings.getInstance(project);
    }





    @NotNull
    @Override
    public String getId() {
        return "PluginDevelopersToolbox.ProjectSettings";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        //final ProjectSettingsForm form = getForm();
        //final Runnable runnable = form.enableSearch(option);
        //if (runnable != null) {
        //    return new Runnable() {
        //        @Override
        //        public void run() {
        //            form.getComponent().show();
        //            runnable.run();
        //        }
        //    };
        //}
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return Bundle.message("settings.project.name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null; //"com.vladsch.markdown.navigator.settings.stylesheet";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        return getForm().getComponent();
    }

    @NotNull
    public ProjectSettingsForm getForm() {
        if (myForm == null) {
            myForm = new ProjectSettingsForm();
        }
        return myForm;
    }

    @Override
    public boolean isModified() {
        return !getForm().getSettings().equals(mySettings);
    }

    @Override
    public void apply() throws ConfigurationException {
        myProfileSynchronizer.setCssSettings(getForm().getSettings());
        myProfileSynchronizer.apply();
    }

    @Override
    public void reset() {
        getForm().setCssSettings(getProfileForReset().getCssSettings());
    }

    @Override
    public void disposeUIResources() {
        if (myForm != null) {
            Disposer.dispose(myForm);
            myForm = null;
            myProfileSynchronizer.reset();
        }
    }

    protected MarkdownRenderingProfileHolder getProfileForReset() {
        if (myFirstReset) {
            myFirstReset = false;
            return myProfileSynchronizer.getRenderingProfile();
        } else {
            return myProfileSynchronizer.getRenderingProfileHolder();
        }
    }
}
