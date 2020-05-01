package org.neo4j.hop.shared;

import org.apache.hop.core.logging.ILogChannel;
import org.neo4j.driver.Session;

import java.util.List;

public class NeoConnectionUtils {
  private static Class<?> PKG = NeoConnectionUtils.class; // for i18n purposes, needed by Translator2!!

  public static final void createNodeIndex( ILogChannel log, Session session, List<String> labels, List<String> keyProperties ) {

    // If we have no properties or labels, we have nothing to do here
    //
    if ( keyProperties.size() == 0 ) {
      return;
    }
    if ( labels.size() == 0 ) {
      return;
    }

    // We only use the first label for index or constraint
    //
    String labelsClause = ":" + labels.get( 0 );

    // CREATE CONSTRAINT ON (n:NodeLabel) ASSERT n.property1 IS UNIQUE
    //
    if ( keyProperties.size() == 1 ) {
      String property = keyProperties.get( 0 );
      String constraintCypher = "CREATE CONSTRAINT ON (n" + labelsClause + ") ASSERT n." + property + " IS UNIQUE;";

      log.logDetailed( "Creating constraint : " + constraintCypher );
      session.run( constraintCypher );

      // This creates an index, no need to go further here...
      //
      return;
    }

    // Composite index case...
    //
    // CREATE INDEX ON :NodeLabel(property, property2, ...);
    //
    String indexCypher = "CREATE INDEX ON ";

    indexCypher += labelsClause;
    indexCypher += "(";
    boolean firstProperty = true;
    for ( String property : keyProperties ) {
      if ( firstProperty ) {
        firstProperty = false;
      } else {
        indexCypher += ", ";
      }
      indexCypher += property;
    }
    indexCypher += ")";

    log.logDetailed( "Creating index : " + indexCypher );
    session.run( indexCypher );
  }

}
