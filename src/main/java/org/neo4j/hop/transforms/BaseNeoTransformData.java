package org.neo4j.hop.transforms;

import org.apache.hop.pipeline.transform.BaseTransformData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseNeoTransformData extends BaseTransformData {

  /**
   * A map : GraphUsage / TransformName / NodeLabels
   */

  public Map<String, Map<String, Set<String>>> usageMap = new HashMap<>();
}
