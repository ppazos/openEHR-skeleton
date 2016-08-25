package com.cabolabs.openehr.skeleton.model.datatypes

import com.cabolabs.openehr.skeleton.model.Element

class DataValue {

   static belongsTo = [Element]
   
   // table per class inheritance mapping
   static mapping = {
      tablePerHierarchy false
   }
   
   static constraints = {
   }
}
