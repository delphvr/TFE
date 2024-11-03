package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired 
    private Database db;

    @PostMapping("/addUser")
    public String test(@RequestBody UserRequest userRequest){
        db.addUser(
            userRequest.getFirstName(),
            userRequest.getLastName(),
            userRequest.getEmail(),
            userRequest.isOrganizer(),
            userRequest.getProfessions()
        );
        return "User added successfully";
    }
    
}
