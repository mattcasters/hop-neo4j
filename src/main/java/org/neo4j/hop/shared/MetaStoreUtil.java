package org.neo4j.hop.shared;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.job.Job;
import org.apache.hop.metastore.MetaStoreConst;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.transform.ITransform;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.metastore.api.exceptions.MetaStoreException;
import org.apache.hop.metastore.stores.delegate.DelegatingMetaStore;
import org.apache.hop.metastore.stores.xml.XmlMetaStore;

public class MetaStoreUtil {

  public static final IMetaStore findMetaStore( ILoggingObject executor ) throws MetaStoreException {

    if ( executor instanceof ITransform ) {
      ITransform step = (ITransform) executor;
      if ( step.getMetaStore() != null ) {
        return step.getMetaStore();
      }
      Pipeline pipeline = step.getPipeline();
      if ( pipeline != null ) {
        if ( pipeline.getMetaStore() != null ) {
          return pipeline.getMetaStore();
        }
        if ( pipeline.getPipelineMeta().getMetaStore() != null ) {
          return pipeline.getPipelineMeta().getMetaStore();
        }
      }
    }

    if ( executor instanceof Pipeline ) {
      Pipeline pipeline = (Pipeline) executor;
      if ( pipeline.getMetaStore() != null ) {
        return pipeline.getMetaStore();
      }
      if ( pipeline.getPipelineMeta().getMetaStore() != null ) {
        return pipeline.getMetaStore();
      }
    }

    if ( executor instanceof Job ) {
      Job job = (Job) executor;
      if ( job.getJobMeta().getMetaStore() != null ) {
        return job.getJobMeta().getMetaStore();
      }
    }

    ILoggingObject parent = executor.getParent();
    if ( parent != null ) {
      IMetaStore metaStore = findMetaStore( parent );
      if ( metaStore != null ) {
        return metaStore;
      }
    }

    // Didn't find it anywhere in the tree above: lazy programmers!
    //
    System.err.println("METASTORE PROBLEM: Local couldn't be found, force local anyway");

    return MetaStoreConst.openLocalHopMetaStore();
  }

  public static final String getMetaStoreDescription(IMetaStore metaStore) throws MetaStoreException {
    String name = metaStore.getName();
    String desc = metaStore.getDescription();

    String description = "";
    if (metaStore instanceof DelegatingMetaStore ) {
      DelegatingMetaStore delegatingMetaStore = (DelegatingMetaStore) metaStore;
      boolean first = true;
      for (IMetaStore store : delegatingMetaStore.getMetaStoreList()) {
        if (first) {
          description+="{ ";
        } else {
          description+=", ";
        }
        description+=getMetaStoreDescription( store );
        if (first) {
          description+=" }";
          first = false;
        }
      }
    } else if (metaStore instanceof XmlMetaStore ) {
      String rootFolder = ((XmlMetaStore)metaStore).getRootFolder();
      description += name + "( located in folder: " + rootFolder + " )";
    } else {
      if ( StringUtils.isNotEmpty(desc)) {
        description += name + "(" + desc + ")";
      } else {
        description += name;
      }
    }
    return description;
  }
}
