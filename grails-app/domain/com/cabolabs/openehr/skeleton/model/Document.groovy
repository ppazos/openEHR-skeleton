package com.cabolabs.openehr.skeleton.model

class Document {

   Date start = new Date()
   Date end
   String category = "event"
   String templateId
   String archetypeId
   String versionUid
   
   Clinician author
   
   // Data from the UI
   // Map<String, String>
   Map bindData
   
   List content
   static hasMany = [content: Item]

   static constraints = {
      archetypeId (nullable: true)
      templateId (nullable: false)
      end (nullable: true)
      versionUid (nullable: true)
   }
   
   static mapping = {
      content cascade: 'save-update' // save the document to save all the structure in cascade
   }
   
   def beforeInsert() {

      bindData = bindData?.collectEntries { key, value ->
         [key, value.toString()] // need the values to be string
      }
   }
}
