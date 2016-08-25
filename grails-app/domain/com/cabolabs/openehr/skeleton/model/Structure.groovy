package com.cabolabs.openehr.skeleton.model

class Structure extends Item {

   static hasMany = [items: Item]
   
   static belongsTo = [Document, Structure]
   
   static mapping = {
      items cascade: 'all' //'save-update'
   }
}
