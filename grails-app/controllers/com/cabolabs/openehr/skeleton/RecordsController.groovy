package com.cabolabs.openehr.skeleton

class RecordsController {

    def index()
    {
    }
    
    def create_blood_pressure()
    {
    }
    
    def save_blood_pressure()
    {
       println params
       redirect action: 'create_blood_pressure'
    }
}
