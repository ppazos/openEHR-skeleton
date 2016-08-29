package com.cabolabs.openehr.skeleton.model.datatypes
import com.cabolabs.openehr.skeleton.model.Item
import com.cabolabs.openehr.skeleton.model.Element
import com.cabolabs.openehr.skeleton.model.Structure
class DataValue {

   static belongsTo = [Element, Structure, Item]
   
   // table per class inheritance mapping
   static mapping = {
      tablePerHierarchy false
   }
   
   static constraints = {
   }
}
