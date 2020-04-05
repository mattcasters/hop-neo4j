package org.neo4j.hop.steps.output;

import org.neo4j.hop.steps.BaseNeoTransformData;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.hop.model.GraphPropertyType;
import org.neo4j.hop.shared.NeoConnection;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.pipeline.transform.ITransformData;
import org.apache.hop.metastore.api.IMetaStore;

import java.util.List;
import java.util.Map;

public class Neo4JOutputData extends BaseNeoTransformData implements ITransformData {

  public IRowMeta outputRowMeta;

  public String[] fieldNames;

  public NeoConnection neoConnection;
  public String url;
  public Driver driver;
  public Session session;

  public long batchSize;
  public long outputCount;

  public int[] fromNodePropIndexes;
  public int[] fromNodeLabelIndexes;
  public int[] toNodePropIndexes;
  public int[] toNodeLabelIndexes;
  public int[] relPropIndexes;
  public int relationshipIndex;
  public GraphPropertyType[] fromNodePropTypes;
  public GraphPropertyType[] toNodePropTypes;
  public GraphPropertyType[] relPropTypes;

  public List<Map<String, Object>> unwindList;

  public String fromLabelsClause;
  public String toLabelsClause;
  public String[] fromLabelValues;
  public String[] toLabelValues;
  public String relationshipLabelValue;

  public String previousFromLabelsClause;
  public String previousToLabelsClause;

  public IMetaStore metaStore;
  public boolean dynamicFromLabels;
  public boolean dynamicToLabels;
  public boolean dynamicRelLabel;

  public List<String> previousFromLabels;
  public List<String> fromLabels;
  public List<String> previousToLabels;
  public List<String> toLabels;
  public String previousRelationshipLabel;
  public String relationshipLabel;

  public OperationType fromOperationType;
  public OperationType toOperationType;
  public OperationType relOperationType;

  public String cypher;
  public boolean version4;
}
