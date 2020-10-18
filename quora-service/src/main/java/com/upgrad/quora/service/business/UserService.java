package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private  PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * Checks the user,if valid user is found then user details are returned
     *
     * Input:
     *      uuid:
     *          Type-String
     *          Unique uuid to identify user
 *          authToken
     *           Type-String
     *           Unique authToken to identify a session
     *
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(String uuid,String authToken) throws AuthorizationFailedException, UserNotFoundException {
       UserAuthEntity authTokenEntity= userDao.getAuthToken(authToken);
       //Checks if authToken is valid or not.
       if(authTokenEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in.");
       }
       UserEntity user= userDao.getUser(uuid);
        //Checks if user is valid or not.
       if(user==null){
           throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
       }

        LocalDateTime logoutTime=authTokenEntity.getLogoutAt().toLocalDateTime();
        LocalDateTime currentTime=LocalDateTime.now();
        //Checks  logged out time to determine if user is currently signed in or not.
       if(logoutTime.isBefore(currentTime))
       {
           throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
       }
       return user;
    }

}
