package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void userDelete(UserEntity user) {
        entityManager.remove(user);
    }

    public UserAuthEntity getUserAuthToken(String bearerToken) {
        UserAuthEntity userAuthEntity;
        try {
            userAuthEntity = entityManager.createNamedQuery("userAuthTokenByAccessToken",
                    UserAuthEntity.class).setParameter("accessToken", bearerToken.trim()).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
        return userAuthEntity;
    }

    public UserEntity getUser(final String userUuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

   
    public UserAuthEntity getAuthToken(final String authToken){
        try {
        return  entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class)
                .setParameter("accessToken",authToken).getSingleResult();
        }catch (Exception e){
            return null;
        }

    }
    public UserAuthEntity getUserAuthTokenByAccessToken(final String accessToken){
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class).setParameter("accessToken",accessToken)
                    .getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

}
