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

import com.intellij.ide.BrowserUtil
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.xml.util.XmlStringUtil

object PluginNotifications {

    val NOTIFICATION_GROUP_UPDATE = NotificationGroup("PluginDevelopersToolbox Update", NotificationDisplayType.STICKY_BALLOON, true, null)
    val NOTIFICATION_GROUP_ACTION = NotificationGroup("PluginDevelopersToolbox File Action", NotificationDisplayType.BALLOON, true, null)
    val NOTIFICATION_GROUP_DEFAULT = NOTIFICATION_GROUP_ACTION

    fun applyHtmlColors(htmlText: String): String {
        val isDarkUITheme = UIUtil.isUnderDarcula()
        val enhColor = if (isDarkUITheme) "#B0A8E6" else "#6106A5"
        val buyColor = if (isDarkUITheme) "#F0A8D4" else "#C02080"
        val specialsColor = if (isDarkUITheme) "#A4EBC5" else "#04964F"
        return htmlText.replace("[[ENHANCED]]", enhColor).replace("[[BUY]]", buyColor).replace("[[SPECIALS]]", specialsColor)
    }

    fun processDashStarList(featureList: String, titleHtml: String? = null, enhAttr: String = "ENHANCED"): String {
        val features = processDashStarPage(processDashStarItems(featureList,enhAttr),titleHtml)
        return applyHtmlColors(features)
    }

    fun processDashStarPage(featuresListHtml: String, titleHtml: String? = null): String {
        val features = featuresListHtml.wrapWith((if (titleHtml != null && !titleHtml.isEmpty()) """
<h4 style="margin: 0; font-size: ${JBUI.scale(10)}px">$titleHtml</h4>""" else "")+"""
<ul style="margin-left: ${JBUI.scale(10)}px;">
""", "</ul>")
        return applyHtmlColors(features)
    }

    fun processDashStarItems(featureList: String, enhAttr: String = "ENHANCED"): String {
        //        val featureList = """
        //- Preferences now under <b>Languages & Frameworks</b>
        //- Improved preview update performance
        //- HTML Text tab now has all links as URI's
        //- Preview resolves upsource:// links to upsource server
        //- Dynamic page width setting to stylesheet preferences
        //- New Zoom factor implemented in swing browser for font size
        //* New <span style="color: [[BUY]]"><b>Split Editor</b></span> with preview and HTML Text
        //* Support for <span style="color: [[BUY]]">Open JavaFX</span> with JetBrains bundled JRE on <span style="color: [[BUY]]">OS X</span>
        //* New per file layout and preview/HTML text mode
        //* New CSS & HTML customization options
        //* Shortcuts change Layout and Preview/HTML Text modes
        //* Refactor all link address formats: relative, /, https://, file://
        //* Validates all link address formats: relative, /, https://, file://
        //* Table of Contents Markdown extension [TOC level=<i>N</i>]
        //"""
        val features = featureList.split('\n').fold("") { accum, elem ->
            val item = elem.trim()
            accum + (
                    if (item.startsWith('*')) item.removePrefix("*").trim().wrapWith("<span style=\"color: [[$enhAttr]]\">", "</span>")
                    else item.removePrefix("-").trim()
                    ).wrapWith("<li>", "</li>")
        }

        return features
    }

    fun makeNotification(message: String,
                         title: String = Bundle.message("plugin.name") + " (" + PluginProjectComponent.productVersion + ")",
                         listener: NotificationListener? = null,
                         notificationType: NotificationType = NotificationType.INFORMATION,
                         issueNotificationGroup: NotificationGroup = NOTIFICATION_GROUP_DEFAULT,
                         project: Project? = null
    ) {

        val basicListener = listener ?: NotificationListener { notification, hyperlinkEvent ->
            //notification.expire();
            if (hyperlinkEvent.url != null) {
                BrowserUtil.browse(hyperlinkEvent.url.toString())
            }
        }

        issueNotificationGroup.createNotification(title, XmlStringUtil.wrapInHtml(message), notificationType, basicListener).notify(project)
    }
}

