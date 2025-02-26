package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Dtos.UserOutput;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public UserOutput mapObjectToUserOutput(Object[] userObject) {
        return new UserOutput((String) userObject[0],
                (String) userObject[1],
                (String) userObject[2],
                (String) userObject[3],
                (Boolean) userObject[4],
                ((Number) userObject[5]).intValue());
    }

    public Page<UserOutput> mapObjectPageToUserOutputPage(Page<Object[]> usersObjectArray) {
        return usersObjectArray.map(this::mapObjectToUserOutput);
    }
}