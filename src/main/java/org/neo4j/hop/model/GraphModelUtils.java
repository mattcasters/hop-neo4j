package org.neo4j.hop.model;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.metastore.persist.MetaStoreFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.neo4j.hop.core.Neo4jDefaults;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.metastore.api.IMetaStore;
import org.neo4j.hop.ui.model.GraphModelDialog;

public class GraphModelUtils {
  private static Class<?> PKG = GraphModelUtils.class; // for i18n purposes, needed by Translator2!!

  public static GraphModel newModel( Shell shell, MetaStoreFactory<GraphModel> factory, IRowMeta inputRowMeta ) {
    GraphModel graphModel = new GraphModel();
    boolean ok = false;
    while ( !ok ) {
      GraphModelDialog dialog = new GraphModelDialog( shell, graphModel, inputRowMeta );
      if ( dialog.open()!=null ) {

        // write to metastore...
        //
        try {
          if ( factory.loadElement( graphModel.getName() ) != null ) {
            MessageBox box = new MessageBox( shell, SWT.YES | SWT.NO | SWT.ICON_ERROR );
            box.setText( BaseMessages.getString( PKG, "GraphModelUtils.Error.ModelExists.Title" ) );
            box.setMessage( BaseMessages.getString( PKG, "GraphModelUtils.Error.ModelExists.Message" ) );
            int answer = box.open();
            if ( ( answer & SWT.YES ) != 0 ) {
              factory.saveElement( graphModel );
              ok = true;
            }
          } else {
            factory.saveElement( graphModel );
            ok = true;
          }
        } catch ( Exception exception ) {
          new ErrorDialog( shell,
            BaseMessages.getString( PKG, "GraphModelUtils.Error.ErrorSavingModel.Title" ),
            BaseMessages.getString( PKG, "GraphModelUtils.Error.ErrorSavingModel.Message" ),
            exception );
          return null;
        }
      } else {
        // Cancel
        return null;
      }
    }
    return graphModel;
  }

  public static void editModel( Shell shell, MetaStoreFactory<GraphModel> factory, String modelName, IRowMeta inputRowMeta ) {
    if ( StringUtils.isEmpty( modelName ) ) {
      return;
    }
    try {
      GraphModel GraphModel = factory.loadElement( modelName );
      if ( GraphModel == null ) {
        newModel( shell, factory, inputRowMeta );
      } else {
        GraphModelDialog GraphModelDialog = new GraphModelDialog( shell, GraphModel, inputRowMeta );
        if ( GraphModelDialog.open()!=null ) {
          factory.saveElement( GraphModel );
        }
      }
    } catch ( Exception exception ) {
      new ErrorDialog( shell,
        BaseMessages.getString( PKG, "GraphModelUtils.Error.ErrorEditingModel.Title" ),
        BaseMessages.getString( PKG, "GraphModelUtils.Error.ErrorEditingModel.Message" ),
        exception );
    }
  }

  public static void deleteModel( Shell shell, MetaStoreFactory<GraphModel> factory, String connectionName ) {
    if ( StringUtils.isEmpty( connectionName ) ) {
      return;
    }

    MessageBox box = new MessageBox( shell, SWT.YES | SWT.NO | SWT.ICON_ERROR );
    box.setText( BaseMessages.getString( PKG, "GraphModelUtils.DeleteModelConfirmation.Title" ) );
    box.setMessage( BaseMessages.getString( PKG, "GraphModelUtils.DeleteModelConfirmation.Message", connectionName ) );
    int answer = box.open();
    if ( ( answer & SWT.YES ) != 0 ) {
      try {
        factory.deleteElement( connectionName );
      } catch ( Exception exception ) {
        new ErrorDialog( shell,
          BaseMessages.getString( PKG, "GraphModelUtils.Error.ErrorDeletingModel.Title" ),
          BaseMessages.getString( PKG, "GraphModelUtils.Error.ErrorDeletingModel.Message", connectionName ),
          exception );
      }
    }
  }

}