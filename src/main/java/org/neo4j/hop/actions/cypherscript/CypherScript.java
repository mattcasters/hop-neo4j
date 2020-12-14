package org.neo4j.hop.actions.cypherscript;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Result;
import org.apache.hop.core.annotations.Action;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopXmlException;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.xml.XmlHandler;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.workflow.action.ActionBase;
import org.apache.hop.workflow.action.IAction;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;
import org.neo4j.hop.shared.NeoConnection;
import org.w3c.dom.Node;

@Action(
    id = "NEO4J_CYPHER_SCRIPT",
    name = "Neo4j Cypher Script",
    description = "Execute a Neo4j Cypher script",
    image = "neo4j_cypher.svg",
    categoryDescription = "i18n:org.apache.hop.workflow:ActionCategory.Category.Scripting",
    documentationUrl = "https://github.com/knowbi/knowbi-pentaho-pdi-neo4j-output/wiki/")
public class CypherScript extends ActionBase implements IAction {

  private String connectionName;

  private String script;

  private boolean replacingVariables;

  public CypherScript() {
    this("", "");
  }

  public CypherScript(String name) {
    this(name, "");
  }

  public CypherScript(String name, String description) {
    super(name, description);
  }

  @Override
  public String getXml() {
    StringBuilder xml = new StringBuilder();
    // Add action name, type, ...
    //
    xml.append(super.getXml());

    xml.append(XmlHandler.addTagValue("connection", connectionName));
    xml.append(XmlHandler.addTagValue("script", script));
    xml.append(XmlHandler.addTagValue("replace_variables", replacingVariables ? "Y" : "N"));

    return xml.toString();
  }

  @Override public void loadXml( Node node, IHopMetadataProvider iHopMetadataProvider, IVariables iVariables ) throws HopXmlException {
    super.loadXml(node);

    connectionName = XmlHandler.getTagValue(node, "connection");
    script = XmlHandler.getTagValue(node, "script");
    replacingVariables = "Y".equalsIgnoreCase(XmlHandler.getTagValue(node, "replace_variables"));
  }

  @Override
  public Result execute(Result result, int nr) throws HopException {

    IHopMetadataSerializer<NeoConnection> serializer =
        metadataProvider.getSerializer(NeoConnection.class);

    // Replace variables & parameters
    //
    NeoConnection connection;
    String realConnectionName = resolve(connectionName);
    try {
      if (StringUtils.isEmpty(realConnectionName)) {
        throw new HopException("The Neo4j connection name is not set");
      }

      connection = serializer.load(realConnectionName);
      if (connection == null) {
        throw new HopException("Unable to find connection with name '" + realConnectionName + "'");
      }
    } catch (Exception e) {
      result.setResult(false);
      result.increaseErrors(1L);
      throw new HopException(
          "Unable to gencsv or find connection with name '" + realConnectionName + "'", e);
    }

    String realScript;
    if (replacingVariables) {
      realScript = resolve(script);
    } else {
      realScript = script;
    }

    Session session = null;
    int nrExecuted = 0;

    // Connect to the database
    //
    session = connection.getSession(log, this);

    TransactionWork<Integer> transactionWork =
        transaction -> {
          int executed = 0;

          try {
            // Split the script into parts : semi-colon at the start of a separate line
            //
            String[] commands = realScript.split("\\r?\\n;");
            for (String command : commands) {
              // Cleanup command: replace leading and trailing whitespaces and newlines
              //
              String cypher = command.replaceFirst("^\\s+", "").replaceFirst("\\s+$", "");

              // Only execute if the statement is not empty
              //
              if (StringUtils.isNotEmpty(cypher)) {
                transaction.run(cypher);
                executed++;
                log.logDetailed("Executed cypher statement: " + cypher);
              }
            }
            // All statements executed successfully so commit
            //
            transaction.commit();
          } catch (Exception e) {
            log.logError("Error executing cypher statements...", e);
            result.increaseErrors(1L);
            transaction.rollback();
            result.setResult(false);
          }

          return executed;
        };
    nrExecuted = session.writeTransaction(transactionWork);

    if (result.getNrErrors() == 0) {
      logBasic("Neo4j script executed " + nrExecuted + " statements without error");
    } else {
      logBasic("Neo4j script executed with error(s)");
    }

    return result;
  }

  @Override
  public String getDialogClassName() {
    return super.getDialogClassName();
  }

  @Override
  public boolean evaluates() {
    return true;
  }

  @Override
  public boolean isUnconditional() {
    return false;
  }

  /**
   * Gets connectionName
   *
   * @return value of connectionName
   */
  public String getConnectionName() {
    return connectionName;
  }

  /** @param connectionName The connectionName to set */
  public void setConnectionName(String connectionName) {
    this.connectionName = connectionName;
  }

  /**
   * Gets script
   *
   * @return value of script
   */
  public String getScript() {
    return script;
  }

  /** @param script The script to set */
  public void setScript(String script) {
    this.script = script;
  }

  /**
   * Gets replacingVariables
   *
   * @return value of replacingVariables
   */
  public boolean isReplacingVariables() {
    return replacingVariables;
  }

  /** @param replacingVariables The replacingVariables to set */
  public void setReplacingVariables(boolean replacingVariables) {
    this.replacingVariables = replacingVariables;
  }
}
