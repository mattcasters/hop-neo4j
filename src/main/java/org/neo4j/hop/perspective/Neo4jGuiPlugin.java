/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.neo4j.hop.perspective;

import org.apache.hop.core.gui.plugin.GuiElementType;
import org.apache.hop.core.gui.plugin.GuiMenuElement;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.core.gui.plugin.GuiToolbarElement;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metastore.persist.MetaStoreFactory;
import org.apache.hop.ui.core.dialog.EnterSelectionDialog;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.hopgui.HopGui;
import org.neo4j.hop.core.Neo4jDefaults;
import org.neo4j.hop.model.GraphModel;
import org.neo4j.hop.model.GraphModelUtils;
import org.neo4j.hop.shared.NeoConnection;
import org.neo4j.hop.shared.NeoConnectionUtils;

import java.util.Collections;
import java.util.List;

@GuiPlugin(
  description = "This class adds Neo4j specific GUI elements to the Hop GUI"
)
public class Neo4jGuiPlugin {
  protected static Class<?> PKG = Neo4jGuiPlugin.class; // for i18n

  public static final String ID_MAIN_MENU_NEO4J = "50000-menu-neo4j";
  public static final String ID_MAIN_MENU_NEO4J_EDIT_CONNECTION = "50100-menu-neo4j-edit-connection";
  public static final String ID_MAIN_MENU_NEO4J_DELETE_CONNECTION = "50150-menu-neo4j-delete-connection";
  public static final String ID_MAIN_MENU_NEO4J_EDIT_MODEL = "50200-menu-neo4j-edit-model";
  public static final String ID_MAIN_MENU_NEO4J_DELETE_MODEL = "50250-menu-neo4j-edit-model";

  public static final String ID_MAIN_TOOLBAR_NEO4J_EDIT_CONNECTION = "toolbar-20000-neo4j-edit-connection";

  private static Neo4jGuiPlugin instance = null;

  private HopGui spoon;
  private MetaStoreFactory<NeoConnection> connectionFactory;
  private MetaStoreFactory<GraphModel> modelFactory;
  private IVariables space;

  private Neo4jGuiPlugin() {
    spoon = HopGui.getInstance();
  }

  public static Neo4jGuiPlugin getInstance() {
    if ( instance == null ) {
      instance = new Neo4jGuiPlugin(); ;
      instance.space = HopGui.getInstance().getVariables();
      instance.connectionFactory = new MetaStoreFactory<>( NeoConnection.class, instance.spoon.getMetaStore(), Neo4jDefaults.NAMESPACE );
      instance.modelFactory = new MetaStoreFactory<>( GraphModel.class, instance.spoon.getMetaStore(), Neo4jDefaults.NAMESPACE );
    }
    return instance;
  }

  @GuiMenuElement( id = ID_MAIN_MENU_NEO4J, type = GuiElementType.MENU_ITEM, label = "Neo4j", parentId = HopGui.ID_MAIN_MENU )
  public void menuNeo4j() {
    // Nothing is done here.
  }

  @GuiMenuElement( id = ID_MAIN_MENU_NEO4J_EDIT_CONNECTION, type = GuiElementType.MENU_ITEM, label = "Edit Connection", image = "neo4j_logo.svg", parentId = ID_MAIN_MENU_NEO4J )
  @GuiToolbarElement( id = ID_MAIN_TOOLBAR_NEO4J_EDIT_CONNECTION, type = GuiElementType.TOOLBAR_BUTTON, image = "neo4j_logo.svg", toolTip = "New", parentId = HopGui.ID_MAIN_TOOLBAR )
  public void menuNeo4jEditConnection() {
    try {
      List<String> elementNames = connectionFactory.getElementNames();
      Collections.sort( elementNames );
      String[] names = elementNames.toArray( new String[ 0 ] );

      EnterSelectionDialog dialog = new EnterSelectionDialog( spoon.getShell(), names, "Edit Neo4j connection", "Select the connection to edit" );
      String choice = dialog.open();
      if ( choice != null ) {
        NeoConnectionUtils.editConnection( spoon.getShell(), space, connectionFactory, choice );
      }
    } catch ( Exception e ) {
      new ErrorDialog( spoon.getShell(), "Error", "Error editing Neo4j connection", e );
    }
  }

  @GuiMenuElement( id = ID_MAIN_MENU_NEO4J_DELETE_CONNECTION, type = GuiElementType.MENU_ITEM, label = "Delete Connection", image = "neo4j_logo.svg", parentId = ID_MAIN_MENU_NEO4J )
  public void deleteConnection() {
    try {
      List<String> elementNames = connectionFactory.getElementNames();
      Collections.sort( elementNames );
      String[] names = elementNames.toArray( new String[ 0 ] );

      EnterSelectionDialog dialog = new EnterSelectionDialog( spoon.getShell(), names, "Delete Neo4j connection", "Select the connection to delete" );
      String choice = dialog.open();
      if ( choice != null ) {
        NeoConnectionUtils.deleteConnection( spoon.getShell(), connectionFactory, choice );
      }
    } catch ( Exception e ) {
      new ErrorDialog( spoon.getShell(), "Error", "Error deleting Neo4j connection", e );
    }
  }


  @GuiMenuElement( id = ID_MAIN_MENU_NEO4J_EDIT_MODEL,
    type = GuiElementType.MENU_ITEM,
    label = "Edit Graph Model",
    image = "neo4j_logo.svg",
    parentId = ID_MAIN_MENU_NEO4J,
    separator = true
  )
  public void editModel() {
    try {
      List<String> elementNames = modelFactory.getElementNames();
      Collections.sort( elementNames );
      String[] names = elementNames.toArray( new String[ 0 ] );

      EnterSelectionDialog dialog = new EnterSelectionDialog( spoon.getShell(), names, "Edit Neo4j model", "Select the graph model to edit" );
      String choice = dialog.open();
      if ( choice != null ) {
        GraphModelUtils.editModel( spoon.getShell(), modelFactory, choice, null );
      }
    } catch ( Exception e ) {
      new ErrorDialog( spoon.getShell(), "Error", "Error editing Neo4j graph model", e );
    }
  }

  @GuiMenuElement( id = ID_MAIN_MENU_NEO4J_DELETE_MODEL,
    type = GuiElementType.MENU_ITEM,
    label = "Delete Graph Model",
    image = "neo4j_logo.svg",
    parentId = ID_MAIN_MENU_NEO4J
  )
  public void deleteModel() {
    try {
      List<String> elementNames = modelFactory.getElementNames();
      Collections.sort( elementNames );
      String[] names = elementNames.toArray( new String[ 0 ] );

      EnterSelectionDialog dialog = new EnterSelectionDialog( spoon.getShell(), names, "Delete Neo4j model", "Select the graph model to delete" );
      String choice = dialog.open();
      if ( choice != null ) {
        GraphModelUtils.deleteModel( spoon.getShell(), modelFactory, choice );
      }
    } catch ( Exception e ) {
      new ErrorDialog( spoon.getShell(), "Error", "Error deleting Neo4j graph model", e );
    }
  }
}
