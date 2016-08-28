package com.cabolabs.openehr.skeleton.model.datatypes

import com.cabolabs.openehr.skeleton.model.Element

class DvDuration extends DataValue {

   String value // TODO: regex to validate it is an ISO8601 duration string
   
   static belongsTo = [Element]

   static constraints = {
   }
}
