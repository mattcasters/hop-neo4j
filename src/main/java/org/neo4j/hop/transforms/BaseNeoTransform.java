package org.neo4j.hop.transforms;

import org.neo4j.hop.core.Neo4jDefaults;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransform;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.apache.hop.pipeline.transform.ITransformMeta;

import java.util.HashMap;

public abstract class BaseNeoTransform<Meta extends ITransformMeta, Data extends BaseNeoTransformData> extends BaseTransform<Meta, Data> {


  public BaseNeoTransform( TransformMeta transformMeta, Meta meta, Data data, int copyNr, PipelineMeta pipelineMeta,
                           Pipeline pipeline ) {
    super( transformMeta, meta, data, copyNr, pipelineMeta, pipeline );
  }

  @Override public boolean init() {

    data.usageMap = new HashMap<>();

    return super.init();
  }

  @Override public void dispose() {

    getPipeline().getExtensionDataMap().put( Neo4jDefaults.TRANS_NODE_UPDATES_GROUP, data.usageMap );

    super.dispose();
  }

}
