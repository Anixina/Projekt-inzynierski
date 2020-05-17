package com.ProjektInzynierski.BackEnd.controller;

import com.ProjektInzynierski.BackEnd.data.entity.Answers;
import com.ProjektInzynierski.BackEnd.data.entity.Questions;
import com.ProjektInzynierski.BackEnd.data.entity.Survey;
import com.ProjektInzynierski.BackEnd.data.model.AnswerDetailsData;
import com.ProjektInzynierski.BackEnd.data.model.SurveyDetailsData;
import com.ProjektInzynierski.BackEnd.processors.creator.CreatorProcessor;
import com.ProjektInzynierski.BackEnd.processors.creator.QuestionAndAnswerIdHandler;
import com.ProjektInzynierski.BackEnd.repository.AnswersRepository;
import com.ProjektInzynierski.BackEnd.repository.SurveyRepository;
import com.ProjektInzynierski.BackEnd.util.ResultMap;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class SurveyController {

    private Logger logger = LoggerController.getInstance();

    private final CreatorProcessor creatorProcessor;

    private final SurveyRepository surveyRepository;

    private final AnswersRepository answersRepository;

    private final QuestionAndAnswerIdHandler questionAndAnswerIdHandler;

    public SurveyController(CreatorProcessor creatorProcessor,
                            SurveyRepository surveyRepository,
                            AnswersRepository answersRepository,
                            QuestionAndAnswerIdHandler questionAndAnswerIdHandler) {
        this.creatorProcessor = creatorProcessor;
        this.surveyRepository = surveyRepository;
        this.answersRepository = answersRepository;
        this.questionAndAnswerIdHandler = questionAndAnswerIdHandler;
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return new ResponseEntity<>("Up", HttpStatus.OK);
    }

    @GetMapping("/survey/all")
    List<Survey> getAllSurveysFromDatabase() {
        return this.surveyRepository.findAll();
    }

    @GetMapping("/survey/search")
    Survey search(@RequestParam(name = "topic") String topic) {
        return this.surveyRepository.findValidSurveyByTopic(topic);
    }

    @GetMapping("/survey/{id}")
    Survey show(@PathVariable("id") String id) {
        int blogId = Integer.parseInt(id);
        return this.surveyRepository.findById(blogId);
    }

    @PostMapping("/survey")
    Survey create(@RequestBody Map<String, String> body) {
        Survey survey = new Survey();
        survey.setTopic(body.get("topic"));
        survey.setDescription(body.get("description"));
        return this.surveyRepository.save(survey);
    }

    @PutMapping("/survey/{id}")
    Survey update(@PathVariable String id, @RequestBody Map<String, String> body) {
        Survey survey = this.surveyRepository.findById(Integer.parseInt(id));
        survey.setTopic(body.get("topic"));
        survey.setDescription(body.get("description"));
        return this.surveyRepository.save(survey);
    }

    @DeleteMapping("/survey/{id}")
    boolean delete(@PathVariable String id) {
        Survey survey = new Survey();
        survey.setId(Integer.parseInt(id));
        this.surveyRepository.delete(survey);
        return true;
    }

    @GetMapping("/con_us_su/{uuid}")
    int[] showCon(@PathVariable("uuid") String uuid) {
        return this.surveyRepository.findSurveysByUserUuid(uuid);
    }

    @GetMapping("/answered/{uuid}")
    int[] showConAns(@PathVariable("uuid") String uuid) {
        return this.surveyRepository.findAnsweredSurveysByUserUuid(uuid);
    }

    @PostMapping("/con_us_su")
    Map<String, String> updateAnswer(@RequestBody Map<String, String> body) {
        int id = this.surveyRepository.findIdByUuidAndSurveyId(body.get("token"), Integer.parseInt(body.get("surveyId")));
        this.surveyRepository.updateAnswer(id);
        return ResultMap.createSuccessMap("Survey answered.");
    }

    @PostMapping("/surveyCreator")
    Map<String, String> createSurvey(@RequestBody SurveyDetailsData body) {
        if (body != null) {
            return creatorProcessor.process(body);
        } else return ResultMap.createNullBodyErrorMap();
    }

//    @GetMapping("/unansweredSurveys/{id}")
//    Set<Survey> getAllUnasweredSurveys(@PathVariable("id") String id){
//        int[] ids = this.surveyToUserRepository.findAllAnsweredSurveys(Integer.parseInt(id));
//        Set<Survey> surveys = new HashSet<>();
//        for(int i: ids){
//            surveys.add(this.surveyRepository.findSurveyForUser(i));
//        }
//        return surveys;
//    }

    @PostMapping("/answerKey")
    Map<Integer, Questions> getAnswersByKey(@RequestBody Map<String, String> body) {
        List<Answers> answers = answersRepository.findAnswersByKey(body.get("key"));
        Map<Integer, Questions> map = new HashMap<>();
        for (Answers a : answers) {
            map.put(a.getId(), a.getQuestion());
        }
        return map;
    }

    @PostMapping("/answers")
    Map<String, String> updateAnswersCount(@RequestBody AnswerDetailsData body) {
        if (body != null) {
            return questionAndAnswerIdHandler.process(body);
        } else return ResultMap.createNullBodyErrorMap();
    }
}
