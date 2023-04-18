package com.vladsch.pluginDevelopersToolbox;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.EditorTabTitleProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisablePluginTabNameExpansionProvider implements EditorTabTitleProvider {
    @Nullable
    @Override
    public String getEditorTabTitle(@NotNull Project project, @NotNull VirtualFile file) {
        PluginDevelopersToolboxSettings toolboxSettings = PluginDevelopersToolboxSettings.getInstance(project);
        String fileName = file.getName();
        boolean disablePluginTabNameExpansion;
        boolean disablePluginTabNameExpansionIfSingle;
        
        if (fileName.equals("plugin.xml")) {
            disablePluginTabNameExpansion = toolboxSettings.isDisablePluginXmlEditorTabNameExpansion();
            disablePluginTabNameExpansionIfSingle = toolboxSettings.isDisablePluginXmlIfSingleFile();
            
        } else if (fileName.equals("settings.gradle") || fileName.equals("settings.gradle.kts") || fileName.equals("build.gradle") || fileName.equals("build.gradle.kts")) {
            disablePluginTabNameExpansion = toolboxSettings.isDisablePluginGradleEditorTabNameExpansion();
            disablePluginTabNameExpansionIfSingle = toolboxSettings.isDisablePluginGradleIfSingleFile();
        } else {
            return null;
        }

        if (!disablePluginTabNameExpansion) {
            return null;
        }

        if (disablePluginTabNameExpansionIfSingle) {
            for (VirtualFile virtualFile : FileEditorManager.getInstance(project).getOpenFiles()) {
                if (!virtualFile.equals(file) && virtualFile.getName().equals(fileName)) {
                    return null;
                }
            }
        }
        return fileName;
    }
}

