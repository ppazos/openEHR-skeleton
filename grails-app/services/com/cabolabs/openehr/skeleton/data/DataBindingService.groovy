package com.cabolabs.openehr.skeleton.data

import grails.transaction.Transactional
import com.cabolabs.openehr.adl.ArchetypeManager
import org.openehr.am.archetype.Archetype
import org.openehr.am.archetype.constraintmodel.*
import com.cabolabs.openehr.skeleton.model.*
import com.cabolabs.openehr.skeleton.model.datatypes.*
import org.openehr.am.openehrprofile.datatypes.quantity.*

@Transactional
class DataBindingService {

   private static String PS = File.separator
   private static String path = "."+ PS +"archetypes"
   private Archetype archetype
   private ArchetypeInternalRef current_ref // to help processing internal refs, TODO> all the path depends on current_ref
    
   /**
    * receives the data grouped in the controller for an archetype and returns a 
    * structure that follows that archetype with the data from the data_grouper.
    */
   def bind(Map data_grouper, String archetype_id, String attr_name)
   {
      this.current_ref = null
      
      def loader = ArchetypeManager.getInstance(path)
      if (loader.loadedArchetypes.size() == 0) loader.loadAll()
      
      this.archetype = loader.getArchetype(archetype_id)
      if (!this.archetype) throw new Exception("Archetype $archetype_id not present on repo $path")
      
      def type = this.archetype.definition.rmTypeName
      def method = 'bind_'+ type
      
      return this."$method"(this.archetype.definition, data_grouper, attr_name)
   }
   
   /**
    * This will be called if the type to bind is not supported yet.
    */
   def methodMissing(String name, args)
   {
      println "Bind type not supported yet "+ (name - 'bind_') +" with constraint: "+ args[0].class.simpleName
   }
   
   /**
    * Gets the path of the object, considering if it is referenced from an internal ref.
    */
   private String getPath(CObject o)
   {
      if (this.current_ref)
      {
         return o.path().replace(this.current_ref.targetPath, this.current_ref.path() +'['+ this.archetype.node( this.current_ref.targetPath ).nodeId +']')
      }
      
      return o.path()
   }
   
   private bind_OBSERVATION(CComplexObject o, Map data_grouper, String attr_name)
   {
      Structure strct = new Structure(
         path:        this.getPath(o),
         nodeId:      o.nodeId,
         type:        o.rmTypeName,
         archetypeId: archetype.archetypeId.value,
         attr:        attr_name)
      
      /*
      def filtered_data, method, type, items
      o.attributes.each { attr ->
      
         filtered_data = filterData(data_grouper, o.path())
         type = attr.class.getSimpleName()
         method = 'bind_'+ type
         items = this."$method"(attr, filtered_data)
         
         items.each { item ->
            
            strct.addToItems(item)
         }
      }
      */
      processAttributes(o, data_grouper, strct) // adds items
      
      return strct
   }
   
   private processAttributes(CComplexObject o, Map data_grouper, Structure s)
   {
      println "Attributes of "+ o.rmTypeName
      
      def filtered_data, method, type, items
      o.attributes.each { attr ->
      
         println "Attribute "+ attr.rmAttributeName
         
         filtered_data = filterData(data_grouper, this.getPath(o))
         type = attr.class.getSimpleName()
         method = 'bind_'+ type
         items = this."$method"(attr, filtered_data)
         
         items.each { item ->
            
            s.addToItems(item)
         }
      }
   }
   
   private bind_HISTORY(CComplexObject o, Map data_grouper, String attr_name)
   {
      Structure strct = new Structure(
         path:        this.getPath(o), 
         nodeId:      o.nodeId, 
         type:        o.rmTypeName, 
         archetypeId: archetype.archetypeId.value,
         attr:        attr_name,
         attributes:  ['origin': new DvDateTime(value:new Date())]
      )
      
      processAttributes(o, data_grouper, strct) // adds items
      
      return strct
   }
   
   private bind_EVENT(CComplexObject o, Map data_grouper, String attr_name)
   {
      Structure strct = new Structure(
         path:        this.getPath(o), 
         nodeId:      o.nodeId, 
         type:        o.rmTypeName, 
         archetypeId: archetype.archetypeId.value,
         attr:        attr_name,
         attributes:  ['time': new DvDateTime(value:new Date())]
      )
      
      processAttributes(o, data_grouper, strct) // adds items
      
      return strct
   }
   
   private bind_POINT_EVENT(CComplexObject o, Map data_grouper, String attr_name)
   {
      return bind_EVENT(o, data_grouper, attr_name)
   }
   
   private bind_INTERVAL_EVENT(CComplexObject o, Map data_grouper, String attr_name)
   {
      //println "INTERVAL EVENT "+ o.attributes.find{ it.rmAttributeName == 'width' }
     
      /*
      INTERVAL EVENT org.openehr.am.archetype.constraintmodel.CSingleAttribute@1d3534e[
  rmAttributeName=width
  existence=REQUIRED
  children=[org.openehr.am.archetype.constraintmodel.CComplexObject@1026357[
  attributes=[org.openehr.am.archetype.constraintmodel.CSingleAttribute@1c80f54[
  rmAttributeName=value
  existence=REQUIRED
  children=[org.openehr.am.archetype.constraintmodel.CPrimitiveObject@1667589[
  item=org.openehr.am.archetype.constraintmodel.primitive.CDuration@1a1de8d[
  value=<null>
  interval=org.openehr.rm.support.basic.Interval@aca9b1[lower=PT24H,lowerIncluded=true,upper=PT24H,upperIncluded=true]
  assumedValue=<null>
  pattern=<null>
  defaultValue=<null>
]
  assumedValue=<null>
  defaultValue=<null>
  rmTypeName=DV_DURATION
  occurrences=org.openehr.rm.support.basic.Interval@109c2d1[lower=1,lowerIncluded=true,upper=1,upperIncluded=true]
  nodeID=<null>
  parent=org.openehr.am.archetype.constraintmodel.CSingleAttribute@1c80f54
  anyAllowed=false
  path=/data[at0001]/events[at1042]/width/value
  hiddenOnForm=false
  annotation=<null>
      
      */
      
      //println o.attributes.find{ it.rmAttributeName == 'math_function' }
      /*
      org.openehr.am.archetype.constraintmodel.CSingleAttribute@152d15d[
  rmAttributeName=math_function
  existence=REQUIRED
  children=[org.openehr.am.archetype.constraintmodel.CComplexObject@2f3679[
  attributes=[org.openehr.am.archetype.constraintmodel.CSingleAttribute@1a17865[
  rmAttributeName=defining_code
  existence=REQUIRED
  children=[org.openehr.am.openehrprofile.datatypes.text.CCodePhrase@1e7508c[
  terminologyId=openehr
  codeList=[146]
  defaultValue=<null>
  assumedValue=<null>
  rmTypeName=CODE_PHRASE
  occurrences=org.openehr.rm.support.basic.Interval@159404f[lower=1,lowerIncluded=true,upper=1,upperIncluded=true]
  nodeID=<null>
  parent=org.openehr.am.archetype.constraintmodel.CSingleAttribute@1a17865
  anyAllowed=false
  path=/data[at0001]/events[at1042]/math_function/defining_code
  hiddenOnForm=false
  annotation=<null>
]]
  anyAllowed=false
  path=/data[at0001]/events[at1042]/math_function/defining_code
  hiddenOnForm=false
  annotation=<null>
]]
  assumedValue=<null>
  defaultValue=<null>
  rmTypeName=DV_CODED_TEXT
  occurrences=org.openehr.rm.support.basic.Interval@8d3163[lower=1,lowerIncluded=true,upper=1,upperIncluded=true]
  nodeID=<null>
  parent=org.openehr.am.archetype.constraintmodel.CSingleAttribute@152d15d
  anyAllowed=false
  path=/data[at0001]/events[at1042]/math_function
  hiddenOnForm=false
  annotation=<null>
]]
  anyAllowed=false
  path=/data[at0001]/events[at1042]/math_function
  hiddenOnForm=false
  annotation=<null>
]
      
      */
      
      Structure strct = new Structure(
         path:        this.getPath(o), 
         nodeId:      o.nodeId, 
         type:        o.rmTypeName, 
         archetypeId: archetype.archetypeId.value,
         attr:        attr_name,
         attributes:  [
           //'math_function': new DvCodedText(value:'') // TODO: get the code from the archetype and use terminology to get the value
           /*,
           'width':         new DvDuration(value: 'PT24H')*/ // TOOD> add DvDuration to data types
         ]
      )
      
      processAttributes(o, data_grouper, strct) // adds items
      
      return strct
   }
   
   private bind_ITEM_TREE(CComplexObject o, Map data_grouper, String attr_name)
   {
      Structure strct = new Structure(
         path:        this.getPath(o), 
         nodeId:      o.nodeId, 
         type:        o.rmTypeName, 
         archetypeId: archetype.archetypeId.value,
         attr:        attr_name
      )
      
      processAttributes(o, data_grouper, strct) // adds items
      
      return strct
   }
   
   private bind_ITEM_TREE(ArchetypeInternalRef o, Map data_grouper, String attr_name)
   {
      this.current_ref = o

      Structure strct = new Structure(
         path:        o.path(), 
         nodeId:      this.archetype.node( o.targetPath ).nodeId, // uses the reference nodeId because the o nodeId is null
         type:        o.rmTypeName, 
         archetypeId: archetype.archetypeId.value,
         attr:        attr_name
      )
      
      processAttributes(this.archetype.node( o.targetPath ), data_grouper, strct) // adds items
      
      this.current_ref = null
      
      return strct
   }
   
   private bind_CLUSTER(CComplexObject o, Map data_grouper, String attr_name)
   {
      Structure strct = new Structure(
         path:        this.getPath(o), 
         nodeId:      o.nodeId, 
         type:        o.rmTypeName, 
         archetypeId: archetype.archetypeId.value,
         attr:        attr_name
      )
      
      processAttributes(o, data_grouper, strct) // adds items
      
      return strct
   }
   private bind_CLUSTER(ArchetypeSlot o, Map data_grouper, String attr_name)
   {
      println "Slots not supported "+ o.path()
      return
   }
   
   private bind_ELEMENT(CComplexObject o, Map data_grouper, String attr_name)
   {
      Element e = new Element(
         path:        this.getPath(o), 
         nodeId:      o.nodeId, 
         type:        o.rmTypeName, 
         archetypeId: archetype.archetypeId.value,
         attr:        attr_name
         /*,
         name:        new DvText(value:''), // TODO: resolve name, check o constraint on the name...
         value:       new DvText(value:'') // TODO: get from binded_attrs
         */
      )
      
      
      def filtered_data, binded_attrs, type, method
      o.attributes.each { attr ->
         
         filtered_data = filterData(data_grouper, this.getPath(o))
         type = attr.class.getSimpleName()
         method = 'bind_'+ type
         binded_attrs = this."$method"(attr, filtered_data)
         
         assert binded_attrs.size() <= 1
         println "binded_attrs: "+ binded_attrs
         
         // if there is no data, the current attribute can be binded to null.
         if (binded_attrs.size() == 1)
            e."$attr.rmAttributeName" = binded_attrs[0] // attribute will be value or name
      }
      
      return e
   }
   
   private bind_DV_DURATION(CComplexObject o, Map data_grouper, String attr_name)
   {
      println "DV_DURATION :"+ data_grouper
      println "DURATION PATH: "+o.path() +" and attr $attr_name"
      
      if (!data_grouper[o.path()])
      {
         println "DV_DURATION No value for path "+ o.path() +" checking the archetype for a fixed value"
         //println o
         /*
         org.openehr.am.archetype.constraintmodel.CComplexObject@918c6c[
           attributes=[
            org.openehr.am.archetype.constraintmodel.CSingleAttribute@19a4344[
              rmAttributeName=value
              existence=REQUIRED
              children=[
               org.openehr.am.archetype.constraintmodel.CPrimitiveObject@b64d29[
                 item=org.openehr.am.archetype.constraintmodel.primitive.CDuration@83e511[
                 value=<null>
                 interval=org.openehr.rm.support.basic.Interval@133c26e[lower=PT24H,lowerIncluded=true,upper=PT24H,upperIncluded=true]
                 assumedValue=<null>
                 pattern=<null>
                 defaultValue=<null>
         
         */
      }
      else
      {
         return new DvDuration(value:data_grouper[o.path()])
      }
   }
   
   private bind_DV_QUANTITY(CDvQuantity o, Map data_grouper, String attr_name)
   {
      println "DV_QUANTITY :"+ data_grouper
      
      // No data, just return null
      if (!data_grouper[o.path()] || !data_grouper[o.path()].magnitude) return
      
      return new DvQuantity(
        magnitude: data_grouper[o.path()].magnitude,
        units: data_grouper[o.path()].units
      )
   }
   
   private bind_DV_TEXT(CComplexObject o, Map data_grouper, String attr_name)
   {
      println "DV_TEXT :"+ data_grouper
      
      
      if (!o.attributes || o.attributes.size() == 0)
      {
      }
      else
      {
         println "text has attributes"
      }

      if (!data_grouper[o.path()])
      {
         println "DV_TEXT No value for path "+ o.path()
         return // do not bind if there is no data
      }
      
      return new DvText(value:data_grouper[o.path()])
   }
   
   private bind_DV_CODED_TEXT(CComplexObject o, Map data_grouper, String attr_name)
   {
      println "DV_CODED_TEXT :"+ data_grouper
      
   }
   
   private List bind_CSingleAttribute(CSingleAttribute cattr, Map data_grouper)
   {
      String method
      def attrs = []
      def attr
      cattr.alternatives().each { cobject ->
         
         method = 'bind_'+  cobject.rmTypeName
         attr = this."$method"(cobject, data_grouper, cattr.rmAttributeName)
         
         // Si no vienen valores para bindear un datavalue, el binder de element retorna null
         // Aqui se verifica si lo que se devuelve es null, y si es, no se considera para enviarselo al nodo padre
         if (attr) attrs << attr
      }
      return attrs
   }
   
   private List bind_CMultipleAttribute(CMultipleAttribute cattr, Map data_grouper)
   {
      String method
      def attrs = []
      def attr
      cattr.members().each { cobject ->

         method = 'bind_'+ cobject.rmTypeName
         attr = this."$method"(cobject, data_grouper, cattr.rmAttributeName)
         
         // Si no vienen valores para bindear un datavalue, el binder de element retorna null
         // Aqui se verifica si lo que se devuelve es null, y si es, no se considera para enviarselo al nodo padre
         if (attr) attrs << attr
      }
      return attrs
   }
   
   /**
    * Filters the data_grouper by the parent node path.
    */
   Map filterData(Map data_grouper, String filter_path)
   {
      Map filtered_data = [:]

      data_grouper.each { path, values ->
         
         // Value puede ser multiple
         if (path.startsWith(filter_path)) filtered_data[path] = values
      }
      
      return filtered_data
   }
}
