package org.neo4j.hop.perspective;

import java.util.ArrayList;
import java.util.List;

public class HistoryResults {

  private String topic;

  private String subjectName;
  private String subjectType;
  private String subjectCopy;
  private String parentName;
  private String parentType;

  private List<HistoryResult> lastExecutions;

  public HistoryResults() {
    lastExecutions = new ArrayList<>();
  }

  /**
   * Gets topic
   *
   * @return value of topic
   */
  public String getTopic() {
    return topic;
  }

  /**
   * @param topic The topic to set
   */
  public void setTopic( String topic ) {
    this.topic = topic;
  }

  /**
   * Gets lastExecutions
   *
   * @return value of lastExecutions
   */
  public List<HistoryResult> getLastExecutions() {
    return lastExecutions;
  }

  /**
   * @param lastExecutions The lastExecutions to set
   */
  public void setLastExecutions( List<HistoryResult> lastExecutions ) {
    this.lastExecutions = lastExecutions;
  }

  /**
   * Gets subjectName
   *
   * @return value of subjectName
   */
  public String getSubjectName() {
    return subjectName;
  }

  /**
   * @param subjectName The subjectName to set
   */
  public void setSubjectName( String subjectName ) {
    this.subjectName = subjectName;
  }

  /**
   * Gets subjectType
   *
   * @return value of subjectType
   */
  public String getSubjectType() {
    return subjectType;
  }

  /**
   * @param subjectType The subjectType to set
   */
  public void setSubjectType( String subjectType ) {
    this.subjectType = subjectType;
  }

  /**
   * Gets subjectCopy
   *
   * @return value of subjectCopy
   */
  public String getSubjectCopy() {
    return subjectCopy;
  }

  /**
   * @param subjectCopy The subjectCopy to set
   */
  public void setSubjectCopy( String subjectCopy ) {
    this.subjectCopy = subjectCopy;
  }

  /**
   * Gets parentName
   *
   * @return value of parentName
   */
  public String getParentName() {
    return parentName;
  }

  /**
   * @param parentName The parentName to set
   */
  public void setParentName( String parentName ) {
    this.parentName = parentName;
  }

  /**
   * Gets parentType
   *
   * @return value of parentType
   */
  public String getParentType() {
    return parentType;
  }

  /**
   * @param parentType The parentType to set
   */
  public void setParentType( String parentType ) {
    this.parentType = parentType;
  }
}
