package org.neo4j.hop.steps.split;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.annotations.Transform;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.exception.HopXMLException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.value.ValueMetaString;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.xml.XMLHandler;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformMeta;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.w3c.dom.Node;

@Transform(
  id = "Neo4jSplitGraph",
  name = "Neo4j Split Graph",
  description = "Splits the nodes and relationships of a graph data type",
  image = "neo4j_split.svg",
  categoryDescription = "Neo4j",
  documentationUrl = "https://github.com/knowbi/knowbi-pentaho-pdi-neo4j-output/wiki/"
)
public class SplitGraphMeta extends BaseTransformMeta implements ITransformMeta<SplitGraph, SplitGraphData> {

  public static final String GRAPH_FIELD = "graph_field";
  public static final String TYPE_FIELD = "type_field";
  public static final String ID_FIELD = "id_field";
  public static final String PROPERTY_SET_FIELD = "property_set_field";

  protected String graphField;
  protected String typeField;
  protected String idField;
  protected String propertySetField;

  @Override public void setDefault() {
    graphField = "graph";
    typeField = "type";
    idField = "id";
    propertySetField = "propertySet";
  }

  @Override public void getFields( IRowMeta inputRowMeta, String name, IRowMeta[] info, TransformMeta nextStep, IVariables space, IMetaStore metaStore )
    throws HopTransformException {
    if ( StringUtils.isNotEmpty( typeField ) ) {
      ValueMetaString typeValueMeta = new ValueMetaString( space.environmentSubstitute( typeField ) );
      typeValueMeta.setOrigin( name );
      inputRowMeta.addValueMeta( typeValueMeta );
    }
    if ( StringUtils.isNotEmpty( idField ) ) {
      ValueMetaString idValueMeta = new ValueMetaString( space.environmentSubstitute( idField ) );
      idValueMeta.setOrigin( name );
      inputRowMeta.addValueMeta( idValueMeta );
    }
    if ( StringUtils.isNotEmpty( propertySetField ) ) {
      ValueMetaString propertySetValueMeta = new ValueMetaString( space.environmentSubstitute( propertySetField ) );
      propertySetValueMeta.setOrigin( name );
      inputRowMeta.addValueMeta( propertySetValueMeta );
    }
  }

  @Override public String getXML() throws HopException {
    StringBuffer xml = new StringBuffer();
    xml.append( XMLHandler.addTagValue( GRAPH_FIELD, graphField ) );
    xml.append( XMLHandler.addTagValue( TYPE_FIELD, typeField ) );
    xml.append( XMLHandler.addTagValue( ID_FIELD, idField ) );
    xml.append( XMLHandler.addTagValue( PROPERTY_SET_FIELD, propertySetField ) );
    return xml.toString();
  }

  @Override public void loadXML( Node stepnode, IMetaStore metaStore ) throws HopXMLException {
    graphField = XMLHandler.getTagValue( stepnode, GRAPH_FIELD );
    typeField = XMLHandler.getTagValue( stepnode, TYPE_FIELD );
    idField = XMLHandler.getTagValue( stepnode, ID_FIELD );
    propertySetField = XMLHandler.getTagValue( stepnode, PROPERTY_SET_FIELD );
  }

  @Override public SplitGraph createTransform( TransformMeta transformMeta, SplitGraphData iTransformData, int copyNr, PipelineMeta pipelineMeta, Pipeline pipeline ) {
    return new SplitGraph( transformMeta, this, iTransformData, copyNr, pipelineMeta, pipeline );
  }

  @Override public SplitGraphData getTransformData() {
    return new SplitGraphData();
  }

  /**
   * Gets graphField
   *
   * @return value of graphField
   */
  public String getGraphField() {
    return graphField;
  }

  /**
   * @param graphField The graphField to set
   */
  public void setGraphField( String graphField ) {
    this.graphField = graphField;
  }

  /**
   * Gets typeField
   *
   * @return value of typeField
   */
  public String getTypeField() {
    return typeField;
  }

  /**
   * @param typeField The typeField to set
   */
  public void setTypeField( String typeField ) {
    this.typeField = typeField;
  }

  /**
   * Gets idField
   *
   * @return value of idField
   */
  public String getIdField() {
    return idField;
  }

  /**
   * @param idField The idField to set
   */
  public void setIdField( String idField ) {
    this.idField = idField;
  }

  /**
   * Gets propertySetField
   *
   * @return value of propertySetField
   */
  public String getPropertySetField() {
    return propertySetField;
  }

  /**
   * @param propertySetField The propertySetField to set
   */
  public void setPropertySetField( String propertySetField ) {
    this.propertySetField = propertySetField;
  }
}
