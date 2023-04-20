// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.pluginDevelopersToolbox

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class PluginApplicationComponent() : ProjectManagerListener, Disposable {

    override fun dispose() {
    }

    override fun projectOpened(project: Project) {
        project.getService(PluginProjectComponent::class.java);
    }

    override fun projectClosing(project: Project) {
        val projectComponent: PluginProjectComponent = project.getService(PluginProjectComponent::class.java);
        projectComponent.dispose()
    }
}
