/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.trans.dataservice.streaming.ui;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.dataservice.ui.controller.AbstractController;
import org.pentaho.di.trans.dataservice.ui.model.DataServiceModel;
import org.pentaho.di.trans.dataservice.utils.DataServiceConstants;
import org.pentaho.di.trans.dataservice.utils.KettleUtils;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.components.XulRadio;
import org.pentaho.ui.xul.components.XulTab;
import org.pentaho.ui.xul.components.XulTextbox;

import java.lang.reflect.InvocationTargetException;

/**
 *Streaming UI controller
 */
public class StreamingController extends AbstractController {
  private static final String NAME = "streamingCtrl";
  KettleUtils kettleUtils = KettleUtils.getInstance();

  public StreamingController() {
    setName( NAME );
  }

  /**
   * Inits the controller-ui bindings.
   *
   * @param model - The {@link org.pentaho.di.trans.dataservice.ui.model.DataServiceModel} dataservice model.
   * @throws InvocationTargetException
   * @throws XulException
   */
  public void initBindings( DataServiceModel model ) throws InvocationTargetException, XulException, KettleException {
    BindingFactory bindingFactory = getBindingFactory();

    XulRadio streamingRadioButton = getElementById( "streaming-type-radio" );
    XulRadio normalModeRadio = getElementById( "regular-type-radio" );
    XulTab streamingTab = getElementById( "streaming-tab" );

    XulTextbox streamingMaxRows = getElementById( "streaming-max-rows" );
    XulTextbox streamingMaxTime = getElementById( "streaming-max-time" );

    bindingFactory.setBindingType( Binding.Type.BI_DIRECTIONAL );

    // Set streaming max rows and time limit value on the text boxes.
    // Get the values from the model first and fallback to the appropriate Kettle Properties
    // if they don't exist in the mode.
    int modelStreamingMaxRows = model.getServiceMaxRows() > 0
      ? model.getServiceMaxRows()
      : Integer.valueOf( kettleUtils.getKettleProperty( "KETTLE_STREAMING_ROW_LIMIT",
          Integer.toString( DataServiceConstants.KETTLE_STREAMING_ROW_LIMIT ) )  );
    streamingMaxRows.setValue( Integer.toString( modelStreamingMaxRows ) );

    model.setServiceMaxRows( modelStreamingMaxRows );

    long modelStreamingMaxTime = model.getServiceMaxTime() > 0
      ? model.getServiceMaxTime()
      : Long.parseLong( kettleUtils.getKettleProperty( "KETTLE_STREAMING_TIME_LIMIT",
          Integer.toString( DataServiceConstants.KETTLE_STREAMING_TIME_LIMIT ) )  );
    streamingMaxTime.setValue( Long.toString( modelStreamingMaxTime ) );

    model.setServiceMaxTime( modelStreamingMaxTime );

    streamingTab.setVisible( model.isStreaming() );

    bindingFactory.createBinding( model, "serviceMaxRows", streamingMaxRows, "value",
      BindingConvertor.integer2String() );

    bindingFactory.createBinding( model, "serviceMaxTime", streamingMaxTime, "value",
      BindingConvertor.long2String() );

    bindingFactory.createBinding( streamingRadioButton, "selected", streamingTab, "visible" );
    bindingFactory.createBinding( normalModeRadio, "!selected", streamingTab, "visible" );
  }
}
