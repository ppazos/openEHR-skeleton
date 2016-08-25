package com.cabolabs.openehr.skeleton.model.datatypes

import com.cabolabs.openehr.skeleton.model.Element

class DvCodedText extends DvText {

   String codeString
   String terminologyIdName
   String terminologyIdVersion
   
   static belongsTo = [Element]
   
   static constraints = {
      terminologyIdVersion (nullable: true)
      terminologyIdName (nullable: false)
      codeString (nullable: false)
      value (nullable: false)
   }
}
