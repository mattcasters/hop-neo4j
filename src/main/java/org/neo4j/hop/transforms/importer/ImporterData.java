package org.neo4j.hop.transforms.importer;

import org.apache.hop.pipeline.transform.BaseTransformData;
import org.apache.hop.pipeline.transform.ITransformData;

import java.util.List;

public class ImporterData extends BaseTransformData implements ITransformData {

  public List<String> nodesFiles;
  public List<String> relsFiles;

  public String importFolder;
  public String adminCommand;
  public String databaseFilename;
  public String baseFolder;
  public String reportFile;


  public int filenameFieldIndex;
  public int fileTypeFieldIndex;
  public String badTolerance;
  public String readBufferSize;
  public String maxMemory;
  public String processors;
}
