package org.neo4j.hop.xp;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.extension.ExtensionPoint;
import org.apache.hop.core.extension.ExtensionPointPluginType;
import org.apache.hop.core.extension.HopExtensionPoint;
import org.apache.hop.core.extension.IExtensionPoint;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.plugins.IPlugin;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.plugin.MetadataPluginType;
import org.neo4j.hop.model.GraphModel;
import org.neo4j.hop.shared.NeoConnection;

import java.net.URL;
import java.util.List;

@ExtensionPoint(
  id = "RegisterMetadataObjectsExtensionPoint",
  extensionPointId = "HopEnvironmentAfterInit",
  description = "Register the Neo4j metadata plugin classes"
)
public class RegisterMetadataObjectsExtensionPoint implements IExtensionPoint<PluginRegistry> {
  @Override public void callExtensionPoint( ILogChannel log, PluginRegistry pluginRegistry ) throws HopException {

    // Find the plugin for this class.
    //
    IPlugin thisPlugin = pluginRegistry.findPluginWithId( ExtensionPointPluginType.class, "RegisterMetadataObjectsExtensionPoint" );
    ClassLoader classLoader = this.getClass().getClassLoader();
    List<String> libraries = thisPlugin.getLibraries();
    URL pluginUrl = thisPlugin.getPluginDirectory();

    pluginRegistry.registerPluginClass( classLoader, libraries, pluginUrl, NeoConnection.class.getName(), MetadataPluginType.class, HopMetadata.class, false );
    pluginRegistry.registerPluginClass( classLoader, libraries, pluginUrl, GraphModel.class.getName(), MetadataPluginType.class, HopMetadata.class, false );
  }
}
