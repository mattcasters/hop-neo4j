package org.neo4j.hop.actions.check;

import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.ui.core.gui.WindowProperty;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.TableView;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neo4j.hop.shared.NeoConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckConnectionsDialog extends ActionDialog implements IActionDialog {

  public static final String CHECK_CONNECTIONS_DIALOG = "Neo4jCheckConnectionsDialog";
  private static Class<?> PKG = CheckConnectionsDialog.class; // for i18n purposes, needed by Translator2!!

  private Shell shell;

  private CheckConnections action;

  private boolean changed;

  private Text wName;
  private TableView wConnections;

  private Button wOk, wCancel;

  private String[] availableConnectionNames;

  public CheckConnectionsDialog( Shell parent, IAction action, WorkflowMeta workflowMeta ) {
    super( parent, action, workflowMeta );
    this.action = (CheckConnections) action;

    if ( this.action.getName() == null ) {
      this.action.setName( "Check Neo4j Connections" );
    }
  }

  @Override public IAction open() {

    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
    props.setLook( shell );
    WorkflowDialog.setShellImage( shell, action );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        action.setChanged();
      }
    };
    changed = action.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( "Check Neo4j Connections" );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    Label wlName = new Label( shell, SWT.RIGHT );
    wlName.setText( "Job entry name" );
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

    Label wlConnections = new Label( shell, SWT.RIGHT );
    wlConnections.setText( "Neo4j Connections:" );
    props.setLook( wlConnections );
    FormData fdlConnections = new FormData();
    fdlConnections.left = new FormAttachment( 0, 0 );
    fdlConnections.right = new FormAttachment( middle, -margin );
    fdlConnections.top = new FormAttachment( 0, margin );
    wlConnections.setLayoutData( fdlConnections );

    // Add buttons first, then the list of connections dynamically sizing
    //
    wOk = new Button( shell, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wOk.addListener( SWT.Selection, e -> ok() );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    wCancel.addListener( SWT.Selection, e -> cancel() );

    // Put these buttons at the bottom
    //
    BaseTransformDialog.positionBottomButtons( shell, new Button[] { wOk, wCancel, }, margin, null );

    try {
      IHopMetadataSerializer<NeoConnection> connectionSerializer = metadataProvider.getSerializer( NeoConnection.class );
      List<String> names = connectionSerializer.listObjectNames();
      Collections.sort( names );
      availableConnectionNames = names.toArray( new String[ 0 ] );
    } catch ( HopException e ) {
      availableConnectionNames = new String[] {};
    }

    ColumnInfo[] columns = new ColumnInfo[] {
      new ColumnInfo( "Connection name", ColumnInfo.COLUMN_TYPE_CCOMBO, availableConnectionNames, false ),
    };
    wConnections = new TableView( action, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, columns,
      action.getConnectionNames().size(), false, lsMod, props );
    FormData fdConnections = new FormData();
    fdConnections.left = new FormAttachment( 0, 0 );
    fdConnections.top = new FormAttachment( wName, margin );
    fdConnections.right = new FormAttachment( 100, 0 );
    fdConnections.bottom = new FormAttachment( wOk, -margin * 2 );
    wConnections.setLayoutData( fdConnections );

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

    return action;
  }

  private void cancel() {
    action.setChanged( changed );
    action = null;
    dispose();
  }

  private void getData() {
    wName.setText( Const.NVL( action.getName(), "" ) );
    wConnections.removeAll();
    for ( int i = 0; i < action.getConnectionNames().size(); i++ ) {
      wConnections.add( Const.NVL( action.getConnectionNames().get( i ), "" ) );
    }
    wConnections.removeEmptyRows();
    wConnections.setRowNums();
    wConnections.optWidth( true );
  }

  private void ok() {
    if ( Utils.isEmpty( wName.getText() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setText( "Warning" );
      mb.setMessage( "The name of the job entry is missing!" );
      mb.open();
      return;
    }
    action.setName( wName.getText() );

    int nrItems = wConnections.nrNonEmpty();
    action.setConnectionNames( new ArrayList<>() );
    for ( int i = 0; i < nrItems; i++ ) {
      String connectionName = wConnections.getNonEmpty( i ).getText( 1 );
      action.getConnectionNames().add( connectionName );
    }

    dispose();
  }

  public void dispose() {
    props.setScreen( new WindowProperty( shell ) );
    shell.dispose();
  }

  public boolean evaluates() {
    return true;
  }

  public boolean isUnconditional() {
    return false;
  }

}
