/*
 * Copyright (c) 2015-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
'use strict';

/**
 *
 * @author Ann Shumilova
 */
export class WorkspaceDataManager {
  
  /**
   * Returns the name of the pointed workspace.
   * 
   * @param workspace workspace name
   */
  getName(workspace: che.IWorkspace): string {
    if (workspace.config) {
      return workspace.config.name;
    } else if (workspace.devfile) {
      return workspace.devfile.name;
    }
  }

  /**
   * Returns the name of the pointed workspace.
   * 
   * @param workspace workspace name
   */
  setName(workspace: che.IWorkspace, name: string): void {
    if (workspace.config) {
      workspace.config.name = name;
    } else if (workspace.devfile) {
      workspace.devfile.name = name;
    }
  }

  getAttributes(workspace: che.IWorkspace): che.IWorkspaceConfigAttributes {
    if (workspace.config) {
      return workspace.config.attributes;
    } else if (workspace.devfile) {
      return workspace.devfile.attributes;
    }
  }
  
}
