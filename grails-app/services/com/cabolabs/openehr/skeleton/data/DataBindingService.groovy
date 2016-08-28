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
         nodeId:      o.nodeID, 
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
         nodeId:      o.nodeID, 
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
      println "INTERVAL EVENT "+ o.attributes.find{ it.rmAttributeName == 'width' }
      println o.attributes.find{ it.rmAttributeName == 'math_function' }
      Structure strct = new Structure(
         path:        this.getPath(o), 
         nodeId:      o.nodeID, 
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
         nodeId:      o.nodeID, 
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
         nodeId:      o.nodeID, 
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
         nodeId:      o.nodeID, 
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
      def filtered_data, binded_attrs, type, method
      o.attributes.each { attr ->
         
         filtered_data = filterData(data_grouper, this.getPath(o))
         type = attr.class.getSimpleName()
         method = 'bind_'+ type
         binded_attrs = this."$method"(attr, filtered_data)
         
         /*
         binded_attrs.each { item ->
            
            s.addToItems(item)
         }
         */
      }
      
      println "binded_attrs: "+ binded_attrs
      
      Element e = new Element(
         path:        this.getPath(o), 
         nodeId:      o.nodeID, 
         type:        o.rmTypeName, 
         archetypeId: archetype.archetypeId.value,
         attr:        attr_name,
         name:        new DvText(value:''), // TODO: resolve name, check o constraint on the name...
         value:       new DvText(value:'') // TODO: get from binded_attrs
      )
      
      //processAttributes(o, data_grouper, strct) // adds items
      
      return e
   }
   
   private bind_DV_DURATION(CComplexObject o, Map data_grouper, String attr_name)
   {
      println "DV_DURATION :"+ data_grouper
   }
   
   private bind_DV_QUANTITY(CDvQuantity o, Map data_grouper, String attr_name)
   {
      println "DV_QUANTITY :"+ data_grouper
   }
   
   private bind_DV_TEXT(CComplexObject o, Map data_grouper, String attr_name)
   {
      println "DV_TEXT :"+ data_grouper
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
