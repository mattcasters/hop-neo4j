package org.neo4j.hop.ui.shared;

import org.apache.hop.core.gui.plugin.GuiMetaStoreElement;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.ui.cluster.IGuiMetaStorePlugin;
import org.neo4j.hop.shared.NeoConnection;

@GuiPlugin
@GuiMetaStoreElement(
  name = "Neo4j Connection",
  description = "Describes a connection to a Neo4j Graph Database",
  iconImage = "neo4j_logo.svg"
)
public class NeoConnectionGuiPlugin implements IGuiMetaStorePlugin<NeoConnection> {
  @Override public Class<NeoConnection> getMetaStoreElementClass() {
    return NeoConnection.class;
  }
}
