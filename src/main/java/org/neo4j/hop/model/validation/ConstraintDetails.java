package org.neo4j.hop.model.validation;

import org.neo4j.driver.Record;

public class ConstraintDetails {
  private String name;
  private String description;

  public ConstraintDetails() {
  }

  public ConstraintDetails( String name, String description ) {
    this();
    this.name = name;
    this.description = description;
  }

  public ConstraintDetails( Record record ) {
    this();
    this.name = record.get("name").asString();
    this.description = record.get( "description" ).asString();
  }

  /**
   * Gets name
   *
   * @return value of name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Gets description
   *
   * @return value of description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description The description to set
   */
  public void setDescription( String description ) {
    this.description = description;
  }
}
