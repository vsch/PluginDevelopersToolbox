// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.pluginDevelopersToolbox;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "PluginDevelopersToolboxSettings",
        storages = {
                @Storage("plugin-developers-toolbox.xml")
        })
public class PluginDevelopersToolboxSettings implements PersistentStateComponent<PluginDevelopersToolboxSettings>, Disposable {
    //final private Project myProject;
    private boolean myEnabled;
    private boolean myDisablePluginXmlEditorTabNameExpansion;
    private boolean myDisablePluginXmlIfSingleFile;
    private boolean myDisablePluginGradleEditorTabNameExpansion;
    private boolean myDisablePluginGradleIfSingleFile;

    @NotNull
    public static PluginDevelopersToolboxSettings getInstance(@NotNull Project project) {
        return project.getService(PluginDevelopersToolboxSettings.class);
    }

    public boolean isEnabled() {
        return myEnabled;
    }

    public void setEnabled(final boolean enabled) {
        myEnabled = enabled;
    }

    public PluginDevelopersToolboxSettings(@NotNull Project project) {

    }

    public boolean isDisablePluginXmlEditorTabNameExpansion() {
        return myDisablePluginXmlEditorTabNameExpansion;
    }

    public void setDisablePluginXmlEditorTabNameExpansion(boolean disablePluginXmlEditorTabNameExpansion) {
        myDisablePluginXmlEditorTabNameExpansion = disablePluginXmlEditorTabNameExpansion;
    }

    public boolean isDisablePluginXmlIfSingleFile() {
        return myDisablePluginXmlIfSingleFile;
    }

    public void setDisablePluginXmlIfSingleFile(boolean disablePluginXmlIfSingleFile) {
        myDisablePluginXmlIfSingleFile = disablePluginXmlIfSingleFile;
    }

    public boolean isDisablePluginGradleEditorTabNameExpansion() {
        return myDisablePluginGradleEditorTabNameExpansion;
    }

    public void setDisablePluginGradleEditorTabNameExpansion(boolean disablePluginGradleEditorTabNameExpansion) {
        myDisablePluginGradleEditorTabNameExpansion = disablePluginGradleEditorTabNameExpansion;
    }

    public boolean isDisablePluginGradleIfSingleFile() {
        return myDisablePluginGradleIfSingleFile;
    }

    public void setDisablePluginGradleIfSingleFile(boolean disablePluginGradleIfSingleFile) {
        myDisablePluginGradleIfSingleFile = disablePluginGradleIfSingleFile;
    }

    public PluginDevelopersToolboxSettings() {

    }

    @Override
    public void dispose() {

    }

    @Nullable
    @Override
    public PluginDevelopersToolboxSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginDevelopersToolboxSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginDevelopersToolboxSettings settings = (PluginDevelopersToolboxSettings) o;

        return myEnabled == settings.myEnabled
                && myDisablePluginXmlEditorTabNameExpansion == settings.myDisablePluginXmlEditorTabNameExpansion
                && myDisablePluginXmlIfSingleFile == settings.myDisablePluginXmlIfSingleFile;
    }

    @Override
    public int hashCode() {
        return (myEnabled ? 31 * 31 : 0) + (myDisablePluginXmlEditorTabNameExpansion ? 31 : 0) + (myDisablePluginXmlIfSingleFile ? 31 : 0);
    }
}
