package org.neo4j.hop.transforms.cypher;

import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.hop.core.data.GraphPropertyDataType;
import org.neo4j.hop.shared.NeoConnection;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.pipeline.transform.BaseTransformData;
import org.apache.hop.pipeline.transform.ITransformData;

import java.util.List;
import java.util.Map;

public class CypherData extends BaseTransformData implements ITransformData {

  public IRowMeta outputRowMeta;
  public NeoConnection neoConnection;
  public String url;
  public Session session;
  public int[] fieldIndexes;
  public String cypher;
  public long batchSize;
  public Transaction transaction;
  public long outputCount;
  public boolean hasInput;
  public int cypherFieldIndex;

  public String unwindMapName;
  public List<Map<String, Object>> unwindList;

  public List<CypherStatement> cypherStatements;

  public Map<String, GraphPropertyDataType> returnSourceTypeMap;
}
