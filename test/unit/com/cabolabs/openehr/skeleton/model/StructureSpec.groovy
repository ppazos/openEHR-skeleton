package com.cabolabs.openehr.skeleton.model

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import  com.cabolabs.openehr.skeleton.model.datatypes.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Structure)
@Mock([DvQuantity, DvText, DataValue, Element])
class StructureSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test save structure"()
    {
      when:
        def s = new Structure(
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
        if (!s.save()) println s.errors.allErrors
      then:
        Structure.count() == 1
    }
}
