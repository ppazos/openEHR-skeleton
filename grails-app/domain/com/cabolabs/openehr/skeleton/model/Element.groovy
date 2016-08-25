package com.cabolabs.openehr.skeleton.model

import com.cabolabs.openehr.skeleton.model.datatypes.*

class Element extends Item {

   DataValue value
   DvText name
   
   static belongsTo = [Structure]
   
   static mapping = {
      value cascade: 'save-update'
      name cascade: 'save-update'
   }
   
   static constraints = {
      value(nullable: true)
      name(nullable: true)
   }
}
