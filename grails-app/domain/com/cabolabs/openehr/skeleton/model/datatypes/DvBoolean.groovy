package com.cabolabs.openehr.skeleton.model.datatypes

import com.cabolabs.openehr.skeleton.model.Element

class DvBoolean extends DataValue {

   boolean value
   
   static belongsTo = [Element]

   static constraints = {
   }
}
