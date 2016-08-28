package com.cabolabs.openehr.skeleton

import com.cabolabs.openehr.adl.ArchetypeManager
import org.openehr.rm.common.archetyped.Locatable
import com.cabolabs.openehr.skeleton.model.datatypes.*
import com.cabolabs.openehr.skeleton.model.*

class RecordsController {

    def dataBindingService

    private static String PS = File.separator
    private static String path = "."+ PS +"archetypes"
    private static List datavalues = ['DV_TEXT', 'DV_CODED_TEXT', 'DV_QUANTITY', 'DV_COUNT',
                                      'DV_ORDINAL', 'DV_DATE', 'DV_DATE_TIME', 'DV_PROPORTION',
                                      'DV_DURATION']

    def index()
    {
    }
    
    def create_blood_pressure()
    {
    }
    
    def save_blood_pressure()
    {
       if (!params.save)
       {
          redirect action: 'create_blood_pressure'
          return
       }
       
       def loader = ArchetypeManager.getInstance(path)
       loader.loadAll()
       
       def archetype = loader.getArchetype(params.archetypeId)
       
       assert archetype.archetypeId.value == params.archetypeId
       
       /*
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
       */
       
       println "data grouper creator"
       def data_grouper = [:]
       def constraint, parent_path, attribute
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
            
            // use parent_path instead of constraint.path because the parent_path can contain an archetype ref
            // and the constraint.path is just the referenced
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
       }
       
       
       
       println "data grouper"
       println data_grouper
       /*
       data_grouper.sort{ it.key }.each { path, data ->
          println path +" > "+ data
       }
       */
       
       
       // each item on the data grouper is a datatype to validate a gainst a constraint
       def validator
       def parent
       def errors = [:]
       data_grouper.sort{ it.key }.each { path, data ->
          
          constraint = archetype.node(path)
          //println constraint.rmTypeName // the validator will depend on the type
          
          
          // --------------------------------------------------------------------------------------
          // Get the parent object to validate using the occurrences (mandatory requires all the data
          // and optional will validate if not all the data is present)
          //
          // TODO: check for attributes of the IM
          parent = archetype.node(constraint.parent.parentNodePath())
          
          //println constraint.parent.getClass() // CSingleAttribute / CComplexAttribute
          
          // if the parent is a datavalue, it is a complex datavalue
          while (datavalues.contains(parent.rmTypeName))
          {
             parent = archetype.node(parent.parent.parentNodePath())
          }
          
          if (parent.rmTypeName != 'ELEMENT')
          {
             //println "parent NOT ELEMENT: "+ parent.rmTypeName
             println "the path is for an IM attribute: " + path
             //println parent
             return // Validation of IM attributes not supported yet
          }
          // --------------------------------------------------------------------------------------
          
          
          validator = 'validate'+ constraint.rmTypeName
          
          if ("$validator"(data, constraint, parent))
          {
             //println "valid"
          }
          else
          {
             //println "invalid"
             // path might not be the individual attribute path, but the grouper path for complex datavalues like DV_QUANTITY
             errors[path] = 'error' // we might add different types of errors here
          }
       }
       
       if (errors.size() > 0)
       {
          render view:'create_blood_pressure', model:[errors:errors]
          return
       }
       
       
       // TODO: save data
       
       // We create the document here for simplicity, because with archetypes
       // we should also add support for SLOTS and need to resolve the SLOTS.
       // Or use Operational Templates that have the complete document structure.
       
       def document = new Document(
          author: Clinician.get(1), // The clinician should come from a session, for that we need to add a login
          templateId: 'Encounter', 
          archetypeId: 'openEHR-EHR-COMPOSITION.encounter.v1',
          content: []
       )
       
       def s = dataBindingService.bind(data_grouper, params.archetypeId, 'content')
       println ">>> " + s
       println s as grails.converters.JSON
       
       // Try saving the document
       document.addToContent(s)
       if (!document.save()) println document.errors.allErrors
       
       
       redirect action: 'create_blood_pressure'
    }
    
    private boolean validateDV_QUANTITY(data, constraint, parent)
    {
       // If there is missing data to create the datavalue,
       //   If the parent is mandatory, then the value is NOT VALID
       //   If the parent is NOT mandatory, the the value is VALID (missing data is considered as an empty value)
       def mandatory = (parent.occurrences.lower == 1)
       if (!data.units || !data.magnitude)
       {
          return !mandatory
       }
       
       def valid = false
       def validator = constraint.list.find{ it.units == data.units } // soporta multiples unidades
       if (validator) valid = validator.magnitude.has(data.magnitude.toDouble())
       
       return valid
    }
    
    private boolean validateDV_TEXT(data, constraint, parent)
    {
       def mandatory = (parent.occurrences.lower == 1)
       if (!data)
       {
          return !mandatory
       }
       //println data
       //println constraint
       return true
    }
    
    private boolean validateCODE_PHRASE(data, constraint, parent)
    {
       def mandatory = (parent.occurrences.lower == 1)
       if (!data)
       {
          return !mandatory
       }
       /*
       println data
       println constraint
       println constraint.codeList
       */
       return constraint.codeList.contains(data) // validates a code
    }
    
    /*
    private boolean validate
    {
    }
    */
}
