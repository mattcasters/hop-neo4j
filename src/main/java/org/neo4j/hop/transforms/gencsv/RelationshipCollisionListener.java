package org.neo4j.hop.transforms.gencsv;

import org.neo4j.hop.core.data.GraphRelationshipData;
import org.apache.hop.core.exception.HopException;

public interface RelationshipCollisionListener {
  /**
   * When a relationship gets added and an existing relationship already exists, let the system know what to do with the existing relationship.
   * This is used to update or aggregate properties in the existing data.
   *
   * @param existing
   * @param added
   *
   * @throws HopException
   */
  void handleCollission( GraphRelationshipData existing, GraphRelationshipData added ) throws HopException;
}
