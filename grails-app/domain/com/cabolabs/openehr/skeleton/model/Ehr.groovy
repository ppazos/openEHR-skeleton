package com.cabolabs.openehr.skeleton.model

class Ehr {

   String uid = java.util.UUID.randomUUID() as String
   Date dateCreated = new Date()
   
   Patient subject
   
   List documents
   static hasMany = [documents: Document]
   
   static constraints = {
   }
}
