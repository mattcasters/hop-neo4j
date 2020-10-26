package org.neo4j.hop.logging.util;

import org.neo4j.driver.Result;

public interface WorkLambda<T> {
  T getResultValue( Result result );
}
