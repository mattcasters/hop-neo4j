package org.neo4j.hop.ui.model;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import org.apache.hop.core.gui.plugin.GuiMetaStoreElement;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.ui.cluster.IGuiMetaStorePlugin;
import org.neo4j.hop.model.GraphModel;
import org.neo4j.hop.shared.NeoConnection;

@GuiPlugin
@GuiMetaStoreElement(
  name = "Neo4j Graph Model",
  description = "Describes a logical Neo4j Graph Model",
  iconImage = "neo4j_logo.svg"
)
public class GraphModelGuiPlugin implements IGuiMetaStorePlugin<GraphModel> {
  @Override public Class<GraphModel> getMetastoreElementClass() {
    return GraphModel.class;
  }
}
