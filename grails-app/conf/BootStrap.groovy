import com.cabolabs.openehr.skeleton.model.datatypes.*
import com.cabolabs.openehr.skeleton.model.*
import grails.converters.JSON

class BootStrap {

   def init = { servletContext ->
   
      JSON.registerObjectMarshaller(com.cabolabs.openehr.skeleton.model.Structure) {
         return [id: it.id, type:it.type, attr:it.attr, archetypeId:it.archetypeId, path:it.path, nodeId:it.nodeId, items: it.items, attributes:it.attributes]
      }
      JSON.registerObjectMarshaller(com.cabolabs.openehr.skeleton.model.Element) {
         return [id: it.id, type:it.type, attr:it.attr, archetypeId:it.archetypeId, path:it.path, nodeId:it.nodeId, name: it.name, value: it.value, attributes:it.attributes]
      }
      JSON.registerObjectMarshaller(com.cabolabs.openehr.skeleton.model.Document) {
         return [id: it.id, start:it.start, end:it.end, archetypeId:it.archetypeId, templateId:it.templateId, author:it.author, content: it.content]
      }

   
      def patient = new Patient(name: 'Pablo Pazos')
      patient.save(flush: true, failOnError: true)
      
      def ehr = new Ehr(subject: patient)
      ehr.save(flush: true, failOnError: true)
      
      def clinician = new Clinician(name: 'Dr. House')
      clinician.save(flush: true, failOnError: true)
      
      /*
       * openEHR-EHR-COMPOSITION.encounter.v1 should have a slot in content that points to openEHR-EHR-OBSERVATION.blood_pressure.v1
       */
      def data = [
       'openEHR-EHR-OBSERVATION.blood_pressure.v1' : [
        '/data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/magnitude' : 120.0,
        '/data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/units': 'mm[Hg]',
        '/data[at0001]/events[at1042]/state[at0007]/items[at1043]/value/defining_code': 'at1044'
       ]
      ]
      
      /*
       * This structure should be created following the archetype structure. This is just an example created by hand.
       */
      def document = new Document(
       author: clinician,
       templateId: 'Encounter', 
       archetypeId: 'openEHR-EHR-COMPOSITION.encounter.v1',
       content: [
        new Structure(
         type:'OBSERVATION',
         attr:'content', 
         archetypeId: 'openEHR-EHR-OBSERVATION.blood_pressure.v1', 
         path: '/',
         nodeId: 'at0000',
         items: [
          new Structure(
           type:'HISTORY',
           attr:'data', 
           archetypeId: 'openEHR-EHR-OBSERVATION.blood_pressure.v1', 
           path: '/data[at0001]',
           nodeId: 'at0001',
           items: [
            new Structure(
             type:'EVENT',
             attr:'events', 
             archetypeId: 'openEHR-EHR-OBSERVATION.blood_pressure.v1', 
             path: '/data[at0001]/events[at0006]',
             nodeId: 'at0006',
             items: [
              new Structure(
               type:'ITEM_TREE',
               attr:'data', 
               archetypeId: 'openEHR-EHR-OBSERVATION.blood_pressure.v1', 
               path: '/data[at0001]/events[at0006]/data[at0003]',
               nodeId: 'at0003',
               items: [
                new Element(
                 type: 'ELEMENT',
                 attr:'items', 
                 archetypeId: 'openEHR-EHR-OBSERVATION.blood_pressure.v1', 
                 path: '/data[at0001]/events[at0006]/data[at0003]/items[at0004]',
                 nodeId: 'at0004',
                 name: new DvText(value: 'Systolic Blood Pressure'),
                 value: new DvQuantity(magnitude: 120.0, units: 'mm[Hg]')
                )
               ]
              )
             ]
            )
           ],
           attributes: [
            'origin': new DvDateTime(value:new Date())
           ]
          )
         ]
        )
       ]
      ) // new Document
      
      if (!document.save(flush: true))
      {
         println document.errors.allErrors
      }
      
      def s = new Structure(
        type:'ITEM_TREE',
        attr:'data', 
        archetypeId: 'openEHR-EHR-OBSERVATION.blood_pressure.v1', 
        path: '/data[at0001]/events[at0006]/data[at0003]',
        nodeId: 'at0003',
        items: [],
        attributes: [
         'fake': new DvDateTime(value:new Date())
        ]
      )
      if (!s.save()) println e.errors.allErrors
      
      def s1 = new Structure(
        type:'ITEM_TREE',
        attr:'data', 
        archetypeId: 'openEHR-EHR-OBSERVATION.blood_pressure.v1', 
        path: '/data[at0001]/events[at0006]/data[at0003]',
        nodeId: 'at0003',
        items: [],
      )
      
      s1.attributes = ['fake': new DvDateTime(value:new Date())]
      
      if (!s1.save()) println e.errors.allErrors
      
      def s2 = new Structure(
        type:'ITEM_TREE',
        attr:'data', 
        archetypeId: 'openEHR-EHR-OBSERVATION.blood_pressure.v1', 
        path: '/data[at0001]/events[at0006]/data[at0003]',
        nodeId: 'at0003',
        items: [],
        attributes: [:]
      )
      
      s2.attributes << ['fake': new DvDateTime(value:new Date())]
      
      if (!s2.save()) println e.errors.allErrors
      
      println Structure.count() // 4
      println Element.list()    // 1 Element
      
      // Just checking if the data is in the database:
      println Document.get(1) as JSON
      /*
      {
         "start": "2016-08-25T06:07:27Z",
         "end": null,
         "archetypeId": "openEHR-EHR-COMPOSITION.encounter.v1",
         "templateId": "Encounter",
         "author": {
            "class": "com.cabolabs.openehr.skeleton.model.Clinician",
            "id": 1,
            "name": "Dr. House",
            "uid": "1c36d9c6-dd5c-4084-aff7-e73a3c7cfe06"
         },
         "content": [{
            "type": "OBSERVATION",
            "attr": "content",
            "archetypeId": "openEHR-EHR-OBSERVATION.blood_pressure.v1",
            "path": "/",
            "nodeId": "at0000",
            "items": [{
               "type": "HISTORY",
               "attr": "data",
               "archetypeId": "openEHR-EHR-OBSERVATION.blood_pressure.v1",
               "path": "/data[at0001]",
               "nodeId": "at0001",
               "items": [{
                  "type": "EVENT",
                  "attr": "events",
                  "archetypeId": "openEHR-EHR-OBSERVATION.blood_pressure.v1",
                  "path": "/data[at0001]/events[at0006]",
                  "nodeId": "at0006",
                  "items": [{
                     "type": "ITEM_TREE",
                     "attr": "data",
                     "archetypeId": "openEHR-EHR-OBSERVATION.blood_pressure.v1",
                     "path": "/data[at0001]/events[at0006]/data[at0003]",
                     "nodeId": "at0003",
                     "items": [{
                        "class": "com.cabolabs.openehr.skeleton.model.Element",
                        "id": 5,
                        "archetypeId": "openEHR-EHR-OBSERVATION.blood_pressure.v1",
                        "attr": "items",
                        "attributes": null,
                        "name": {
                           "class": "com.cabolabs.openehr.skeleton.model.datatypes.DvText",
                           "id": 1
                        },
                        "nodeId": "at0004",
                        "path": "/data[at0001]/events[at0006]/data[at0003]/items[at0004]",
                        "type": "ELEMENT",
                        "value": {
                           "class": "com.cabolabs.openehr.skeleton.model.datatypes.DataValue",
                           "id": 2
                        }
                     }]
                  }]
               }]
            }]
         }]
      }
      */
   }
   def destroy = {
   }
}
