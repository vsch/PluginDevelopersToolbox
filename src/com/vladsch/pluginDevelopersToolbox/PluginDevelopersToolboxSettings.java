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

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "PluginDevelopersToolboxSettings",
        storages = {
                @Storage(file = "plugin-developers-toolbox.xml", scheme = StorageScheme.DIRECTORY_BASED)
        })
public class PluginDevelopersToolboxSettings implements ProjectComponent, PersistentStateComponent<PluginDevelopersToolboxSettings> {
    //final private Project myProject;
    private boolean myEnabled;

    @NotNull
    public static PluginDevelopersToolboxSettings getInstance(@NotNull Project project) {
        return project.getComponent(PluginDevelopersToolboxSettings.class);
    }

    public boolean isEnabled() {
        return myEnabled;
    }

    public void setEnabled(final boolean enabled) {
        myEnabled = enabled;
    }

    public PluginDevelopersToolboxSettings(@NotNull Project project) {

    }

    public PluginDevelopersToolboxSettings() {

    }

    @Override
    public void noStateLoaded() {

    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "PluginDevelopersToolboxSettings";
    }

    @Nullable
    @Override
    public PluginDevelopersToolboxSettings getState() {
        return this;
    }

    @Override
    public void loadState(PluginDevelopersToolboxSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginDevelopersToolboxSettings settings = (PluginDevelopersToolboxSettings) o;

        return myEnabled == settings.myEnabled;
    }

    @Override
    public int hashCode() {
        return (myEnabled ? 1 : 0);
    }
}
