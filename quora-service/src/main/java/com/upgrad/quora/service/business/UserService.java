package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private  PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(String uuid,String authToken) throws AuthorizationFailedException, UserNotFoundException {
       UserAuthTokenEntity authTokenEntity= userDao.getAuthToken(authToken);
       if(authTokenEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in.");
       }
       UserEntity user= userDao.getUser(uuid);
       if(user==null){
           throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
       }
       return user;
    }
}
