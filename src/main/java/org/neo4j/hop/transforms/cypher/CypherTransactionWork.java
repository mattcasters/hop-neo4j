package org.neo4j.hop.transforms.cypher;

import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.apache.hop.core.exception.HopException;

import java.util.Map;

public class CypherTransactionWork implements TransactionWork<Void> {
  private final Cypher transform;
  private final Object[] currentRow;
  private final boolean unwind;
  private final int attempts;
  private String cypher;
  private Map<String, Object> unwindMap;

  public CypherTransactionWork( Cypher transform, Object[] currentRow, boolean unwind, String cypher, Map<String, Object> unwindMap, int attempts ) {
    this.transform = transform;
    this.currentRow = currentRow;
    this.unwind = unwind;
    this.cypher = cypher;
    this.unwindMap = unwindMap;
    this.attempts = attempts;
  }

  @Override public Void execute( Transaction tx ) {
    Result result = null;
    for (int attempt=0;attempt<attempts;attempt++) {
        try {
          result = tx.run( cypher, unwindMap );
          break;
        } catch(Exception e) {
          if (attempt+1>=attempts) {
            throw e;
          } else {
            transform.logBasic( "Retrying unwind after error: "+e.getMessage() );
          }
        }
    }
    try {
      transform.getResultRows( result, currentRow, unwind );
      return null;
    } catch ( HopException e ) {
      throw new RuntimeException( "Unable to execute cypher statement '"+cypher+"'", e );
    }
  }

  /**
   * Gets cypher
   *
   * @return value of cypher
   */
  public String getCypher() {
    return cypher;
  }

  /**
   * @param cypher The cypher to set
   */
  public void setCypher( String cypher ) {
    this.cypher = cypher;
  }

  /**
   * Gets unwindMap
   *
   * @return value of unwindMap
   */
  public Map<String, Object> getUnwindMap() {
    return unwindMap;
  }

  /**
   * @param unwindMap The unwindMap to set
   */
  public void setUnwindMap( Map<String, Object> unwindMap ) {
    this.unwindMap = unwindMap;
  }
}
