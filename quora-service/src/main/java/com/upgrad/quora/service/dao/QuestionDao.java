package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity newQuestion){
       entityManager.persist(newQuestion);
       return newQuestion;
    }

    public QuestionEntity updateQuestion(QuestionEntity newQuestion){
        entityManager.merge(newQuestion);
        return newQuestion;
    }

    public QuestionEntity getQuestion(String questionUUId){
        QuestionEntity question=entityManager.createNamedQuery("questionByUUId",QuestionEntity.class).setParameter("uuid",questionUUId)
                .getSingleResult();
        return question;
    }

    public List<QuestionEntity> getAllQuestions (UserEntity userEntity){
        List<QuestionEntity> questions=entityManager.createNamedQuery("questionsByUser",QuestionEntity.class).setParameter("userId",userEntity)
                .getResultList();
        return questions;
    }
}
