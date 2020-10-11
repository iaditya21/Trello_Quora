package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity question, String authToken) throws AuthorizationFailedException {
       UserAuthTokenEntity authTokenEntity= userDao.getAuthToken(authToken);
       //Checks if authToken is valid or not.
       if(authTokenEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in.");
       }
        question.setUserId(authTokenEntity.getUser());

       if(authTokenEntity.getLogoutAt()!=null) {
           LocalDateTime logoutTime = authTokenEntity.getLogoutAt().toLocalDateTime();
           LocalDateTime currentTime = LocalDateTime.now();
           //Checks  logged out time to determine if user is currently signed in or not.
           if (logoutTime.isBefore(currentTime)) {
               throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
           }
       }
       return questionDao.createQuestion(question);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestion(String authToken,String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity authTokenEntity= userDao.getAuthToken(authToken);
        //Checks if authToken is valid or not.
        if(authTokenEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in.");
        }
        if(authTokenEntity.getLogoutAt()!=null) {
            LocalDateTime logoutTime = authTokenEntity.getLogoutAt().toLocalDateTime();
            LocalDateTime currentTime = LocalDateTime.now();
            //Checks  logged out time to determine if user is currently signed in or not.
            if (logoutTime.isBefore(currentTime)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }

        UserEntity userEntity=userDao.getUser(userId);
        if(userEntity==null){
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestions(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestion(String authToken) throws AuthorizationFailedException {
        UserAuthTokenEntity authTokenEntity= userDao.getAuthToken(authToken);
        //Checks if authToken is valid or not.
        if(authTokenEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in.");
        }
        if(authTokenEntity.getLogoutAt()!=null) {
            LocalDateTime logoutTime = authTokenEntity.getLogoutAt().toLocalDateTime();
            LocalDateTime currentTime = LocalDateTime.now();
            //Checks  logged out time to determine if user is currently signed in or not.
            if (logoutTime.isBefore(currentTime)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }



        return questionDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(String questionUUId,String authToken,String content) throws AuthorizationFailedException {
        UserAuthTokenEntity authTokenEntity= userDao.getAuthToken(authToken);
        QuestionEntity question=questionDao.getQuestion(questionUUId);
        //Checks if authToken is valid or not.
        if(authTokenEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in.");
        }
        if(authTokenEntity.getLogoutAt()!=null) {
            LocalDateTime logoutTime = authTokenEntity.getLogoutAt().toLocalDateTime();
            LocalDateTime currentTime = LocalDateTime.now();
            //Checks  logged out time to determine if user is currently signed in or not.
            if (logoutTime.isBefore(currentTime)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }

        if(authTokenEntity.getUser()!=question.getUserId()){
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        question.setContent(content);
        return questionDao.updateQuestion(question);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(String questionUUId,String authToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity authTokenEntity= userDao.getAuthToken(authToken);
        QuestionEntity question=questionDao.getQuestion(questionUUId);

        if(question==null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }
        //Checks if authToken is valid or not.
        if(authTokenEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in.");
        }
        if(authTokenEntity.getLogoutAt()!=null) {
            LocalDateTime logoutTime = authTokenEntity.getLogoutAt().toLocalDateTime();
            LocalDateTime currentTime = LocalDateTime.now();
            //Checks  logged out time to determine if user is currently signed in or not.
            if (logoutTime.isBefore(currentTime)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }

        if(authTokenEntity.getUser()!=question.getUserId()) {
            if (!(authTokenEntity.getUser().getRole().equalsIgnoreCase("admin"))) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }
        }

        questionDao.deleteQuestion(questionDao.getQuestion(questionUUId));
    }

}
