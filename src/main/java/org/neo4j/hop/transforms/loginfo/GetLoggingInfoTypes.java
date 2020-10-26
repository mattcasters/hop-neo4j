/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License" );
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.neo4j.hop.transforms.loginfo;

import org.apache.hop.i18n.BaseMessages;

public enum GetLoggingInfoTypes {
  TYPE_SYSTEM_INFO_NONE( "", "" ),

  TYPE_SYSTEM_INFO_TRANS_DATE_FROM              ( "Specified transformation: Start of date range", "Types.Desc.TransStartDateRange" ),
  TYPE_SYSTEM_INFO_TRANS_DATE_TO                ( "Specified transformation: End of date range", "Types.Desc.TransEndDateRange" ),
  TYPE_SYSTEM_INFO_TRANS_PREVIOUS_EXECUTION_DATE( "Specified transformation : Previous execution date", "Types.Desc.TransPreviousExecutionDate" ),
  TYPE_SYSTEM_INFO_TRANS_PREVIOUS_SUCCESS_DATE  ( "Specified transformation : Previous success date", "Types.Desc.TransPreviousSuccessDate" ),

  TYPE_SYSTEM_INFO_JOB_DATE_FROM                ( "Specified job: Start of date range", "Types.Desc.JobStartDateRange" ),
  TYPE_SYSTEM_INFO_JOB_DATE_TO                  ( "Specified job: End of date range", "Types.Desc.JobEndDateRange" ),
  TYPE_SYSTEM_INFO_JOB_PREVIOUS_EXECUTION_DATE  ( "Specified job: Previous execution date", "Types.Desc.JobPreviousExecutionDate" ),
  TYPE_SYSTEM_INFO_JOB_PREVIOUS_SUCCESS_DATE    ( "Specified job: Previous success date", "Types.Desc.JobPreviousSuccessDate" ),
  ;

  private String code;
  private String description;

  private static Class<?> PKG = GetLoggingInfoMeta.class; // for i18n purposes, needed by Translator2!!

  public String getCode() {
    return code;
  }

  public String lookupDescription() {
    return description;
  }

  public static GetLoggingInfoTypes getTypeFromString( String typeStr ) {
    for ( GetLoggingInfoTypes type : GetLoggingInfoTypes.values() ) {
      // attempting to purge this typo from KTRs
      if ( "previous result nr lines rejeted".equalsIgnoreCase( typeStr ) ) {
        typeStr = "previous result nr lines rejected";
      }

      if ( type.toString().equals( typeStr )
        || type.code.equalsIgnoreCase( typeStr )
        || type.description.equalsIgnoreCase( typeStr ) ) {
        return type;
      }
    }

    return TYPE_SYSTEM_INFO_NONE;
  }

  public static String lookupDescription( String i18nDescription) {
    if (PKG==null) {
      PKG=GetLoggingInfoMeta.class;
    }
    return BaseMessages.getString( PKG, i18nDescription );
  }

  GetLoggingInfoTypes( String code, String i18nDescription ) {
    this.code = code;
    this.description = lookupDescription(i18nDescription);
  }
}