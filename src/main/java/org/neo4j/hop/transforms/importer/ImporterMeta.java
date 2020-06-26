package org.neo4j.hop.transforms.importer;

import org.apache.hop.core.annotations.Transform;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.exception.HopXmlException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.xml.XmlHandler;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformMeta;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.w3c.dom.Node;

@Transform(
  id = "Neo4jImport",
  name = "Neo4j Import",
  description = "Runs an import command using the provided CSV files ",
  image = "neo4j_import.svg",
  categoryDescription = "Neo4j",
  documentationUrl = "https://github.com/knowbi/knowbi-pentaho-pdi-neo4j-output/wiki/"
)
public class ImporterMeta extends BaseTransformMeta implements ITransformMeta<Importer, ImporterData> {

  public static final String FILENAME_FIELD = "filename_field_name";
  public static final String FILE_TYPE_FIELD = "file_type_field_name";
  public static final String ADMIN_COMMAND = "admin_command";
  public static final String BASE_FOLDER = "base_folder";
  public static final String REPORT_FILE = "report_file";
  public static final String DB_NAME = "db_name";
  public static final String IGNORE_DUPLICATE_NODES = "ignore_duplicate_nodes";
  public static final String IGNORE_MISSING_NODES = "ignore_missing_nodes";
  public static final String IGNORE_EXTRA_COLUMNS = "ignore_extra_columns";
  public static final String MAX_MEMORY = "max_memory";
  public static final String HIGH_IO = "high_io";
  public static final String MULTI_LINE = "multi_line";
  public static final String SKIP_BAD_RELATIONSHIPS = "skip_bad_relationships";
  public static final String READ_BUFFER_SIZE = "read_buffer_size";

  protected String filenameField;
  protected String fileTypeField;
  protected String baseFolder;
  protected String adminCommand;
  protected String reportFile;
  protected String databaseFilename;
  protected String maxMemory;
  protected boolean ignoringDuplicateNodes;
  protected boolean ignoringMissingNodes;
  protected boolean ignoringExtraColumns;
  protected boolean highIo;
  protected boolean multiLine;
  protected boolean skippingBadRelationships;
  protected String readBufferSize;


  @Override public void setDefault() {
    databaseFilename = "graph.db";
    adminCommand = "neo4j-import";
    baseFolder = "/var/lib/neo4j/";
    reportFile = "import.report";
    readBufferSize = "4M";
  }

  @Override public void getFields( IRowMeta inputRowMeta, String name, IRowMeta[] info, TransformMeta nextStep, IVariables space, IHopMetadataProvider metadataProvider )
    throws HopTransformException {
    // No fields are added by default
  }

  @Override public String getXml() throws HopException {
    StringBuffer xml = new StringBuffer();
    xml.append( XmlHandler.addTagValue( FILENAME_FIELD, filenameField ) );
    xml.append( XmlHandler.addTagValue( FILE_TYPE_FIELD, fileTypeField ) );
    xml.append( XmlHandler.addTagValue( ADMIN_COMMAND, adminCommand ) );
    xml.append( XmlHandler.addTagValue( BASE_FOLDER, baseFolder ) );
    xml.append( XmlHandler.addTagValue( REPORT_FILE, reportFile ) );
    xml.append( XmlHandler.addTagValue( DB_NAME, databaseFilename ) );
    xml.append( XmlHandler.addTagValue( MAX_MEMORY, maxMemory ) );
    xml.append( XmlHandler.addTagValue( IGNORE_DUPLICATE_NODES, ignoringDuplicateNodes ) );
    xml.append( XmlHandler.addTagValue( IGNORE_MISSING_NODES, ignoringMissingNodes ) );
    xml.append( XmlHandler.addTagValue( IGNORE_EXTRA_COLUMNS, ignoringExtraColumns ) );
    xml.append( XmlHandler.addTagValue( HIGH_IO, highIo ) );
    xml.append( XmlHandler.addTagValue( MULTI_LINE, multiLine ) );
    xml.append( XmlHandler.addTagValue( SKIP_BAD_RELATIONSHIPS, skippingBadRelationships ) );
    xml.append( XmlHandler.addTagValue( READ_BUFFER_SIZE, readBufferSize ) );
    return xml.toString();
  }

  @Override public void loadXml( Node stepnode, IHopMetadataProvider metadataProvider ) throws HopXmlException {
    filenameField = XmlHandler.getTagValue( stepnode, FILENAME_FIELD );
    fileTypeField = XmlHandler.getTagValue( stepnode, FILE_TYPE_FIELD );
    adminCommand = XmlHandler.getTagValue( stepnode, ADMIN_COMMAND );
    baseFolder = XmlHandler.getTagValue( stepnode, BASE_FOLDER );
    reportFile = XmlHandler.getTagValue( stepnode, REPORT_FILE );
    databaseFilename = XmlHandler.getTagValue( stepnode, DB_NAME );
    maxMemory = XmlHandler.getTagValue( stepnode, MAX_MEMORY );
    ignoringDuplicateNodes = "Y".equalsIgnoreCase( XmlHandler.getTagValue( stepnode, IGNORE_DUPLICATE_NODES ) );
    ignoringMissingNodes = "Y".equalsIgnoreCase( XmlHandler.getTagValue( stepnode, IGNORE_MISSING_NODES ) );
    ignoringExtraColumns = "Y".equalsIgnoreCase( XmlHandler.getTagValue( stepnode, IGNORE_EXTRA_COLUMNS ) );
    highIo = "Y".equalsIgnoreCase( XmlHandler.getTagValue( stepnode, HIGH_IO ) );
    multiLine = "Y".equalsIgnoreCase( XmlHandler.getTagValue( stepnode, MULTI_LINE ) );
    skippingBadRelationships = "Y".equalsIgnoreCase( XmlHandler.getTagValue( stepnode, SKIP_BAD_RELATIONSHIPS ) );
    readBufferSize = XmlHandler.getTagValue( stepnode, READ_BUFFER_SIZE );
  }


  @Override public Importer createTransform( TransformMeta transformMeta, ImporterData iTransformData, int copyNr, PipelineMeta pipelineMeta, Pipeline pipeline ) {
    return new Importer( transformMeta, this, iTransformData, copyNr, pipelineMeta, pipeline );
  }

  @Override public ImporterData getTransformData() {
    return new ImporterData();
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
   * Gets adminCommand
   *
   * @return value of adminCommand
   */
  public String getAdminCommand() {
    return adminCommand;
  }

  /**
   * @param adminCommand The adminCommand to set
   */
  public void setAdminCommand( String adminCommand ) {
    this.adminCommand = adminCommand;
  }

  /**
   * Gets reportFile
   *
   * @return value of reportFile
   */
  public String getReportFile() {
    return reportFile;
  }

  /**
   * @param reportFile The reportFile to set
   */
  public void setReportFile( String reportFile ) {
    this.reportFile = reportFile;
  }

  /**
   * Gets maxMemory
   *
   * @return value of maxMemory
   */
  public String getMaxMemory() {
    return maxMemory;
  }

  /**
   * @param maxMemory The maxMemory to set
   */
  public void setMaxMemory( String maxMemory ) {
    this.maxMemory = maxMemory;
  }

  /**
   * Gets ignoringDuplicateNodes
   *
   * @return value of ignoringDuplicateNodes
   */
  public boolean isIgnoringDuplicateNodes() {
    return ignoringDuplicateNodes;
  }

  /**
   * @param ignoringDuplicateNodes The ignoringDuplicateNodes to set
   */
  public void setIgnoringDuplicateNodes( boolean ignoringDuplicateNodes ) {
    this.ignoringDuplicateNodes = ignoringDuplicateNodes;
  }

  /**
   * Gets ignoringMissingNodes
   *
   * @return value of ignoringMissingNodes
   */
  public boolean isIgnoringMissingNodes() {
    return ignoringMissingNodes;
  }

  /**
   * @param ignoringMissingNodes The ignoringMissingNodes to set
   */
  public void setIgnoringMissingNodes( boolean ignoringMissingNodes ) {
    this.ignoringMissingNodes = ignoringMissingNodes;
  }

  /**
   * Gets ignoringExtraColumns
   *
   * @return value of ignoringExtraColumns
   */
  public boolean isIgnoringExtraColumns() {
    return ignoringExtraColumns;
  }

  /**
   * @param ignoringExtraColumns The ignoringExtraColumns to set
   */
  public void setIgnoringExtraColumns( boolean ignoringExtraColumns ) {
    this.ignoringExtraColumns = ignoringExtraColumns;
  }

  /**
   * Gets highIo
   *
   * @return value of highIo
   */
  public boolean isHighIo() {
    return highIo;
  }

  /**
   * @param highIo The highIo to set
   */
  public void setHighIo( boolean highIo ) {
    this.highIo = highIo;
  }

  /**
   * Gets databaseFilename
   *
   * @return value of databaseFilename
   */
  public String getDatabaseFilename() {
    return databaseFilename;
  }

  /**
   * @param databaseFilename The databaseFilename to set
   */
  public void setDatabaseFilename( String databaseFilename ) {
    this.databaseFilename = databaseFilename;
  }

  /**
   * Gets multiLine
   *
   * @return value of multiLine
   */
  public boolean isMultiLine() {
    return multiLine;
  }

  /**
   * @param multiLine The multiLine to set
   */
  public void setMultiLine( boolean multiLine ) {
    this.multiLine = multiLine;
  }

  /**
   * Gets skippingBadRelationships
   *
   * @return value of skippingBadRelationships
   */
  public boolean isSkippingBadRelationships() {
    return skippingBadRelationships;
  }

  /**
   * @param skippingBadRelationships The skippingBadRelationships to set
   */
  public void setSkippingBadRelationships( boolean skippingBadRelationships ) {
    this.skippingBadRelationships = skippingBadRelationships;
  }

  /**
   * Gets readBufferSize
   *
   * @return value of readBufferSize
   */
  public String getReadBufferSize() {
    return readBufferSize;
  }

  /**
   * @param readBufferSize The readBufferSize to set
   */
  public void setReadBufferSize( String readBufferSize ) {
    this.readBufferSize = readBufferSize;
  }
}
