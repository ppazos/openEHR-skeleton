package com.cabolabs.openehr.skeleton.model

class Patient {

   String uid = java.util.UUID.randomUUID() as String
   String name
   
   static constraints = {
   }
}
