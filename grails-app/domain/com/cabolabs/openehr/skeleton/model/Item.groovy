package com.cabolabs.openehr.skeleton.model

import com.cabolabs.openehr.skeleton.model.datatypes.DataValue

class Item {

   String type // rm_type_name
   String attr // rm_attr_name
   
   String archetypeId
   String path
   String nodeId // atNNNN
   
   Map attributes // attributes of the openEHR IM
   static hasMany = [attributes: DataValue]
   
   static belongsTo = [Document, Structure]
   
   static constraints = {
      attr(nullable:true)
   }
   
   static mapping = {
      attributes cascade: "all"
   }
}
