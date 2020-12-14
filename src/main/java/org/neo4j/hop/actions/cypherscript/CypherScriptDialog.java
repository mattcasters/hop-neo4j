package org.neo4j.hop.actions.cypherscript;

import org.apache.hop.core.Const;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.gui.GuiResource;
import org.apache.hop.ui.core.gui.WindowProperty;
import org.apache.hop.ui.core.widget.MetaSelectionLine;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.apache.hop.ui.workflow.action.ActionDialog;
import org.apache.hop.ui.workflow.dialog.WorkflowDialog;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.IAction;
import org.apache.hop.workflow.action.IActionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neo4j.hop.shared.NeoConnection;

public class CypherScriptDialog extends ActionDialog implements IActionDialog {

  public static final String CHECK_CONNECTIONS_DIALOG = "Neo4jCheckConnectionsDialog";
  private static Class<?> PKG = CypherScriptDialog.class; // for i18n purposes, needed by Translator2!!

  private Shell shell;

  private CypherScript cypherScript;

  private boolean changed;

  private Text wName;
  private MetaSelectionLine<NeoConnection> wConnection;
  private TextVar wScript;
  private Button wReplaceVariables;

  private Button wOk, wCancel;

  public CypherScriptDialog( Shell parent, IAction iAction, WorkflowMeta workflowMeta ) {
    super( parent, workflowMeta );
    this.cypherScript = (CypherScript) iAction;

    if ( this.cypherScript.getName() == null ) {
      this.cypherScript.setName( "Neo4j Cypher Script" );
    }
  }

  @Override public IAction open() {

    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
    props.setLook( shell );
    WorkflowDialog.setShellImage( shell, cypherScript );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        cypherScript.setChanged();
      }
    };
    changed = cypherScript.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( "Neo4j Cypher Script" );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    Label wlName = new Label( shell, SWT.RIGHT );
    wlName.setText( "Action name" );
    props.setLook( wlName );
    FormData fdlName = new FormData();
    fdlName.left = new FormAttachment( 0, 0 );
    fdlName.right = new FormAttachment( middle, -margin );
    fdlName.top = new FormAttachment( 0, margin );
    wlName.setLayoutData( fdlName );
    wName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wName );
    wName.addModifyListener( lsMod );
    FormData fdName = new FormData();
    fdName.left = new FormAttachment( middle, 0 );
    fdName.top = new FormAttachment( 0, margin );
    fdName.right = new FormAttachment( 100, 0 );
    wName.setLayoutData( fdName );
    Control lastControl = wName;

    wConnection = new MetaSelectionLine<>( variables, getMetadataProvider(), NeoConnection.class, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER, "Neo4j Connection", "The name of the Neo4j connection to use" );
    props.setLook( wConnection );
    wConnection.addModifyListener( lsMod );
    FormData fdConnection = new FormData();
    fdConnection.left = new FormAttachment( 0, 0 );
    fdConnection.right = new FormAttachment( 100, 0 );
    fdConnection.top = new FormAttachment( lastControl, margin );
    wConnection.setLayoutData( fdConnection );
    try {
      wConnection.fillItems();
    } catch ( Exception e ) {
      new ErrorDialog( shell, "Error", "Error getting list of connections", e );
    }

    // Add buttons first, then the script field can use dynamic sizing
    //
    wOk = new Button( shell, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wOk.addListener( SWT.Selection, e -> ok() );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    wCancel.addListener( SWT.Selection, e -> cancel() );

    Label wlReplaceVariables = new Label( shell, SWT.LEFT );
    wlReplaceVariables.setText( "Replace variables in script?" );
    props.setLook( wlReplaceVariables );
    FormData fdlReplaceVariables = new FormData();
    fdlReplaceVariables.left = new FormAttachment( 0, 0 );
    fdlReplaceVariables.right = new FormAttachment( middle, -margin );
    fdlReplaceVariables.bottom = new FormAttachment( wOk, -margin * 2 );
    wlReplaceVariables.setLayoutData( fdlReplaceVariables );
    wReplaceVariables = new Button( shell, SWT.CHECK | SWT.BORDER );
    props.setLook( wReplaceVariables );
    FormData fdReplaceVariables = new FormData();
    fdReplaceVariables.left = new FormAttachment( middle, 0 );
    fdReplaceVariables.right = new FormAttachment( 100, 0 );
    fdReplaceVariables.top = new FormAttachment( wlReplaceVariables, 0, SWT.CENTER );
    wReplaceVariables.setLayoutData( fdReplaceVariables );

    Label wlScript = new Label( shell, SWT.LEFT );
    wlScript.setText( "Cypher Script. Separate commands with ; on a new line." );
    props.setLook( wlScript );
    FormData fdlCypher = new FormData();
    fdlCypher.left = new FormAttachment( 0, 0 );
    fdlCypher.right = new FormAttachment( 100, 0 );
    fdlCypher.top = new FormAttachment( wConnection, margin );
    wlScript.setLayoutData( fdlCypher );
    wScript = new TextVar( variables, shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
    wScript.getTextWidget().setFont( GuiResource.getInstance().getFontFixed() );
    props.setLook( wScript );
    wScript.addModifyListener( lsMod );
    FormData fdCypher = new FormData();
    fdCypher.left = new FormAttachment( 0, 0 );
    fdCypher.right = new FormAttachment( 100, 0 );
    fdCypher.top = new FormAttachment( wlScript, margin );
    fdCypher.bottom = new FormAttachment( wReplaceVariables, -margin * 2 );
    wScript.setLayoutData( fdCypher );

    // Put these buttons at the bottom
    //
    BaseTransformDialog.positionBottomButtons( shell, new Button[] { wOk, wCancel, }, margin, null );

    // Detect X or ALT-F4 or something that kills this window...
    //
    shell.addListener( SWT.Close, e -> cancel() );
    wName.addListener( SWT.DefaultSelection, e -> ok() );

    getData();

    BaseTransformDialog.setSize( shell );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }

    return cypherScript;
  }

  private void cancel() {
    cypherScript.setChanged( changed );
    cypherScript = null;
    dispose();
  }

  private void getData() {
    wName.setText( Const.NVL( cypherScript.getName(), "" ) );
    wConnection.setText( Const.NVL( cypherScript.getConnectionName(), "" ) );
    wScript.setText( Const.NVL( cypherScript.getScript(), "" ) );
    wReplaceVariables.setSelection( cypherScript.isReplacingVariables() );
  }

  private void ok() {
    if ( Utils.isEmpty( wName.getText() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setText( "Warning" );
      mb.setMessage( "The name of the action is missing!" );
      mb.open();
      return;
    }
    cypherScript.setName( wName.getText() );
    cypherScript.setConnectionName( wConnection.getText() );
    cypherScript.setScript( wScript.getText() );
    cypherScript.setReplacingVariables( wReplaceVariables.getSelection() );

    dispose();
  }

  public void dispose() {
    props.setScreen( new WindowProperty( shell ) );
    shell.dispose();
  }
}
