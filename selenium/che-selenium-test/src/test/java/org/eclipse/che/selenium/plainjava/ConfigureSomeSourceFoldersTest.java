/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.selenium.plainjava;

import static org.eclipse.che.selenium.core.constant.TestIntelligentCommandsConstants.CommandsDefaultNames.JAVA_NAME;
import static org.eclipse.che.selenium.core.constant.TestIntelligentCommandsConstants.CommandsGoals.RUN_GOAL;
import static org.eclipse.che.selenium.core.constant.TestIntelligentCommandsConstants.CommandsTypes.JAVA_TYPE;
import static org.eclipse.che.selenium.core.constant.TestProjectExplorerContextMenuConstants.NEW;
import static org.eclipse.che.selenium.core.constant.TestProjectExplorerContextMenuConstants.SubMenuNew.JAVA_CLASS;
import static org.eclipse.che.selenium.pageobject.AskForValueDialog.JavaFiles.CLASS;
import static org.eclipse.che.selenium.pageobject.CodenvyEditor.MarkersType.ERROR_MARKER;

import com.google.inject.Inject;
import java.net.URL;
import java.nio.file.Paths;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.client.TestProjectServiceClient;
import org.eclipse.che.selenium.core.constant.TestProjectExplorerContextMenuConstants;
import org.eclipse.che.selenium.core.project.ProjectTemplates;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.AskForValueDialog;
import org.eclipse.che.selenium.pageobject.CodenvyEditor;
import org.eclipse.che.selenium.pageobject.Consoles;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.Loader;
import org.eclipse.che.selenium.pageobject.NotificationsPopupPanel;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.eclipse.che.selenium.pageobject.intelligent.CommandsEditor;
import org.eclipse.che.selenium.pageobject.intelligent.CommandsExplorer;
import org.openqa.selenium.Keys;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Musienko Maxim
 * @author Aleksandr Shmaraiev
 */
public class ConfigureSomeSourceFoldersTest {
  private static final String PROJECT_NAME = NameGenerator.generate("PlainJava-", 4);
  private String newJavaClassName = "NewClass";

  @Inject private TestWorkspace ws;
  @Inject private Ide ide;
  @Inject private ProjectExplorer projectExplorer;
  @Inject private CodenvyEditor codenvyEditor;
  @Inject private Loader loader;
  @Inject private AskForValueDialog askForValueDialog;
  @Inject private CommandsExplorer commandsExplorer;
  @Inject private CommandsEditor commandsEditor;
  @Inject private Consoles consoles;
  @Inject private TestProjectServiceClient testProjectServiceClient;
  @Inject private NotificationsPopupPanel notificationsPopupPanel;

  @BeforeClass
  public void prepare() throws Exception {
    URL resource = getClass().getResource("/projects/java-project-with-additional-source-folder");
    testProjectServiceClient.importProject(
        ws.getId(), Paths.get(resource.toURI()), PROJECT_NAME, ProjectTemplates.PLAIN_JAVA);
    ide.open(ws);
  }

  @Test
  public void checkConfigureClasspathPlainJavaProject() {
    projectExplorer.waitProjectExplorer();
    projectExplorer.waitItem(PROJECT_NAME);
    projectExplorer.openItemByPath(PROJECT_NAME);
    projectExplorer.openContextMenuByPathSelectedItem(PROJECT_NAME + "/source");
    projectExplorer.clickOnItemInContextMenu(TestProjectExplorerContextMenuConstants.BUILD_PATH);
    projectExplorer.clickOnItemInContextMenu(
        TestProjectExplorerContextMenuConstants.SubMenuBuildPath.USE_AS_SOURCE_FOLDER);
    projectExplorer.waitFolderDefinedTypeOfFolderByPath(
        PROJECT_NAME + "/source", ProjectExplorer.FolderTypes.JAVA_SOURCE_FOLDER);
    projectExplorer.waitFolderDefinedTypeOfFolderByPath(
        PROJECT_NAME + "/src", ProjectExplorer.FolderTypes.JAVA_SOURCE_FOLDER);
    projectExplorer.openContextMenuByPathSelectedItem(PROJECT_NAME + "/source");
    createNewJavaClass(newJavaClassName);
    projectExplorer.waitItem(PROJECT_NAME + "/source/" + newJavaClassName + ".java");
    codenvyEditor.waitTextIntoEditor("public class NewClass {");
    codenvyEditor.waitAllMarkersDisappear(ERROR_MARKER);
    codenvyEditor.setCursorToDefinedLineAndChar(2, 24);
    codenvyEditor.typeTextIntoEditor(Keys.ENTER.toString());
    String methodForChecking =
        " public static String typeCheckMess(){\n"
            + "        return \"Message from source folder\";\n"
            + "    ";
    codenvyEditor.typeTextIntoEditor(methodForChecking);
    codenvyEditor.waitAllMarkersDisappear(ERROR_MARKER);
    projectExplorer.openItemByPath(PROJECT_NAME + "/src");
    projectExplorer.waitItem(PROJECT_NAME + "/src/Main.java");
    projectExplorer.openItemByPath(PROJECT_NAME + "/src/Main.java");
    codenvyEditor.waitTabFileWithSavedStatus("Main");
    launchMainClassFromCommandWidget();
    consoles.waitExpectedTextIntoConsole("Message from source", 15);
  }

  private void createNewJavaClass(String name) {
    projectExplorer.clickOnItemInContextMenu(NEW);
    projectExplorer.clickOnNewContextMenuItem(JAVA_CLASS);
    askForValueDialog.createJavaFileByNameAndType(name, CLASS);
    projectExplorer.waitItemInVisibleArea(name + ".java");
    codenvyEditor.waitActiveEditor();
    loader.waitOnClosed();
    codenvyEditor.waitTabIsPresent(name);
  }

  private void launchMainClassFromCommandWidget() {
    commandsExplorer.openCommandsExplorer();
    commandsExplorer.waitCommandExplorerIsOpened();
    commandsExplorer.clickAddCommandButton(RUN_GOAL);
    commandsExplorer.chooseCommandTypeInContextMenu(JAVA_TYPE);
    commandsExplorer.checkCommandIsPresentInGoal(RUN_GOAL, JAVA_NAME);
    commandsEditor.waitTabFileWithSavedStatus(JAVA_NAME);
    notificationsPopupPanel.waitProgressPopupPanelClose(
        20); // TODO it is a workaround: https://github.com/eclipse/che/issues/6734, delete after
             // resolve
    commandsEditor.clickOnRunButton();
  }
}
