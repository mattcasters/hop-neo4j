package org.neo4j.hop.transforms.cypher;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.value.ValueMetaFactory;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.PipelinePreviewFactory;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformDialog;
import org.apache.hop.ui.core.dialog.EnterNumberDialog;
import org.apache.hop.ui.core.dialog.EnterTextDialog;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.dialog.PreviewRowsDialog;
import org.apache.hop.ui.core.gui.GUIResource;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.MetaSelectionLine;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.hopgui.HopGui;
import org.apache.hop.ui.pipeline.dialog.PipelinePreviewProgressDialog;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Type;
import org.neo4j.driver.util.Pair;
import org.neo4j.hop.core.data.GraphPropertyDataType;
import org.neo4j.hop.model.GraphPropertyType;
import org.neo4j.hop.shared.NeoConnection;
import org.neo4j.hop.shared.NeoConnectionUtils;
import org.neo4j.hop.transforms.output.Neo4JOutputDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CypherDialog extends BaseTransformDialog implements ITransformDialog {

  private static Class<?> PKG = CypherMeta.class; // for i18n purposes, needed by Translator2!!

  private Text wTransformName;

  private MetaSelectionLine<NeoConnection> wConnection;
  private TextVar wBatchSize;
  private Button wReadOnly;
  private Button wRetry;
  private Button wCypherFromField;
  private CCombo wCypherField;
  private Button wUnwind;
  private Label wlUnwindMap;
  private TextVar wUnwindMap;
  private Button wReturnGraph;
  private Label wlReturnGraphField;
  private TextVar wReturnGraphField;

  private Text wCypher;

  private TableView wParameters;

  private TableView wReturns;

  private CypherMeta input;

  public CypherDialog( Shell parent, Object inputMetadata, PipelineMeta pipelineMeta, String transformName ) {
    super( parent, (BaseTransformMeta) inputMetadata, pipelineMeta, transformName );
    input = (CypherMeta) inputMetadata;

    // Hack the metastore...
    //
    metaStore = HopGui.getInstance().getMetaStore();
  }

  @Override public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, input );

    FormLayout shellLayout = new FormLayout();
    shell.setLayout( shellLayout );
    shell.setText( "Cypher" );

    ModifyListener lsMod = e -> input.setChanged();
    changed = input.hasChanged();

    ScrolledComposite wScrolledComposite = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    FormLayout scFormLayout = new FormLayout();
    wScrolledComposite.setLayout( scFormLayout );
    FormData fdSComposite = new FormData();
    fdSComposite.left = new FormAttachment( 0, 0 );
    fdSComposite.right = new FormAttachment( 100, 0 );
    fdSComposite.top = new FormAttachment( 0, 0 );
    fdSComposite.bottom = new FormAttachment( 100, 0 );
    wScrolledComposite.setLayoutData( fdSComposite );

    Composite wComposite = new Composite( wScrolledComposite, SWT.NONE );
    props.setLook( wComposite );
    FormData fdComposite = new FormData();
    fdComposite.left = new FormAttachment( 0, 0 );
    fdComposite.right = new FormAttachment( 100, 0 );
    fdComposite.top = new FormAttachment( 0, 0 );
    fdComposite.bottom = new FormAttachment( 100, 0 );
    wComposite.setLayoutData( fdComposite );

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;
    wComposite.setLayout( formLayout );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Step name line
    //
    Label wlTransformName = new Label( wComposite, SWT.RIGHT );
    wlTransformName.setText( "Transform name" );
    props.setLook( wlTransformName );
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment( 0, 0 );
    fdlTransformName.right = new FormAttachment( middle, -margin );
    fdlTransformName.top = new FormAttachment( 0, margin );
    wlTransformName.setLayoutData( fdlTransformName );
    wTransformName = new Text( wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wTransformName );
    wTransformName.addModifyListener( lsMod );
    fdTransformName = new FormData();
    fdTransformName.left = new FormAttachment( middle, 0 );
    fdTransformName.top = new FormAttachment( wlTransformName, 0, SWT.CENTER );
    fdTransformName.right = new FormAttachment( 100, 0 );
    wTransformName.setLayoutData( fdTransformName );
    Control lastControl = wTransformName;

    wConnection = new MetaSelectionLine<>( pipelineMeta, metaStore, NeoConnection.class, wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER, "Neo4j Connection", "The name of the Neo4j connection to use");
    props.setLook( wConnection );
    wConnection.addModifyListener( lsMod );
    FormData fdConnection = new FormData();
    fdConnection.left = new FormAttachment( 0, 0 );
    fdConnection.right = new FormAttachment( 100, 0 );
    fdConnection.top = new FormAttachment( lastControl, margin );
    wConnection.setLayoutData( fdConnection );
    try {
      wConnection.fillItems();
    } catch(Exception e) {
      new ErrorDialog( shell, "Error", "Error getting list of connections", e );
    }
    lastControl = wConnection;

    Label wlBatchSize = new Label( wComposite, SWT.RIGHT );
    wlBatchSize.setText( "Batch size (rows)" );
    props.setLook( wlBatchSize );
    FormData fdlBatchSize = new FormData();
    fdlBatchSize.left = new FormAttachment( 0, 0 );
    fdlBatchSize.right = new FormAttachment( middle, -margin );
    fdlBatchSize.top = new FormAttachment( lastControl, 2 * margin );
    wlBatchSize.setLayoutData( fdlBatchSize );
    wBatchSize = new TextVar( pipelineMeta, wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wBatchSize );
    wBatchSize.addModifyListener( lsMod );
    FormData fdBatchSize = new FormData();
    fdBatchSize.left = new FormAttachment( middle, 0 );
    fdBatchSize.right = new FormAttachment( 100, 0 );
    fdBatchSize.top = new FormAttachment( wlBatchSize, 0, SWT.CENTER );
    wBatchSize.setLayoutData( fdBatchSize );
    lastControl = wBatchSize;

    Label wlReadOnly = new Label( wComposite, SWT.RIGHT );
    wlReadOnly.setText( "Read only statement? " );
    props.setLook( wlReadOnly );
    FormData fdlReadOnly = new FormData();
    fdlReadOnly.left = new FormAttachment( 0, 0 );
    fdlReadOnly.right = new FormAttachment( middle, -margin );
    fdlReadOnly.top = new FormAttachment( lastControl, 2 * margin );
    wlReadOnly.setLayoutData( fdlReadOnly );
    wReadOnly = new Button( wComposite, SWT.CHECK | SWT.BORDER );
    props.setLook( wReadOnly );
    FormData fdReadOnly = new FormData();
    fdReadOnly.left = new FormAttachment( middle, 0 );
    fdReadOnly.right = new FormAttachment( 100, 0 );
    fdReadOnly.top = new FormAttachment( wlReadOnly, 0, SWT.CENTER );
    wReadOnly.setLayoutData( fdReadOnly );
    lastControl = wReadOnly;

    Label wlRetry = new Label( wComposite, SWT.RIGHT );
    wlRetry.setText( "Retry connecting after disconnection? " );
    props.setLook( wlRetry );
    FormData fdlRetry = new FormData();
    fdlRetry.left = new FormAttachment( 0, 0 );
    fdlRetry.right = new FormAttachment( middle, -margin );
    fdlRetry.top = new FormAttachment( lastControl, 2 * margin );
    wlRetry.setLayoutData( fdlRetry );
    wRetry = new Button( wComposite, SWT.CHECK | SWT.BORDER );
    props.setLook( wRetry );
    FormData fdRetry = new FormData();
    fdRetry.left = new FormAttachment( middle, 0 );
    fdRetry.right = new FormAttachment( 100, 0 );
    fdRetry.top = new FormAttachment( wlRetry, 0, SWT.CENTER );
    wRetry.setLayoutData( fdRetry );
    lastControl = wRetry;


    Label wlCypherFromField = new Label( wComposite, SWT.RIGHT );
    wlCypherFromField.setText( "Get Cypher from input field? " );
    props.setLook( wlCypherFromField );
    FormData fdlCypherFromField = new FormData();
    fdlCypherFromField.left = new FormAttachment( 0, 0 );
    fdlCypherFromField.right = new FormAttachment( middle, -margin );
    fdlCypherFromField.top = new FormAttachment( lastControl, 2 * margin );
    wlCypherFromField.setLayoutData( fdlCypherFromField );
    wCypherFromField = new Button( wComposite, SWT.CHECK | SWT.BORDER );
    props.setLook( wCypherFromField );
    FormData fdCypherFromField = new FormData();
    fdCypherFromField.left = new FormAttachment( middle, 0 );
    fdCypherFromField.right = new FormAttachment( 100, 0 );
    fdCypherFromField.top = new FormAttachment( wlCypherFromField, 0, SWT.CENTER );
    wCypherFromField.setLayoutData( fdCypherFromField );
    lastControl = wCypherFromField;

    wCypherFromField.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        enableFields();
      }
    } );

    Label wlCypherField = new Label( wComposite, SWT.RIGHT );
    wlCypherField.setText( "Cypher input field" );
    props.setLook( wlCypherField );
    FormData fdlCypherField = new FormData();
    fdlCypherField.left = new FormAttachment( 0, 0 );
    fdlCypherField.right = new FormAttachment( middle, -margin );
    fdlCypherField.top = new FormAttachment( lastControl, 2 * margin );
    wlCypherField.setLayoutData( fdlCypherField );
    wCypherField = new CCombo( wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wCypherField );
    wCypherField.addModifyListener( lsMod );
    FormData fdCypherField = new FormData();
    fdCypherField.left = new FormAttachment( middle, 0 );
    fdCypherField.right = new FormAttachment( 100, 0 );
    fdCypherField.top = new FormAttachment( wlCypherField, 0, SWT.CENTER );
    wCypherField.setLayoutData( fdCypherField );
    lastControl = wCypherField;

    Label wlUnwind = new Label( wComposite, SWT.RIGHT );
    wlUnwind.setText( "Collect parameter values map?" );
    wlUnwind.setToolTipText( "Collect the specified parameters field data and expose it into a single variable to support UNWIND statements" );
    props.setLook( wlUnwind );
    FormData fdlUnwind = new FormData();
    fdlUnwind.left = new FormAttachment( 0, 0 );
    fdlUnwind.right = new FormAttachment( middle, -margin );
    fdlUnwind.top = new FormAttachment( lastControl, 2 * margin );
    wlUnwind.setLayoutData( fdlUnwind );
    wUnwind = new Button( wComposite, SWT.CHECK | SWT.BORDER );
    props.setLook( wUnwind );
    FormData fdUnwind = new FormData();
    fdUnwind.left = new FormAttachment( middle, 0 );
    fdUnwind.right = new FormAttachment( 100, 0 );
    fdUnwind.top = new FormAttachment( wlUnwind, 0, SWT.CENTER );
    wUnwind.setLayoutData( fdUnwind );
    lastControl = wUnwind;
    wUnwind.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        enableFields();
      }
    } );

    wlUnwindMap = new Label( wComposite, SWT.RIGHT );
    wlUnwindMap.setText( "Name of values map list" );
    wlUnwindMap.setToolTipText( "You can use this parameter in your Cypher usually in UNWIND statements" );
    props.setLook( wlUnwindMap );
    FormData fdlUnwindMap = new FormData();
    fdlUnwindMap.left = new FormAttachment( 0, 0 );
    fdlUnwindMap.right = new FormAttachment( middle, -margin );
    fdlUnwindMap.top = new FormAttachment( lastControl, 2 * margin );
    wlUnwindMap.setLayoutData( fdlUnwindMap );
    wUnwindMap = new TextVar( pipelineMeta, wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wUnwindMap );
    wUnwindMap.addModifyListener( lsMod );
    FormData fdUnwindMap = new FormData();
    fdUnwindMap.left = new FormAttachment( middle, 0 );
    fdUnwindMap.right = new FormAttachment( 100, 0 );
    fdUnwindMap.top = new FormAttachment( wlUnwindMap, 0, SWT.CENTER );
    wUnwindMap.setLayoutData( fdUnwindMap );
    lastControl = wUnwindMap;

    Label wlReturnGraph = new Label( wComposite, SWT.RIGHT );
    wlReturnGraph.setText( "Return graph data?" );
    String returnGraphTooltipText = "Returns the whole result of a query as a Graph Hop data type";
    wlReturnGraph.setToolTipText( returnGraphTooltipText );
    props.setLook( wlReturnGraph );
    FormData fdlReturnGraph = new FormData();
    fdlReturnGraph.left = new FormAttachment( 0, 0 );
    fdlReturnGraph.right = new FormAttachment( middle, -margin );
    fdlReturnGraph.top = new FormAttachment( lastControl, 2 * margin );
    wlReturnGraph.setLayoutData( fdlReturnGraph );
    wReturnGraph = new Button( wComposite, SWT.CHECK | SWT.BORDER );
    wReturnGraph.setToolTipText( returnGraphTooltipText );
    props.setLook( wReturnGraph );
    FormData fdReturnGraph = new FormData();
    fdReturnGraph.left = new FormAttachment( middle, 0 );
    fdReturnGraph.right = new FormAttachment( 100, 0 );
    fdReturnGraph.top = new FormAttachment( wlReturnGraph, 0, SWT.CENTER );
    wReturnGraph.setLayoutData( fdReturnGraph );
    lastControl = wReturnGraph;
    wReturnGraph.addSelectionListener( new SelectionAdapter() {
      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
        enableFields();
      }
    } );

    wlReturnGraphField = new Label( wComposite, SWT.RIGHT );
    wlReturnGraphField.setText( "Graph output field name" );
    props.setLook( wlReturnGraphField );
    FormData fdlReturnGraphField = new FormData();
    fdlReturnGraphField.left = new FormAttachment( 0, 0 );
    fdlReturnGraphField.right = new FormAttachment( middle, -margin );
    fdlReturnGraphField.top = new FormAttachment( lastControl, 2 * margin );
    wlReturnGraphField.setLayoutData( fdlReturnGraphField );
    wReturnGraphField = new TextVar( pipelineMeta, wComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wReturnGraphField );
    wReturnGraphField.addModifyListener( lsMod );
    FormData fdReturnGraphField = new FormData();
    fdReturnGraphField.left = new FormAttachment( middle, 0 );
    fdReturnGraphField.right = new FormAttachment( 100, 0 );
    fdReturnGraphField.top = new FormAttachment( wlReturnGraphField, 0, SWT.CENTER );
    wReturnGraphField.setLayoutData( fdReturnGraphField );
    lastControl = wReturnGraphField;

    Label wlCypher = new Label( wComposite, SWT.LEFT );
    wlCypher.setText( "Cypher:" );
    props.setLook( wlCypher );
    FormData fdlCypher = new FormData();
    fdlCypher.left = new FormAttachment( 0, 0 );
    fdlCypher.right = new FormAttachment( middle, -margin );
    fdlCypher.top = new FormAttachment( lastControl, margin );
    wlCypher.setLayoutData( fdlCypher );
    wCypher = new Text( wComposite, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
    wCypher.setFont( GUIResource.getInstance().getFontFixed() );
    props.setLook( wCypher );
    wCypher.addModifyListener( lsMod );
    FormData fdCypher = new FormData();
    fdCypher.left = new FormAttachment( 0, 0 );
    fdCypher.right = new FormAttachment( 100, 0 );
    fdCypher.top = new FormAttachment( wlCypher, margin );
    fdCypher.bottom = new FormAttachment( 60, 0 );
    wCypher.setLayoutData( fdCypher );
    lastControl = wCypher;


    // Some buttons
    wOk = new Button( wComposite, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wPreview = new Button( wComposite, SWT.PUSH );
    wPreview.setText( BaseMessages.getString( PKG, "System.Button.Preview" ) );
    wCancel = new Button( wComposite, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    // Position the buttons at the bottom of the dialog.
    //
    setButtonPositions( new Button[] { wOk, wPreview, wCancel }, margin, null );

    String[] fieldNames;
    try {
      fieldNames = pipelineMeta.getPrevTransformFields( transformName ).getFieldNames();
    } catch ( Exception e ) {
      logError( "Unable to get fields from previous steps", e );
      fieldNames = new String[] {};
    }

    // Table: parameter and field
    //
    ColumnInfo[] parameterColumns =
      new ColumnInfo[] {
        new ColumnInfo( "Parameter", ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo( "Field", ColumnInfo.COLUMN_TYPE_CCOMBO, fieldNames, false ),
        new ColumnInfo( "Neo4j Type", ColumnInfo.COLUMN_TYPE_CCOMBO, GraphPropertyType.getNames(), false ),
      };

    Label wlParameters = new Label( wComposite, SWT.LEFT );
    wlParameters.setText( "Parameters: (NOTE that parameters for labels are not supported)" );
    props.setLook( wlParameters );
    FormData fdlParameters = new FormData();
    fdlParameters.left = new FormAttachment( 0, 0 );
    fdlParameters.right = new FormAttachment( 100, 0 );
    fdlParameters.top = new FormAttachment( lastControl, margin );
    wlParameters.setLayoutData( fdlParameters );

    Button wbGetParameters = new Button( wComposite, SWT.PUSH );
    wbGetParameters.setText( "Get parameters" );
    FormData fdbGetParameters = new FormData();
    fdbGetParameters.right = new FormAttachment( 100, 0 );
    fdbGetParameters.top = new FormAttachment( wlParameters, margin );
    wbGetParameters.setLayoutData( fdbGetParameters );
    wbGetParameters.addListener( SWT.Selection, ( e ) -> {
      try {
        IRowMeta r = pipelineMeta.getPrevTransformFields( transformMeta );

        BaseTransformDialog.getFieldsFromPrevious( r, wParameters, 2, new int[] { 2 }, new int[] {}, -1, -1,
          ( item, valueMeta ) -> Neo4JOutputDialog.getPropertyNameTypePrimary( item, valueMeta, new int[] { 1 }, new int[] { 3 }, -1 )
        );
      } catch ( Exception ex ) {
        new ErrorDialog( shell, "Error", "Error getting step input fields", ex );
      }
    } );

    wParameters = new TableView( pipelineMeta, wComposite, SWT.FULL_SELECTION | SWT.MULTI, parameterColumns,
      input.getParameterMappings().size(), lsMod, props );
    props.setLook( wParameters );
    wParameters.addModifyListener( lsMod );
    FormData fdParameters = new FormData();
    fdParameters.left = new FormAttachment( 0, 0 );
    fdParameters.right = new FormAttachment( wbGetParameters, -margin );
    fdParameters.top = new FormAttachment( wlParameters, margin );
    fdParameters.bottom = new FormAttachment( wlParameters, 300 + margin );
    wParameters.setLayoutData( fdParameters );
    lastControl = wParameters;


    // Table: return field name and type TODO Support more than String
    //
    ColumnInfo[] returnColumns =
      new ColumnInfo[] {
        new ColumnInfo( "Field name", ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo( "Return type", ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMetaFactory.getAllValueMetaNames(), false ),
        new ColumnInfo( "Source type", ColumnInfo.COLUMN_TYPE_CCOMBO, GraphPropertyDataType.getNames(), false ),
      };

    Label wlReturns = new Label( wComposite, SWT.LEFT );
    wlReturns.setText( "Returns" );
    props.setLook( wlReturns );
    FormData fdlReturns = new FormData();
    fdlReturns.left = new FormAttachment( 0, 0 );
    fdlReturns.right = new FormAttachment( middle, -margin );
    fdlReturns.top = new FormAttachment( lastControl, margin );
    wlReturns.setLayoutData( fdlReturns );

    Button wbGetReturnFields = new Button( wComposite, SWT.PUSH );
    wbGetReturnFields.setText( "Get Output Fields" );
    FormData fdbGetReturnFields = new FormData();
    fdbGetReturnFields.right = new FormAttachment( 100, 0 );
    fdbGetReturnFields.top = new FormAttachment( wlReturns, margin );
    wbGetReturnFields.setLayoutData( fdbGetReturnFields );
    wbGetReturnFields.addListener( SWT.Selection, ( e ) -> getReturnValues() );

    wReturns = new TableView( pipelineMeta, wComposite, SWT.FULL_SELECTION | SWT.MULTI, returnColumns, input.getReturnValues().size(), lsMod, props );
    props.setLook( wReturns );
    wReturns.addModifyListener( lsMod );
    FormData fdReturns = new FormData();
    fdReturns.left = new FormAttachment( 0, 0 );
    fdReturns.right = new FormAttachment( wbGetReturnFields, 0 );
    fdReturns.top = new FormAttachment( wlReturns, margin );
    fdReturns.bottom = new FormAttachment( wlReturns, 300 + margin );
    wReturns.setLayoutData( fdReturns );
    // lastControl = wReturns;

    wComposite.pack();
    Rectangle bounds = wComposite.getBounds();

    wScrolledComposite.setContent( wComposite );

    wScrolledComposite.setExpandHorizontal( true );
    wScrolledComposite.setExpandVertical( true );
    wScrolledComposite.setMinWidth( bounds.width );
    wScrolledComposite.setMinHeight( bounds.height );

    // Add listeners
    //
    wCancel.addListener( SWT.Selection, e -> cancel() );
    wOk.addListener( SWT.Selection, e -> ok() );
    wPreview.addListener( SWT.Selection, e -> preview() );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wConnection.addSelectionListener( lsDef );
    wTransformName.addSelectionListener( lsDef );
    wBatchSize.addSelectionListener( lsDef );
    wReadOnly.addSelectionListener( lsDef );
    wRetry.addSelectionListener( lsDef );
    wCypherFromField.addSelectionListener( lsDef );
    wCypherField.addSelectionListener( lsDef );
    wUnwind.addSelectionListener( lsDef );
    wUnwindMap.addSelectionListener( lsDef );
    wReturnGraphField.addSelectionListener( lsDef );


    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Set the shell size, based upon previous time...
    setSize();

    getData();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return transformName;

  }

  private void enableFields() {
    boolean fromField = wCypherFromField.getSelection();

    wCypher.setEnabled( !fromField );
    wCypherField.setEnabled( fromField );

    boolean usingUnwind = wUnwind.getSelection();

    wlUnwindMap.setEnabled( usingUnwind );
    wUnwindMap.setEnabled( usingUnwind );

    boolean returningGraph = wReturnGraph.getSelection();

    wlReturnGraphField.setEnabled( returningGraph );
    wReturnGraphField.setEnabled( returningGraph );
  }

  private void cancel() {
    transformName = null;
    input.setChanged( changed );
    dispose();
  }

  public void getData() {

    wTransformName.setText( Const.NVL( transformName, "" ) );
    wConnection.setText( Const.NVL( input.getConnectionName(), "" ) );

    wReadOnly.setSelection( input.isReadOnly() );
    wRetry.setSelection( input.isRetrying() );
    wCypherFromField.setSelection( input.isCypherFromField() );
    wCypherField.setText( Const.NVL( input.getCypherField(), "" ) );
    try {
      wCypherField.setItems( pipelineMeta.getPrevTransformFields( transformName ).getFieldNames() );
    } catch ( HopTransformException e ) {
      log.logError( "Error getting fields from previous steps", e );
    }

    wUnwind.setSelection( input.isUsingUnwind() );
    wUnwindMap.setText( Const.NVL( input.getUnwindMapName(), "" ) );
    wReturnGraph.setSelection( input.isReturningGraph() );
    wReturnGraphField.setText( Const.NVL( input.getReturnGraphField(), "" ) );
    wBatchSize.setText( Const.NVL( input.getBatchSize(), "" ) );
    wCypher.setText( Const.NVL( input.getCypher(), "" ) );

    for ( int i = 0; i < input.getParameterMappings().size(); i++ ) {
      ParameterMapping mapping = input.getParameterMappings().get( i );
      TableItem item = wParameters.table.getItem( i );
      item.setText( 1, Const.NVL( mapping.getParameter(), "" ) );
      item.setText( 2, Const.NVL( mapping.getField(), "" ) );
      item.setText( 3, Const.NVL( mapping.getNeoType(), "" ) );
    }
    wParameters.removeEmptyRows();
    wParameters.setRowNums();
    wParameters.optWidth( true );

    for ( int i = 0; i < input.getReturnValues().size(); i++ ) {
      ReturnValue returnValue = input.getReturnValues().get( i );
      TableItem item = wReturns.table.getItem( i );
      item.setText( 1, Const.NVL( returnValue.getName(), "" ) );
      item.setText( 2, Const.NVL( returnValue.getType(), "" ) );
      item.setText( 3, Const.NVL( returnValue.getSourceType(), "" ) );
    }
    wReturns.removeEmptyRows();
    wReturns.setRowNums();
    wReturns.optWidth( true );

    enableFields();
  }

  private void ok() {
    if ( StringUtils.isEmpty( wTransformName.getText() ) ) {
      return;
    }
    transformName = wTransformName.getText(); // return value
    getInfo( input );
    dispose();
  }

  private void getInfo( CypherMeta meta ) {
    meta.setConnectionName( wConnection.getText() );
    meta.setBatchSize( wBatchSize.getText() );
    meta.setCypher( wCypher.getText() );

    meta.setReadOnly( wReadOnly.getSelection() );
    meta.setRetrying( wRetry.getSelection() );
    meta.setCypherFromField( wCypherFromField.getSelection() );
    meta.setCypherField( wCypherField.getText() );

    meta.setUsingUnwind( wUnwind.getSelection() );
    meta.setUnwindMapName( wUnwindMap.getText() );

    meta.setReturningGraph( wReturnGraph.getSelection() );
    meta.setReturnGraphField( wReturnGraphField.getText() );

    List<ParameterMapping> mappings = new ArrayList<>();
    for ( int i = 0; i < wParameters.nrNonEmpty(); i++ ) {
      TableItem item = wParameters.getNonEmpty( i );
      mappings.add( new ParameterMapping( item.getText( 1 ), item.getText( 2 ), item.getText( 3 ) ) );
    }
    meta.setParameterMappings( mappings );

    List<ReturnValue> returnValues = new ArrayList<>();
    for ( int i = 0; i < wReturns.nrNonEmpty(); i++ ) {
      TableItem item = wReturns.getNonEmpty( i );
      returnValues.add( new ReturnValue( item.getText( 1 ), item.getText( 2 ), item.getText( 3 ) ) );
    }
    meta.setReturnValues( returnValues );
  }

  private synchronized void preview() {
    CypherMeta oneMeta = new CypherMeta();
    this.getInfo( oneMeta );
    PipelineMeta previewMeta = PipelinePreviewFactory.generatePreviewPipeline( this.pipelineMeta, oneMeta, this.wTransformName.getText() );
    this.pipelineMeta.getVariable( "Internal.Transformation.Filename.Directory" );
    previewMeta.getVariable( "Internal.Transformation.Filename.Directory" );
    EnterNumberDialog
      numberDialog = new EnterNumberDialog( this.shell, this.props.getDefaultPreviewSize(),
      BaseMessages.getString( PKG, "CypherDialog.PreviewSize.DialogTitle" ),
      BaseMessages.getString( PKG, "CypherDialog.PreviewSize.DialogMessage" )
    );
    int previewSize = numberDialog.open();
    if ( previewSize > 0 ) {
      PipelinePreviewProgressDialog progressDialog = new PipelinePreviewProgressDialog( this.shell, previewMeta, new String[] { this.wTransformName.getText() }, new int[] { previewSize } );
      progressDialog.open();
      Pipeline pipeline = progressDialog.getPipeline();
      String loggingText = progressDialog.getLoggingText();
      if ( !progressDialog.isCancelled() && pipeline.getResult() != null && pipeline.getResult().getNrErrors() > 0L ) {
        EnterTextDialog etd = new EnterTextDialog( this.shell,
          BaseMessages.getString( PKG, "System.Dialog.PreviewError.Title", new String[ 0 ] ),
          BaseMessages.getString( PKG, "System.Dialog.PreviewError.Message", new String[ 0 ] ), loggingText, true );
        etd.setReadOnly();
        etd.open();
      }

      PreviewRowsDialog prd = new PreviewRowsDialog( this.shell, this.pipelineMeta, 0, this.wTransformName.getText(), progressDialog.getPreviewRowsMeta( this.wTransformName.getText() ),
        progressDialog.getPreviewRows( this.wTransformName.getText() ), loggingText );
      prd.open();
    }
  }

  private void getReturnValues() {

    IMetaStore metaStore = HopGui.getInstance().getMetaStore();
    Driver driver = null;
    Session session = null;
    Transaction transaction = null;

    CypherMeta meta = new CypherMeta();
    getInfo( meta );

    try {
      NeoConnection neoConnection = NeoConnectionUtils.getConnectionFactory( metaStore ).loadElement( meta.getConnectionName() );
      neoConnection.initializeVariablesFrom( pipelineMeta );
      driver = neoConnection.getDriver( log );
      session = driver.session();
      transaction = session.beginTransaction();
      Map<String, Object> parameters = new HashMap<>();
      for ( ParameterMapping mapping : meta.getParameterMappings() ) {
        parameters.put( pipelineMeta.environmentSubstitute( mapping.getParameter() ), "" );
      }
      String cypher = pipelineMeta.environmentSubstitute( wCypher.getText() );
      Result result = transaction.run( cypher, parameters );

      // Evaluate the result
      //
      if ( result.hasNext() ) {
        Record record = result.next();
        List<Pair<String, Value>> fields = record.fields();
        for ( Pair<String, Value> fieldPair : fields ) {
          String returnField = fieldPair.key();
          Value returnValue = fieldPair.value();
          Type valueType = returnValue.type();

          String typeName = valueType.name().replaceAll( "_", "" );
          GraphPropertyDataType type = GraphPropertyDataType.parseCode( typeName );
          int kettleType = type.getHopType();

          TableItem item = new TableItem( wReturns.table, SWT.NONE );
          item.setText( 1, returnField );
          item.setText( 2, ValueMetaFactory.getValueMetaName( kettleType ) );
          item.setText( 3, type.name() );
        }
      }
      wReturns.removeEmptyRows();
      wReturns.setRowNums();
      wReturns.optWidth( true );
    } catch ( Exception e ) {
      new ErrorDialog( shell, "Error", "Error getting return values from Cypher statement", e );
    } finally {
      if ( transaction != null ) {
        transaction.rollback();
        transaction.close();
      }
      if ( session != null ) {
        session.close();
      }
      if ( driver != null ) {
        driver.close();
      }
    }
  }
}
