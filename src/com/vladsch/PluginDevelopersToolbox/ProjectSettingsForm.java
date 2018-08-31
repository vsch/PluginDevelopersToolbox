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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.table.TableCellEditor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectSettingsForm implements Disposable {
    private static final Logger logger = Logger.getInstance("com.vladsch.idea.multimarkdown.settings.css");

    private JPanel myMainPanel;
    private JBCheckBox myCssFromURIEnabled;

    public JComponent getComponent() {
        return myMainPanel;
    }

    public ProjectSettingsForm() {

        myLastPanelProviderInfo = getPanelProviderInfo();
        myCssProvidersModel = getCssProvidersModel(getPanelProvider());

        myUpdateListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myCssURI.setEnabled(myCssFromURIEnabled.isSelected());
                myCssUriSerial.setEnabled(myCssFromURIEnabled.isSelected() && PathInfo.isLocal(myCssURI.getText()));
                updateFormOnReshow(false);
            }
        };

        myCssFromURIEnabled.addActionListener(myUpdateListener);
        myCssURI.getTextEditor().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(final DocumentEvent event) {
                myCssUriSerial.setEnabled(myCssFromURIEnabled.isSelected() && PathInfo.isLocal(myCssURI.getText()));
                updateFormOnReshow(false);
            }
        });

        myApplyCustomCssText.addActionListener(myUpdateListener);

        myFeaturesOnlyAvailableInLabel.setText(MultiMarkdownBundle.message("settings.only.enhanced.feature"));
        myApplyCustomCssText.setText(MultiMarkdownBundle.message("settings.markdown.css.enable.inline"));
        myCssFromURIEnabled.setText(MultiMarkdownBundle.message("settings.markdown.css.enable.uri"));
        myCssUriSerial.setText(MultiMarkdownBundle.message("settings.markdown.css.serial.uri"));
        myCssProvidersLabel.setText(MultiMarkdownBundle.message("settings.css.css-provider.label"));
        myHtmlThemeLabel.setText(MultiMarkdownBundle.message("settings.css.scheme.label"));

        onFormCreated();
        updateOptionalSettings();
    }

    private void createUIComponents() {
        myCssEditor = createCustomizableTextFieldEditor(new CustomizableEditorTextField.EditorCustomizationListener() {
            @Override
            public boolean editorCreated(@NotNull EditorEx editor, @Nullable Project project) {
                updateFormOnReshow(false);
                return true;
            }

            @Nullable
            @Override
            public EditorHighlighter getHighlighter(Project project, @NotNull FileType fileType, @NotNull EditorColorsScheme settings) {
                return null;
            }
        }, "css");

        myHtmlThemeModel = getHtmlThemeModel();
        assert myHtmlThemeModel.getSelected() != null;

        myHtmlThemeComboBox = new ComboBox(myHtmlThemeModel);
        myHtmlThemeLastItem = myHtmlThemeModel.getSelected();
        myHtmlThemeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                final Object item = e.getItem();
                if (e.getStateChange() != ItemEvent.SELECTED || !(item instanceof MarkdownCssSettings.PreviewScheme)) {
                    return;
                }

                myHtmlThemeLastItem = (MarkdownCssSettings.PreviewScheme) item;
                updateOptionalSettings();
            }
        });

        //noinspection unchecked
        myCssProvidersModel = getCssProvidersModel(getPanelProvider());
        assert myCssProvidersModel.getSelected() != null;

        myCssProvidersComboBox = new ComboBox(myCssProvidersModel);
        myCssProviderLastItem = myCssProvidersModel.getSelected();
        myCssProvidersComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                final Object item = e.getItem();
                if (e.getStateChange() != ItemEvent.SELECTED || !(item instanceof HtmlCssResourceProvider.Info)) {
                    return;
                }

                myCssProviderLastItem = (HtmlCssResourceProvider.Info) item;
                updateOptionalSettings();
            }
        });

        myScriptProvidersModel = getScriptProvidersModel();
        myScriptsTableModel = new ScriptsTableModel(getScriptTableResources());
        myScriptsJBTable = new ScriptsTable(myScriptsTableModel);
        myScriptsJBTable.setModel(myScriptsTableModel);

        myTableJPanel = new JPanel(new BorderLayout());
        TableCellEditor cellEditor = myScriptsJBTable.getDefaultEditor(Boolean.class);
        cellEditor.addCellEditorListener(new CellEditorListener() {
            public void editingStopped(ChangeEvent e) {
                myScriptsTableModel.fireTableDataChanged();
            }

            public void editingCanceled(ChangeEvent e) {
                myScriptsTableModel.fireTableDataChanged();
            }
        });
        //myTableJPanel.add(myScriptsJBTable.getTableHeader(), BorderLayout.PAGE_START);
        myTableJPanel.add(myScriptsJBTable, BorderLayout.CENTER);
        //myTableJPanel.setMinimumSize(myScriptsJBTable.getMinimumSize());
    }

    @NotNull
    private ArrayList<ScriptResource> getScriptTableResources() {
        ArrayList<ScriptResource> scripts = new ArrayList<ScriptResource>();
        for (HtmlScriptResourceProvider.Info info : myScriptProvidersModel.getItems()) {
            HtmlScriptResourceProvider scriptResourceProvider = HtmlScriptResourceProvider.getFromId(info.getProviderId());
            if (scriptResourceProvider != null) {
                boolean forAvailable = scriptResourceProvider.getCOMPATIBILITY().isForAvailable(getPanelProvider().getCOMPATIBILITY());
                scripts.add(new ScriptResource(forAvailable, false, info));
            }
        }
        return scripts;
    }

    @Override
    protected void updatePanelProviderDependentComponents(@NotNull HtmlPanelProvider fromProvider, @NotNull HtmlPanelProvider toProvider, boolean isInitialShow) {
        MarkdownCssSettings cssSettings = getSettings();
        cssSettings = cssSettings.changeToProvider(fromProvider.getINFO(), toProvider.getINFO());
        myLastPanelProviderInfo = toProvider.getINFO();
        myScriptsTableModel.setScripts(getScriptTableResources());
        myScriptsJBTable.setModel(myScriptsTableModel);

        setCssSettings(cssSettings);
    }

    @Override
    protected void updateLicenseDependentComponents(boolean isInitialShow) {
        if (!myLicenseState()) {
            myHtmlThemeModel.setSelectedItem(myHtmlThemeModel.getItems().get(0));
            myCssProvidersModel.setSelectedItem(MarkdownCssSettings.Companion.getDefaultSettings(getPanelProvider().getINFO()).getCssProviderInfo());
            myCssFromURIEnabled.setSelected(false);
            myCssUriSerial.setSelected(false);
            myApplyCustomCssText.setSelected(false);
        }

        myMainPanel.setEnabled(myLicenseState());
        myCssFromURIEnabled.setEnabled(myLicenseState());
        myCssURI.setEnabled(myCssFromURIEnabled.isSelected() && myLicenseState());
        myCssUriSerial.setEnabled(myCssFromURIEnabled.isSelected() && myLicenseState() && PathInfo.isLocal(myCssURI.getText()));
        myApplyCustomCssText.setEnabled(myLicenseState());
        myHtmlThemeComboBox.setEnabled(myLicenseState());
        myCssProvidersComboBox.setEnabled(myLicenseState());
        updateCustomizableTextFieldEditorEditable(myCssEditor, myApplyCustomCssText.isSelected() && myLicenseState());
        myHtmlThemeLabel.setEnabled(myLicenseState());
        myCssProvidersLabel.setEnabled(myLicenseState());
        myCssURILabel.setEnabled(myLicenseState());
    }

    public void updateFormOnReshow(boolean isInitialShow) {
        final boolean canEdit = myApplyCustomCssText.isSelected();
        updateCustomizableTextFieldEditorEditable(myCssEditor, canEdit);
    }

    @Override
    protected JPanel getMainFormPanel() {
        return myMainPanel;
    }

    @NotNull
    private CollectionComboBoxModel<HtmlCssResourceProvider.Info> getCssProvidersModel(@NotNull HtmlPanelProvider provider) {
        List<HtmlCssResourceProvider.Info> cssProviderInfos = getCompatibleCssProvidersInfo(provider);
        return new CollectionComboBoxModel<HtmlCssResourceProvider.Info>(cssProviderInfos, cssProviderInfos.get(0));
    }

    void updateOptionalSettings() {
        final HtmlCssResourceProvider provider = HtmlCssResourceProvider.Companion.getFromInfoOrDefault(myCssProviderLastItem);
        myDynamicPageWidthCheckBox.setEnabled(provider.isSupportedSetting(MarkdownCssSettings.DYNAMIC_PAGE_WIDTH));
        myDynamicPageWidthLabel.setEnabled(provider.isSupportedSetting(MarkdownCssSettings.DYNAMIC_PAGE_WIDTH));
    }

    @NotNull
    private CollectionComboBoxModel<PreviewScheme> getHtmlThemeModel() {
        return new CollectionComboBoxModel<PreviewScheme>(Arrays.asList(PreviewScheme.values()));
    }

    @NotNull
    private List<HtmlCssResourceProvider.Info> getCompatibleCssProvidersInfo(@NotNull HtmlPanelProvider provider) {
        HtmlCssResourceProvider[] extensions = HtmlCssResourceProvider.Companion.getEP_NAME().getExtensions();
        return ContainerUtil.mapNotNull(extensions,
                new Function<HtmlCssResourceProvider, HtmlCssResourceProvider.Info>() {
                    @Override
                    public HtmlCssResourceProvider.Info fun(HtmlCssResourceProvider provider) {
                        if (provider.getHAS_PARENT()) {
                            // dedicated CSS to another provider, not for generic panel use
                            return null;
                        }
                        if (!provider.getCOMPATIBILITY().isForAvailable(provider.getCOMPATIBILITY())) {
                            // not compatible with current browser
                            return null;
                        }
                        return provider.getINFO();
                    }
                });
    }

    @NotNull
    private CollectionListModel<HtmlScriptResourceProvider.Info> getScriptProvidersModel() {
        List<HtmlScriptResourceProvider.Info> scriptProvidersInfo = getScriptProvidersInfo();
        return new CollectionListModel<HtmlScriptResourceProvider.Info>(scriptProvidersInfo);
    }

    @NotNull
    private List<HtmlScriptResourceProvider.Info> getScriptProvidersInfo() {
        HtmlScriptResourceProvider[] extensions = HtmlScriptResourceProvider.Companion.getEP_NAME().getExtensions();
        return ContainerUtil.mapNotNull(extensions,
                new Function<HtmlScriptResourceProvider, HtmlScriptResourceProvider.Info>() {

                    @Override
                    public HtmlScriptResourceProvider.Info fun(HtmlScriptResourceProvider provider) {
                        if (provider.getHAS_PARENT()) {
                            // dedicated Script to another provider, not for generic panel use
                            return null;
                        }
                        return provider.getINFO();
                    }
                });
    }

    String ensureTrailingBlankLines(String text, int blankLines) {
        int trailingBlankLines = 0;

        for (int i = text.length(); i-- > 0; ) {
            char c = text.charAt(i);

            if (c == '\n') {
                if (++trailingBlankLines > blankLines) break;
            } else if (c != ' ' && c != '\t') {
                if (trailingBlankLines > 0) trailingBlankLines--;
                break;
            }
        }

        if (trailingBlankLines < blankLines) {
            // add difference
            return text + RepeatedCharSequence.of('\n', blankLines - trailingBlankLines);
        }
        return text;
    }

    @Override
    protected void resetToRenderingProfile(@NotNull final MarkdownRenderingProfile renderingProfile) {
        setCssSettings(renderingProfile.getCssSettings());
    }

    @Override
    protected void onCssSettingsChanged(@NotNull final MarkdownCssSettings cssSettings) {
        setCssSettings(cssSettings);
    }

    @Override
    public void setCssSettings(@NotNull MarkdownCssSettings settings) {
        MarkdownCssSettings cssSettings = settings;

        myCssFromURIEnabled.setSelected(cssSettings.isCssUriEnabled());
        myCssUriSerial.setSelected(cssSettings.isCssUriSerial());
        myCssURI.setHistory(settings.getCssUriHistoryList());
        myCssURI.setTextAndAddToHistory(cssSettings.getCssUri());
        myApplyCustomCssText.setSelected(cssSettings.isCssTextEnabled());
        updateCustomizableTextFieldEditorText(myCssEditor, ensureTrailingBlankLines(cssSettings.getCssText(), 1));
        myDynamicPageWidthCheckBox.setSelected(cssSettings.isDynamicPageWidth());

        myHtmlThemeLastItem = cssSettings.getPreviewScheme();
        myHtmlThemeModel.setSelectedItem(myHtmlThemeLastItem);

        // IMPORTANT: these are needed for IDEA 14 to repaint
        myHtmlThemeComboBox.setSelectedItem(myHtmlThemeLastItem);
        myHtmlThemeComboBox.repaint();

        myCssProviderLastItem = cssSettings.getCssProviderInfo();
        myCssProvidersModel.setSelectedItem(myCssProviderLastItem);

        // IMPORTANT: these are needed for IDEA 14 to repaint
        myCssProvidersComboBox.setSelectedItem(myCssProviderLastItem);
        myCssProvidersComboBox.repaint();

        myScriptsTableModel.setSelected(cssSettings.getHtmlScriptProvidersInfo());

        if (!myInitialShow) {
            // if already initialized then we update, otherwise the first show will do it
            ApplicationManager.getApplication().invokeLater(() -> updateFormOnReshow(false));
        }

        updateOptionalSettings();
        myUpdateListener.actionPerformed(null);
    }

    @NotNull
    public PluginDevelopersToolboxSettings getSettings() {
        //logger.info("PreviewSettingsForm:getSettings profilePreviewProvider " + myRenderingProfile().getPreviewSettings().getHtmlPanelProviderInfo().getName());

        ArrayList<HtmlScriptResourceProvider.Info> info = myScriptsTableModel.getEnabledScripts();
        return new MarkdownCssSettings(
                myHtmlThemeLastItem,
                myCssProviderLastItem,
                info,
                myCssFromURIEnabled.isSelected(),
                myCssUriSerial.isSelected(),
                myCssURI.getText(),
                myApplyCustomCssText.isSelected(),
                getCustomizableTextFiledEditorText(myCssEditor),
                myDynamicPageWidthCheckBox.isSelected(),
                myCssURI.getHistory()
        );
    }

    @Override
    public void dispose() {
        myCssEditor = null;
    }
}
