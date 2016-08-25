package com.cabolabs.openehr.skeleton.model.datatypes

import com.cabolabs.openehr.skeleton.model.Element

class DvText extends DataValue {

   String value
      
   static belongsTo = [Element]
   
   static constraints = {
   }
}
