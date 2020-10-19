package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
        QuestionEntity question=null;
        try {
             question = entityManager.createNamedQuery("questionByUuId", QuestionEntity.class).setParameter("uuid", questionUUId)
                    .getSingleResult();
        }catch (Exception e){
            return null;
        }
        return question;
    }

    public List<QuestionEntity> getAllQuestions (UserEntity userId){
        List<QuestionEntity> questions=entityManager.createNamedQuery("questionsByUser",QuestionEntity.class).setParameter("userId",userId)
                .getResultList();
        return questions;
    }

    public List<QuestionEntity> getAllQuestions (){
        List<QuestionEntity> questions=entityManager.createNamedQuery("questionsByEveryUser",QuestionEntity.class)
                .getResultList();
        return questions;
    }

    public void deleteQuestion(QuestionEntity question){
        try {
            entityManager.remove(question);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public QuestionEntity getQuestionByUuid(String questionId){
        try {
            return entityManager.createNamedQuery("questionByUuId", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
