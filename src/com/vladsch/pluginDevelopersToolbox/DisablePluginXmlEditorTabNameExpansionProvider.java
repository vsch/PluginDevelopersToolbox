package com.vladsch.pluginDevelopersToolbox;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.EditorTabTitleProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisablePluginXmlEditorTabNameExpansionProvider implements EditorTabTitleProvider {
    @Nullable
    @Override
    public String getEditorTabTitle(@NotNull Project project, @NotNull VirtualFile file) {
        PluginDevelopersToolboxSettings toolboxSettings = PluginDevelopersToolboxSettings.getInstance(project);
        if (!toolboxSettings.isDisablePluginXmlEditorTabNameExpansion()) {
            return null;
        }

        if (toolboxSettings.isDisablePluginXmlIfSingleFile()) {
            for (VirtualFile virtualFile : FileEditorManager.getInstance(project).getOpenFiles()) {
                if (!virtualFile.equals(file) && virtualFile.getName().equals("plugin.xml")) {
                    return null;
                }
            }
        }

        return file.getName();
    }
}

