package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {


    @Autowired
    private QuestionService questionService;


    @RequestMapping(method = RequestMethod.POST,path="/question/create",produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion (QuestionRequest request, @RequestHeader ("authorization") final String authToken) throws AuthorizationFailedException {

        QuestionEntity questionEntity=new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(request.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        QuestionEntity createdQuestion=questionService.createQuestion(questionEntity,authToken);

        QuestionResponse response=new QuestionResponse();
        response.status("QUESTION CREATED");
        response.id(createdQuestion.getUuid());
        return new ResponseEntity<QuestionResponse>(response,HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET,path="/question/all",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> createQuestion (@RequestHeader ("authorization") final String authToken) throws AuthorizationFailedException {
        List<QuestionEntity> allQuestions=questionService.getAllQuestion(authToken);
        List<QuestionDetailsResponse> responses=new ArrayList<>();
        for (QuestionEntity question:allQuestions) {
            QuestionDetailsResponse response=new QuestionDetailsResponse();
            response.content(question.getContent());
            response.id(question.getUuid());
            responses.add(response);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(responses,HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.PUT,path="/question/edit/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion (@PathVariable("questionId") final String questionId, QuestionEditRequest request,
            @RequestHeader ("authorization") final String authToken) throws AuthorizationFailedException {

        QuestionEntity editedValue=questionService.editQuestion(questionId,authToken,request.getContent());
        QuestionResponse response=new QuestionResponse();
        response.status("QUESTION EDITED");
        response.id(editedValue.getUuid());
        return new ResponseEntity<QuestionResponse>(response,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,path="/question/delete/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion (@PathVariable("questionId") final String questionId,
                                                                           @RequestHeader ("authorization") final String authToken) throws AuthorizationFailedException, InvalidQuestionException {

        questionService.deleteQuestion(questionId,authToken);
        QuestionDeleteResponse response=new QuestionDeleteResponse();
        response.status("QUESTION DELETED");
        response.id(questionId);
        return new ResponseEntity<QuestionDeleteResponse>(response,HttpStatus.OK);
    }

}
