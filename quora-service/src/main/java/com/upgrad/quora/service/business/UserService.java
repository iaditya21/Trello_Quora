package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;


@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;


    /**
     * Checks the user,if valid user is found then user details are returned
     * <p>
     * Input:
     * uuid:
     * Type-String
     * Unique uuid to identify user
     * authToken
     * Type-String
     * Unique authToken to identify a session
     */
    @Transactional(propagation = Propagation.REQUIRED)

    public UserEntity getUser(String uuid, String authToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity authTokenEntity = userDao.getAuthToken(authToken);
        //Checks if authToken is valid or not.
        if (authTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
        }
        UserEntity user = userDao.getUser(uuid);
        //Checks if user is valid or not.
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        if (authTokenEntity.getLogoutAt() == null) {
            return user;
        }
        LocalDateTime logoutTime = authTokenEntity.getLogoutAt().toLocalDateTime();
        LocalDateTime currentTime = LocalDateTime.now();
        //Checks  logged out time to determine if user is currently signed in or not.
        if (logoutTime.isBefore(currentTime)) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        return user;

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String authorization) throws SignOutRestrictedException {

        UserAuthEntity userAuthToken = userDao.getUserAuthTokenByAccessToken(authorization);

        //Throw exception if either the JWT access token is invalid orer if the user has already signed out
        //In case user has already signed out then the logout time of the user will not be null
        if(userAuthToken == null || (userAuthToken != null && userAuthToken.getLogoutAt() != null)) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        userAuthToken.setLogoutAt(ZonedDateTime.now());
        userDao.updateLogOutTime(userAuthToken);
        return userAuthToken.getUser();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signin(final String userName, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUserName(userName);

        //Throw exception if given user name does not exist in database
        if(userEntity == null){
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());

        //Throw exception if provided password does not match with the password stored in database
        if(!encryptedPassword.equals(userEntity.getPassword())){
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }

        //Construct a JWT token using JwtTokenProvider and persist it in the database before returning to controller
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        UserAuthEntity userAuthToken = new UserAuthEntity();
        userAuthToken.setUuid(UUID.randomUUID().toString());
        userAuthToken.setUser(userEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));
        userAuthToken.setLoginAt(now);
        userAuthToken.setExpiresAt(expiresAt);

        userDao.createAuthToken(userAuthToken);

        return userAuthToken;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        //Throw exception if given user name already exists
        if(userDao.getUserByUserName(userEntity.getUsername()) != null){
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }

        //Throw exception if given email address already exists
        if(userDao.getUserByEmailAddress(userEntity.getEmail()) != null){
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }

        //Get encrypted password from cryptography provider, the method returns an array of Strings
        //Salt will be at index 0 in the return array and encrypted password will be at index 1
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }
}
