package com.cabolabs.openehr.skeleton

import com.cabolabs.openehr.adl.ArchetypeManager

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
       println params
       
       def loader = ArchetypeManager.getInstance(path)
       loader.loadAll()
       
       def archetype = loader.getArchetype(params.archetypeId)
       
       assert archetype.archetypeId.value == params.archetypeId
       
       redirect action: 'create_blood_pressure'
    }
}
