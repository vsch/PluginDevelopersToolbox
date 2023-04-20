// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.pluginDevelopersToolbox;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
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
        return getForm().isModified(mySettings);
    }

    @Override
    public void apply() throws ConfigurationException {
        getForm().apply(mySettings);
    }

    @Override
    public void reset() {
        getForm().reset(mySettings);
    }

    @Override
    public void disposeUIResources() {
        if (myForm != null) {
            Disposer.dispose(myForm);
            myForm = null;
        }
    }
}
