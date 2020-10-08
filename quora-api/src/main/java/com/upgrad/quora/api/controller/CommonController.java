package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {


    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET,path="/userprofile/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@PathVariable("userId") final String userId,
                                                           @RequestHeader ("authorization") final String authToken) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity=userService.getUser(userId,authToken);
        UserDetailsResponse userDetailsResponse=new UserDetailsResponse().firstName(userEntity.getFirstName()).lastName(userEntity.getLastName())
                                                    .emailAddress(userEntity.getEmail()).userName((userEntity.getUsername()))
                                                    .country(userEntity.getCountry()).aboutMe(userEntity.getAboutMe()).dob(userEntity.getAboutMe())
                                                    .contactNumber(userEntity.getContactNumber());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }

}
