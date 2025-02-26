package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Dtos.UserOutput;
import com.example.virtualwallet.models.User;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public UserOutput mapUserToUserOutput(User user){
        UserOutput userOutput = new UserOutput();


        return userOutput;
    }
}
