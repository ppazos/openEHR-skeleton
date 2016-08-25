package com.cabolabs.openehr.skeleton.model.datatypes

import com.cabolabs.openehr.skeleton.model.Element

class DvQuantity extends DataValue {

    // BigDecimal instead of Float because: http://stackoverflow.com/questions/37450774/grails-2-5-3-not-binding-float-fields-correctly/37454016
   BigDecimal magnitude
   String units
   
   static belongsTo = [Element]

   static constraints = {
   }
}
