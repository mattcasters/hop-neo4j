package org.neo4j.hop.steps.gencsv;

import org.neo4j.hop.core.data.GraphNodeData;
import org.apache.hop.core.exception.HopException;

public interface NodeCollisionListener {
  /**
   * When a node gets added and an existing node already exists, let the system know what to do with the existing node.
   * This is used to update or aggregate properties in the existing data.
   *
   * @param existing
   * @param added
   *
   * @throws HopException
   */
  void handleCollission( GraphNodeData existing, GraphNodeData added) throws HopException;
}
