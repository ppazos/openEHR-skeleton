package com.cabolabs.openehr.skeleton

import com.cabolabs.openehr.adl.ArchetypeManager
import org.openehr.rm.common.archetyped.Locatable

class RecordsController {

    private static String PS = File.separator
    private static String path = "."+ PS +"archetypes"

    def index()
    {
    }
    
    def create_blood_pressure()
    {
    }
    
    def save_blood_pressure()
    {
       //println params
       
       def loader = ArchetypeManager.getInstance(path)
       loader.loadAll()
       
       def archetype = loader.getArchetype(params.archetypeId)
       
       assert archetype.archetypeId.value == params.archetypeId
       
       println "archetype paths"
       archetype.physicalPaths().sort{ it }.each {
          println it
       }
       println ""
       
       println "params"
       params.sort{it.key}.each { path, value ->
       
          // is a path?
          if (!path.startsWith('/')) return
          
          println path +": "+ value
       }
       println ""
       
       println "data grouper creator"
       def data_grouper = [:]
       
       def constraint
       def parent_path
       def attribute
       params.each { path, value ->
       
          // is a path?
          if (!path.startsWith('/')) return
          
          //println path
          constraint = archetype.node(path)
          if (!constraint)
          {
            parent_path = Locatable.parentPath(path)
            constraint = archetype.node(parent_path)
            
            attribute = path - (parent_path + '/') // /a[]/b[]/c - /a[]/b[]/ = c
            
            // user parent_path instead of constraint.path because the parent_path can contain an archetype
            // ref and the constraint.path is just the referenced
            if (!data_grouper[parent_path])
            {
               data_grouper[parent_path] = [:]
            }
            data_grouper[parent_path][attribute] = value // groups values from complex datatypes
          }
          else
          {
            data_grouper[path] = value
          }
          
          //println constraint.getClass()
       }
       
       
       println "data grouper"
       data_grouper.sort{ it.key }.each { path, data ->
          println path +" > "+ data
       }
       
       
       // each item on the data grouper is a datatype to validate a gainst a constraint
       data_grouper.sort{ it.key }.each { path, data ->
          
          constraint = archetype.node(path)
          println constraint.rmTypeName // the validator will depend on the type
          
          /*
          DV_QUANTITY
DV_QUANTITY
DV_TEXT
DV_QUANTITY
DV_QUANTITY
CODE_PHRASE
DV_QUANTITY
CODE_PHRASE
DV_TEXT
DV_QUANTITY
DV_QUANTITY
DV_TEXT
DV_QUANTITY
DV_QUANTITY
CODE_PHRASE
CODE_PHRASE
DV_QUANTITY
CODE_PHRASE
DV_TEXT
CODE_PHRASE
CODE_PHRASE
CODE_PHRASE
DV_TEXT
CODE_PHRASE
DV_TEXT
*/
       }
       
       
       redirect action: 'create_blood_pressure'
    }
}
