package org.neo4j.hop.shared;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.metastore.api.exceptions.MetaStoreException;
import org.apache.hop.metastore.stores.delegate.DelegatingMetaStore;
import org.apache.hop.metastore.stores.xml.XmlMetaStore;

public class MetaStoreUtil {

  public static final String getMetaStoreDescription( IMetaStore metaStore ) throws MetaStoreException {
    String name = metaStore.getName();
    String desc = metaStore.getDescription();

    String description = "";
    if ( metaStore instanceof DelegatingMetaStore ) {
      DelegatingMetaStore delegatingMetaStore = (DelegatingMetaStore) metaStore;
      boolean first = true;
      for ( IMetaStore store : delegatingMetaStore.getMetaStoreList() ) {
        if ( first ) {
          description += "{ ";
        } else {
          description += ", ";
        }
        description += getMetaStoreDescription( store );
        if ( first ) {
          description += " }";
          first = false;
        }
      }
    } else if ( metaStore instanceof XmlMetaStore ) {
      String rootFolder = ( (XmlMetaStore) metaStore ).getRootFolder();
      description += name + "( located in folder: " + rootFolder + " )";
    } else {
      if ( StringUtils.isNotEmpty( desc ) ) {
        description += name + "(" + desc + ")";
      } else {
        description += name;
      }
    }
    return description;
  }
}
