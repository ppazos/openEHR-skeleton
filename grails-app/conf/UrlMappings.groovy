class UrlMappings {

	static mappings = {
      "/$controller/$action?/$id?(.$format)?"{
         constraints {
            // apply constraints here
         }
      }

      //"/"(view:"/index")
      "/"{
         controller = 'records'
         action = 'create_blood_pressure'
      }
      
      "500"(view:'/error')
	}
}
