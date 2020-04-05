package org.neo4j.hop.entries.check;

import org.apache.hop.core.Const;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.job.JobMeta;
import org.apache.hop.job.entry.IJobEntry;
import org.apache.hop.job.entry.IJobEntryDialog;
import org.apache.hop.metastore.api.exceptions.MetaStoreException;
import org.apache.hop.metastore.persist.MetaStoreFactory;
import org.apache.hop.pipeline.transform.BaseTransform;
import org.apache.hop.ui.core.gui.WindowProperty;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.hopgui.HopGui;
import org.apache.hop.ui.job.dialog.JobDialog;
import org.apache.hop.ui.job.entry.JobEntryDialog;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
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
import org.neo4j.hop.core.Neo4jDefaults;
import org.neo4j.hop.shared.NeoConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckConnectionsDialog extends JobEntryDialog implements IJobEntryDialog {

  public static final String CHECK_CONNECTIONS_DIALOG = "Neo4jCheckConnectionsDialog";
  private static Class<?> PKG = CheckConnectionsDialog.class; // for i18n purposes, needed by Translator2!!

  private Shell shell;

  private CheckConnections jobEntry;

  private boolean changed;

  private Text wName;
  private TableView wConnections;

  private Button wOK, wCancel;

  private String[] availableConnectionNames;
  private MetaStoreFactory<NeoConnection> connectionFactory;

  public CheckConnectionsDialog( Shell parent, IJobEntry jobEntry,JobMeta jobMeta ) {
    super( parent, jobEntry, jobMeta );
    this.jobEntry = (CheckConnections) jobEntry;
    connectionFactory = new MetaStoreFactory<>( NeoConnection.class, HopGui.getInstance().getMetaStore(), Neo4jDefaults.NAMESPACE );

    if ( this.jobEntry.getName() == null ) {
      this.jobEntry.setName( "Check Neo4j Connections" );
    }
  }

  @Override public IJobEntry open() {

    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, props.getJobsDialogStyle() );
    props.setLook( shell );
    JobDialog.setShellImage( shell, jobEntry );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        jobEntry.setChanged();
      }
    };
    changed = jobEntry.hasChanged();

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
    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wOK.addListener( SWT.Selection, e -> ok() );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    wCancel.addListener( SWT.Selection, e -> cancel() );

    // Put these buttons at the bottom
    //
    BaseTransformDialog.positionBottomButtons( shell, new Button[] { wOK, wCancel, }, margin, null );

    try {
      List<String> names = connectionFactory.getElementNames();
      Collections.sort( names );
      availableConnectionNames = names.toArray( new String[ 0 ] );
    } catch ( MetaStoreException e ) {
      availableConnectionNames = new String[] {};
    }

    ColumnInfo[] columns = new ColumnInfo[] {
      new ColumnInfo( "Connection name", ColumnInfo.COLUMN_TYPE_CCOMBO, availableConnectionNames, false ),
    };
    wConnections = new TableView( jobEntry, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, columns,
      jobEntry.getConnectionNames().size(), false, lsMod, props );
    FormData fdConnections = new FormData();
    fdConnections.left = new FormAttachment( 0, 0 );
    fdConnections.top = new FormAttachment( wName, margin );
    fdConnections.right = new FormAttachment( 100, 0 );
    fdConnections.bottom = new FormAttachment( wOK, -margin * 2 );
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

    return jobEntry;
  }

  private void cancel() {
    jobEntry.setChanged( changed );
    jobEntry = null;
    dispose();
  }

  private void getData() {
    wName.setText( Const.NVL( jobEntry.getName(), "" ) );
    wConnections.removeAll();
    for ( int i = 0; i < jobEntry.getConnectionNames().size(); i++ ) {
      wConnections.add( Const.NVL( jobEntry.getConnectionNames().get( i ), "" ) );
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
    jobEntry.setName( wName.getText() );

    int nrItems = wConnections.nrNonEmpty();
    jobEntry.setConnectionNames( new ArrayList<>() );
    for ( int i = 0; i < nrItems; i++ ) {
      String connectionName = wConnections.getNonEmpty( i ).getText( 1 );
      jobEntry.getConnectionNames().add( connectionName );
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
