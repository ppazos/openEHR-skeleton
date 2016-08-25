package com.cabolabs.openehr.skeleton.model

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Ehr)
@Mock([Patient])
class EhrSpec extends Specification {

   def setup() {
   }

   def cleanup() {
   }

   void "create an EHR"()
   {
      when:
        def patient = new Patient(name: 'Pablo Pazos')
        patient.save(flush: true, failOnError: true)
        
        def ehr = new Ehr(subject: patient)
        if (!ehr.save()) println ehr.errors.allErrors
      then:
        Ehr.count() == 1
        Ehr.get(1).uid != null
   }
}
