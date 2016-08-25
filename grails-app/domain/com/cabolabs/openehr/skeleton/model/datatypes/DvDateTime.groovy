package com.cabolabs.openehr.skeleton.model.datatypes

import com.cabolabs.openehr.skeleton.model.Element

class DvDateTime extends DataValue {

   Date value
   
   static belongsTo = [Element]
   
   static constraints = {
   }
}
