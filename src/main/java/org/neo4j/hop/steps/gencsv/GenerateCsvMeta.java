package org.neo4j.hop.steps.gencsv;

import org.apache.hop.core.annotations.Transform;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopXMLException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.row.value.ValueMetaString;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.xml.XMLHandler;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransform;
import org.apache.hop.pipeline.transform.ITransformData;
import org.apache.hop.pipeline.transform.ITransformMeta;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.w3c.dom.Node;

import java.util.List;

@Transform(
  id = "Neo4jLoad",
  name = "Neo4j Generate CSVs",
  description = "Generate CSV files for nodes and relationships in the import/ folder for use with neo4j-import",
  image = "neo4j_load.svg",
  categoryDescription = "Neo4j",
  documentationUrl = "https://github.com/knowbi/knowbi-pentaho-pdi-neo4j-output/wiki/"
)
public class GenerateCsvMeta extends BaseTransformMeta implements ITransformMeta<GenerateCsv, GenerateCsvData> {

  public static final String GRAPH_FIELD_NAME = "graph_field_name";
  public static final String BASE_FOLDER = "base_folder";
  public static final String UNIQUENESS_STRATEGY = "uniqueness_strategy";
  public static final String FILES_PREFIX = "files_prefix";
  public static final String FILENAME_FIELD = "filename_field";
  public static final String FILE_TYPE_FIELD = "file_type_field";

  protected String graphFieldName;
  protected String baseFolder;
  protected UniquenessStrategy uniquenessStrategy;

  protected String filesPrefix;
  protected String filenameField;
  protected String fileTypeField;

  @Override public void setDefault() {
    baseFolder = "/var/lib/neo4j/";
    uniquenessStrategy = UniquenessStrategy.None;
    filesPrefix = "prefix";
    filenameField = "filename";
    fileTypeField = "fileType";
  }

  @Override public void getFields( IRowMeta inputRowMeta, String name, IRowMeta[] info, TransformMeta nextStep, IVariables space, IMetaStore metaStore ) {

    inputRowMeta.clear();

    IValueMeta filenameValueMeta = new ValueMetaString( space.environmentSubstitute( filenameField ) );
    filenameValueMeta.setOrigin( name );
    inputRowMeta.addValueMeta( filenameValueMeta );

    IValueMeta fileTypeValueMeta = new ValueMetaString( space.environmentSubstitute( fileTypeField ) );
    fileTypeValueMeta.setOrigin( name );
    inputRowMeta.addValueMeta( fileTypeValueMeta );


  }

  @Override public String getXML() throws HopException {
    StringBuffer xml = new StringBuffer();
    xml.append( XMLHandler.addTagValue( GRAPH_FIELD_NAME, graphFieldName ) );
    xml.append( XMLHandler.addTagValue( BASE_FOLDER, baseFolder ) );
    xml.append( XMLHandler.addTagValue( UNIQUENESS_STRATEGY, uniquenessStrategy != null ? uniquenessStrategy.name() : null ) );
    xml.append( XMLHandler.addTagValue( FILES_PREFIX, filesPrefix ) );
    xml.append( XMLHandler.addTagValue( FILENAME_FIELD, filenameField ) );
    xml.append( XMLHandler.addTagValue( FILE_TYPE_FIELD, fileTypeField ) );
    return xml.toString();
  }

  @Override public void loadXML( Node stepnode, IMetaStore metaStore ) throws HopXMLException {
    graphFieldName = XMLHandler.getTagValue( stepnode, GRAPH_FIELD_NAME );
    baseFolder = XMLHandler.getTagValue( stepnode, BASE_FOLDER );
    uniquenessStrategy = UniquenessStrategy.getStrategyFromName( XMLHandler.getTagValue( stepnode, UNIQUENESS_STRATEGY ) );
    filesPrefix = XMLHandler.getTagValue( stepnode, FILES_PREFIX );
    filenameField = XMLHandler.getTagValue( stepnode, FILENAME_FIELD );
    fileTypeField = XMLHandler.getTagValue( stepnode, FILE_TYPE_FIELD );
  }

  @Override public GenerateCsv createTransform( TransformMeta transformMeta, GenerateCsvData data, int copyNr, PipelineMeta pipelineMeta, Pipeline pipeline ) {
    return new GenerateCsv( transformMeta, this, data, copyNr, pipelineMeta, pipeline );
  }

  @Override public GenerateCsvData getTransformData() {
    return new GenerateCsvData();
  }

  /**
   * Gets graphFieldName
   *
   * @return value of graphFieldName
   */
  public String getGraphFieldName() {
    return graphFieldName;
  }

  /**
   * @param graphFieldName The graphFieldName to set
   */
  public void setGraphFieldName( String graphFieldName ) {
    this.graphFieldName = graphFieldName;
  }


  /**
   * Gets baseFolder
   *
   * @return value of baseFolder
   */
  public String getBaseFolder() {
    return baseFolder;
  }

  /**
   * @param baseFolder The baseFolder to set
   */
  public void setBaseFolder( String baseFolder ) {
    this.baseFolder = baseFolder;
  }

  /**
   * Gets nodeUniquenessStrategy
   *
   * @return value of nodeUniquenessStrategy
   */
  public UniquenessStrategy getUniquenessStrategy() {
    return uniquenessStrategy;
  }

  /**
   * @param uniquenessStrategy The nodeUniquenessStrategy to set
   */
  public void setUniquenessStrategy( UniquenessStrategy uniquenessStrategy ) {
    this.uniquenessStrategy = uniquenessStrategy;
  }

  /**
   * Gets filesPrefix
   *
   * @return value of filesPrefix
   */
  public String getFilesPrefix() {
    return filesPrefix;
  }

  /**
   * @param filesPrefix The filesPrefix to set
   */
  public void setFilesPrefix( String filesPrefix ) {
    this.filesPrefix = filesPrefix;
  }

  /**
   * Gets filenameField
   *
   * @return value of filenameField
   */
  public String getFilenameField() {
    return filenameField;
  }

  /**
   * @param filenameField The filenameField to set
   */
  public void setFilenameField( String filenameField ) {
    this.filenameField = filenameField;
  }

  /**
   * Gets fileTypeField
   *
   * @return value of fileTypeField
   */
  public String getFileTypeField() {
    return fileTypeField;
  }

  /**
   * @param fileTypeField The fileTypeField to set
   */
  public void setFileTypeField( String fileTypeField ) {
    this.fileTypeField = fileTypeField;
  }
}
