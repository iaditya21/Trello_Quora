package com.upgrad.quora.service.business;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationBusinessService {

    @Autowired
    private UserDao userDao;

    public UserAuthEntity authorizeUser(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthToken = userDao.getUserAuthTokenByAccessToken(authorization);

        //Throw exception if provided access token does not exist in database
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        return userAuthToken;
    }
}
