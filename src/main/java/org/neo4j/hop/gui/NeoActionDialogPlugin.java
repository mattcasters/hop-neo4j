package org.neo4j.hop.gui;

import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.core.gui.plugin.toolbar.GuiToolbarElement;
import org.apache.hop.ui.core.dialog.ContextDialog;

@GuiPlugin
public class NeoActionDialogPlugin {

  public static final String TOOLBAR_ITEM_FILTER_NEO4J_ITEMS = "ContextDialog-Toolbar-10100-FilterNeo4jItems";


  /**
   * Filters out the Neo4j actions
   */
  @GuiToolbarElement(
    root = ContextDialog.GUI_PLUGIN_TOOLBAR_PARENT_ID,
    id = TOOLBAR_ITEM_FILTER_NEO4J_ITEMS,
    toolTip = "Filter Neo4j Items",
    image = "neo4j_check.svg",
    separator = true
  )
  public void filterNeo4j() {
    ContextDialog dialog = ContextDialog.getInstance();
    if (dialog!=null) {
      dialog.getSearchTextWidget().setText("Neo4j");
    }
  }

}
