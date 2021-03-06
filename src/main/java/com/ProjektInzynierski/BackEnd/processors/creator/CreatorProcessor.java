package com.ProjektInzynierski.BackEnd.processors.creator;

import com.ProjektInzynierski.BackEnd.controller.LoggerController;
import com.ProjektInzynierski.BackEnd.data.entity.Answers;
import com.ProjektInzynierski.BackEnd.data.entity.Questions;
import com.ProjektInzynierski.BackEnd.data.entity.Survey;
import com.ProjektInzynierski.BackEnd.data.model.QuestionData;
import com.ProjektInzynierski.BackEnd.data.model.SurveyDetailsData;
import com.ProjektInzynierski.BackEnd.enums.CreatorMsg;
import com.ProjektInzynierski.BackEnd.processors.ProcessInterface;
import com.ProjektInzynierski.BackEnd.repository.AnswersRepository;
import com.ProjektInzynierski.BackEnd.repository.QuestionRepository;
import com.ProjektInzynierski.BackEnd.repository.SurveyRepository;
import com.ProjektInzynierski.BackEnd.util.AnswerRep;
import com.ProjektInzynierski.BackEnd.util.Iterator;
import com.ProjektInzynierski.BackEnd.util.ResultMap;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * This class is responsible for handling creator process
 */
@Component
public class CreatorProcessor extends ProcessInterface {

    private Logger logger = LoggerController.getInstance();

    private final SurveyRepository surveyRepository;

    private final QuestionRepository questionRepository;

    private final AnswersRepository answersRepository;

    public CreatorProcessor(SurveyRepository surveyRepository, QuestionRepository questionRepository, AnswersRepository answersRepository) {
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.answersRepository = answersRepository;
    }

    /**
     * This method persist into database survey, question and answer data created out of json mapped into surveyDetailsData.
     *
     * @param surveyDetailsData contains data of survey to be created.
     * @return Map<String, String> contains data to be returned respectively success or error message.
     */
    @Override
    public Map<String, String> process(SurveyDetailsData surveyDetailsData) {

        logger.info("Start of creation process.");
        try {

            if (surveyDetailsData != null && !surveyDetailsData.getQuestions().isEmpty()) {
                int nullElements = 0;
                Survey survey = saveSurvey(surveyDetailsData);
                for (QuestionData element : surveyDetailsData.getQuestions()) {
                    if (element != null) {
                        Questions questions = new Questions.QuestionsBuilder()
                                .setType(element.getType())
                                .setQuestion(element.getQuestion())
                                .setSurvey(survey)
                                .build();

                        Questions question = questionRepository.save(questions);
                        if (element.getAnswers() != null) {

                            AnswerRep answers = new AnswerRep();
                            answers.setAnswers(element.getAnswers());

                            for (Iterator iter = answers.getIterator(); iter.hasNext(); ) {
                                String string = (String) iter.next();
                                if (!string.equals("") && !string.equals(" ")) {
                                    Answers answersObj = new Answers();
                                    answersObj.setAnswer(string);
                                    answersObj.setQuestion(question);
                                    answersRepository.save(answersObj);
                                }
                            }
                        }
                    } else {
                        nullElements++;
                        if (nullElements == surveyDetailsData.getQuestions().size()) {
                            surveyRepository.delete(survey);
                            logger.error("Creation went wrong questions data is null.");
                            return ResultMap.createErrorMap(CreatorMsg.CREATOR_NULL_QUESTIONS_DATA_ERROR.getErrorMsg());
                        }
                    }
                }
            } else {
                logger.error("Creation went wrong survey data is null.");
                return ResultMap.createErrorMap(CreatorMsg.CREATOR_NULL_ERROR.getErrorMsg());
            }

            logger.info("Creation successful.");
            return ResultMap.createSuccessMap(CreatorMsg.CREATOR_SUCCESS.getErrorMsg());

        } catch (Exception e) {
            logger.error("Creation went wrong unexpected error.");
            return ResultMap.createErrorMap(CreatorMsg.CREATOR_UNEXPECTED_ERROR.getErrorMsg());
        }
    }

    /**
     * This method persist survey (topic, description) created out of surveyDetailsData into database.
     *
     * @param surveyDetailsData contains data to be persisted.
     * @return Survey contains data of saved Survey entity.
     */
    private Survey saveSurvey(SurveyDetailsData surveyDetailsData) {
        Survey survey = new Survey();
        survey.setTopic(surveyDetailsData.getTitle());
        survey.setDescription(surveyDetailsData.getDescription());
        return surveyRepository.save(survey);
    }
}
