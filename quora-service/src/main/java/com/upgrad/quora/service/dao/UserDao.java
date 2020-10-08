package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity getUser(String uuid){
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        }catch (Exception e){
            return null;
        }
    }

    public UserAuthTokenEntity getAuthToken(final String authToken){
        try {
        return  entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthTokenEntity.class)
                .setParameter("accessToken",authToken).getSingleResult();
        }catch (Exception e){
            return null;
        }

    }

}
