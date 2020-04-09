package org.neo4j.hop.transforms.graph;

import org.neo4j.hop.transforms.BaseNeoTransformData;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.hop.model.GraphModel;
import org.neo4j.hop.model.GraphProperty;
import org.neo4j.hop.shared.NeoConnection;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.pipeline.transform.ITransformData;

import java.util.HashMap;
import java.util.Map;

public class GraphOutputData extends BaseNeoTransformData implements ITransformData {

  public IRowMeta outputRowMeta;
  public NeoConnection neoConnection;
  public String url;
  public Session session;
  public int[] fieldIndexes;
  public long batchSize;
  public Transaction transaction;
  public long outputCount;
  public boolean hasInput;
  public GraphModel graphModel;
  public int nodeCount;
  public Map<String, CypherParameters> cypherMap;
  public HashMap<String, Map<GraphProperty, Integer>> relationshipPropertyIndexMap;
  public boolean version4;
}
