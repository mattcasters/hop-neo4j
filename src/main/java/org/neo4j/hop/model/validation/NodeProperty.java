package org.neo4j.hop.model.validation;

public class NodeProperty {
  private String nodeName;
  private String propertyName;

  public NodeProperty() {
  }

  public NodeProperty( String nodeName, String propertyName ) {
    this();
    this.nodeName = nodeName;
    this.propertyName = propertyName;
  }

  /**
   * Gets nodeName
   *
   * @return value of nodeName
   */
  public String getNodeName() {
    return nodeName;
  }

  /**
   * @param nodeName The nodeName to set
   */
  public void setNodeName( String nodeName ) {
    this.nodeName = nodeName;
  }

  /**
   * Gets propertyName
   *
   * @return value of propertyName
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * @param propertyName The propertyName to set
   */
  public void setPropertyName( String propertyName ) {
    this.propertyName = propertyName;
  }
}
