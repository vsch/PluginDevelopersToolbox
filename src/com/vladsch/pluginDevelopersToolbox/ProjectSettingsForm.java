// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
    private JBCheckBox myDisablePluginXmlEditorTabNameExpansion;
    private JBCheckBox myDisablePluginXmlIfSingleFile;
    private JBCheckBox myDisablePluginGradleEditorTabNameExpansion;
    private JBCheckBox myDisablePluginGradleIfSingleFile;

    public JComponent getComponent() {
        return myMainPanel;
    }

    public ProjectSettingsForm() {
        myDisablePluginXmlEditorTabNameExpansion.addActionListener(e -> myDisablePluginXmlIfSingleFile.setEnabled(myDisablePluginXmlEditorTabNameExpansion.isSelected()));
        myDisablePluginGradleEditorTabNameExpansion.addActionListener(e -> myDisablePluginGradleIfSingleFile.setEnabled(myDisablePluginGradleEditorTabNameExpansion.isSelected()));
    }

    private void createUIComponents() {

    }

    public boolean isModified(@NotNull PluginDevelopersToolboxSettings settings) {
        return myEnabled.isSelected() != settings.isEnabled()
                || myDisablePluginXmlEditorTabNameExpansion.isSelected() != settings.isDisablePluginXmlEditorTabNameExpansion()
                || myDisablePluginXmlIfSingleFile.isSelected() != settings.isDisablePluginXmlIfSingleFile()
                || myDisablePluginGradleEditorTabNameExpansion.isSelected() != settings.isDisablePluginGradleEditorTabNameExpansion()
                || myDisablePluginGradleIfSingleFile.isSelected() != settings.isDisablePluginGradleIfSingleFile()
                ;
    }

    public void apply(@NotNull PluginDevelopersToolboxSettings settings) {
        settings.setEnabled(myEnabled.isSelected());
        settings.setDisablePluginXmlEditorTabNameExpansion(myDisablePluginXmlEditorTabNameExpansion.isSelected());
        settings.setDisablePluginXmlIfSingleFile(myDisablePluginXmlIfSingleFile.isSelected());
        settings.setDisablePluginGradleEditorTabNameExpansion(myDisablePluginGradleEditorTabNameExpansion.isSelected());
        settings.setDisablePluginGradleIfSingleFile(myDisablePluginGradleIfSingleFile.isSelected());
    }

    public void reset(@NotNull PluginDevelopersToolboxSettings settings) {
        myEnabled.setSelected(settings.isEnabled());
        myDisablePluginXmlEditorTabNameExpansion.setSelected(settings.isDisablePluginXmlEditorTabNameExpansion());
        myDisablePluginXmlIfSingleFile.setSelected(settings.isDisablePluginXmlIfSingleFile());
        myDisablePluginGradleEditorTabNameExpansion.setSelected(settings.isDisablePluginGradleEditorTabNameExpansion());
        myDisablePluginGradleIfSingleFile.setSelected(settings.isDisablePluginGradleIfSingleFile());
    }

    @Override
    public void dispose() {

    }
}
