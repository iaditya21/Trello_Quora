package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    }
