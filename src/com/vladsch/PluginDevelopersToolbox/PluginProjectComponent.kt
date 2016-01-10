/*
 * Copyright (c) 2015-2016 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vladsch.PluginDevelopersToolbox

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.*
import com.intellij.psi.PsiManager
import com.intellij.util.Alarm
import com.vladsch.PluginDevelopersToolbox.Bundle
import org.apache.log4j.Logger
import org.jetbrains.annotations.NonNls

import java.io.IOException
import java.util.regex.Pattern

class PluginProjectComponent(val myProject: Project) : ProjectComponent, VirtualFileListener, Disposable, Runnable {

    private val mySwingAlarm = Alarm(Alarm.ThreadToUse.SWING_THREAD, this)
    private val REQUESTS_LOCK = Object()
    private var myLastRequest: Runnable? = null
    private val myNotifyList = StringBuilder()
    private val myNotifyMessage = StringBuilder()
    private var myLastParentItem: String? = null

    override fun dispose() {

    }

    override fun projectOpened() {
        VirtualFileManager.getInstance().addVirtualFileListener(this)
    }

    override fun projectClosed() {
        VirtualFileManager.getInstance().removeVirtualFileListener(this)
    }

    @NonNls
    override fun getComponentName(): String {
        return this.javaClass.name
    }

    override fun initComponent() {

    }

    override fun disposeComponent() {

    }

    override fun run() {
        var message: String = "";
        synchronized (REQUESTS_LOCK) {
            myLastRequest = null

            if (myLastParentItem != null) {
                closeParentItem()
            }

            myNotifyMessage.append(PluginNotifications.processDashStarItems(myNotifyList.toString()))
            myNotifyList.delete(0, myNotifyList.length)

            message = myNotifyMessage.toString();
            //            println(message)
            myNotifyMessage.delete(0, myNotifyMessage.length)
        }

        val messageHtml = PluginNotifications.processDashStarPage(message, Bundle.message("plugin.action.files-moved.title"))
        //        println(messageHtml)
        PluginNotifications.makeNotification(messageHtml, project = this.myProject)
    }

    private fun openParentItem(parentItem: String?) {
        myNotifyMessage.append(PluginNotifications.processDashStarItems(myNotifyList.toString()))
        myNotifyMessage.appendln("\n<li>$parentItem\n<ul style=\"margin-top: 0px;\">")
        myNotifyList.delete(0, myNotifyList.length)
        myLastParentItem = parentItem
    }

    private fun closeParentItem() {
        myNotifyMessage.append(PluginNotifications.processDashStarItems(myNotifyList.toString()))
        myNotifyMessage.appendln("\n</ul></li>")
        myNotifyList.delete(0, myNotifyList.length)
        myLastParentItem = null
    }

    fun addNotificationItem(item: String, parentItem: String?) {
        synchronized (REQUESTS_LOCK) {
            val lastRequest = myLastRequest
            if (lastRequest != null) {
                mySwingAlarm.cancelRequest(lastRequest)
            }

            if (myLastParentItem != null && (parentItem == null || parentItem != myLastParentItem)) {
                closeParentItem()
            }

            if (myLastParentItem == null && parentItem != null) {
                openParentItem(parentItem)
            }

            myNotifyList.appendln(item)

            val nextRequest: Runnable = this
            myLastRequest = nextRequest
            mySwingAlarm.addRequest(nextRequest, 100, ModalityState.any())
        }
    }

    // detect creation of files with the format: (.*)_dark@2x\.(.*) and rename the file to $1@2x_dark.$2
    override fun propertyChanged(event: VirtualFilePropertyEvent) {
        //updateHighlighters();
    }

    override fun contentsChanged(event: VirtualFileEvent) {
        //updateHighlighters();
    }

    fun invokeLaterInWriteAction(runnable:()->Unit) {
        ApplicationManager.getApplication().invokeLater() {
            WriteCommandAction.runWriteCommandAction(myProject, runnable)
        }
    }

    override fun fileCreated(event: VirtualFileEvent) {
        val parent = event.parent ?: return
        val file = event.file
        if (!event.isFromRefresh || !file.isValid || PsiManager.getInstance(myProject).findDirectory(parent) == null) return // not in our project

        if (!file.isDirectory && file.extension in IMAGE_EXTENSIONS) {
            invokeLaterInWriteAction() {
                if (file.isValid && parent.isValid) {
                    processSlicyFile(file, parent, parent.name + "/")
                }
            }
        } else if (file.isDirectory && file.extension == "+") {
            // take the directory name  (less extension) add file name by stripping the leading - in file name and put it into the parent
            // directory while deleting this directory
            invokeLaterInWriteAction() {
                if (file.isValid && parent.isValid) {
                    processSlicyDirectory(file, parent, parent.name + "/")
                }
            }
        } else if (file.isDirectory) {
            // see if there are any child directories matching the Slicy directory splicing pattern
            invokeLaterInWriteAction() {
                if (file.isValid && parent.isValid) {
                    for (subDir in file.children) {
                        if (subDir.isValid && subDir.isDirectory && subDir.extension == "+") {
                            processSlicyDirectory(subDir, file, parent.name + "/" + file.name + "/")
                        }
                    }
                }
            }
        }
    }

    private fun processSlicyDirectory(directory: VirtualFile, parent: VirtualFile, parentItem: String?) {
        val namePrefix = directory.nameWithoutExtension
        var allProcessed = true

        for (file in directory.children) {
            var fileProcessed = false
            if (!file.isDirectory && file.extension in IMAGE_EXTENSIONS) {
                // our candidate
                var removedPrefix = file.nameWithoutExtension.removePrefix("+")
                if (removedPrefix == "_dark@2x") removedPrefix = "@2x_dark"
                val newName = namePrefix + removedPrefix + '.' + file.extension
                val virtualFile = parent.findChild(newName)

                if (virtualFile != null && virtualFile.exists()) {
                    try {
                        virtualFile.delete(this)
                    } catch (e: IOException) {
                        addNotificationItem(Bundle.message("plugin.action.file-delete-failed", newName), parentItem)
                        e.printStackTrace()
                    }
                }

                try {
                    file.copy(this, parent, newName)
                    try {
                        file.delete(this)
                        fileProcessed = true
                        addNotificationItem(Bundle.message("plugin.action.file-processed", newName), parentItem)
                    } catch (e: IOException) {
                        addNotificationItem(Bundle.message("plugin.action.file-delete-failed", newName), parentItem)
                        e.printStackTrace()
                    }
                } catch (e: IOException) {
                    addNotificationItem(Bundle.message("plugin.action.file-copy-failed", file.name, newName), parentItem)
                }
            }

            if (!fileProcessed) {
                allProcessed = false
            }
        }

        if (allProcessed) {
            // remove the directory
            try {
                directory.delete(this)
            } catch (e: IOException) {
                addNotificationItem(Bundle.message("plugin.action.file-delete-failed", directory.name), parentItem)
                allProcessed = false
                e.printStackTrace()
            }
        }

        if (!allProcessed) {
            addNotificationItem(Bundle.message("plugin.action.file-delete-not-done", directory.name), parentItem)
        }
    }

    private fun processSlicyFile(file: VirtualFile, parent: VirtualFile, parentItem: String?) {
        val pattern = Pattern.compile("^(.+)_dark@2x\\.(.*)$")
        val matcher = pattern.matcher(file.name)
        if (matcher.matches()) {
            val matchResult = matcher.toMatchResult()
            if (matchResult.groupCount() == 2) {
                val prefix = matchResult.group(1)
                val extension = matchResult.group(2)
                if (prefix != null && extension != null) {
                    // matched, move it by first deleting the old file and renaming this one
                    val newName = "$prefix@2x_dark.$extension"
                    val virtualFile = parent.findChild(newName)

                    if (virtualFile != null && virtualFile.exists()) {
                        try {
                            virtualFile.delete(this)
                        } catch (e: IOException) {
                            addNotificationItem(Bundle.message("plugin.action.file-delete-failed", newName), parentItem)
                            e.printStackTrace()
                        }
                    }

                    try {
                        file.rename(this, newName)
                        addNotificationItem(Bundle.message("plugin.action.file-processed", file.name), parentItem)
                    } catch (e: IOException) {
                        addNotificationItem(Bundle.message("plugin.action.file-rename-failed", file.name, newName), parentItem)
                    }
                }
            }
        }
    }

    override fun fileDeleted(event: VirtualFileEvent) {
        //updateHighlighters();
    }

    override fun fileMoved(event: VirtualFileMoveEvent) {
        //updateHighlighters();
    }

    override fun fileCopied(event: VirtualFileCopyEvent) {
        //updateHighlighters();
    }

    override fun beforePropertyChange(event: VirtualFilePropertyEvent) {
        //String s = event.getPropertyName();
        //int tmp = 0;
    }

    override fun beforeContentsChange(event: VirtualFileEvent) {
        //int tmp = 0;
    }

    override fun beforeFileDeletion(event: VirtualFileEvent) {
        //int tmp = 0;
    }

    override fun beforeFileMovement(event: VirtualFileMoveEvent) {
        //int tmp = 0;
    }

    companion object {
        @JvmStatic @JvmField val IMAGE_EXTENSIONS = arrayOf("png", "jpg", "jpeg", "gif")
        private val logger = Logger.getLogger(PluginProjectComponent::class.java)
    }
}
