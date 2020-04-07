package com.ProjektInzynierski.BackEnd.processors.registration;

import com.ProjektInzynierski.BackEnd.controller.LoggerController;
import com.ProjektInzynierski.BackEnd.data.entity.UserEntity;
import com.ProjektInzynierski.BackEnd.data.model.UserData;
import com.ProjektInzynierski.BackEnd.enums.RegistrationMsg;
import com.ProjektInzynierski.BackEnd.processors.validation.LoginValidationChain;
import com.ProjektInzynierski.BackEnd.repository.UsersRepository;
import com.ProjektInzynierski.BackEnd.util.ResultMap;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RegistrationProcessor {

    private final UsersRepository usersRepository;

    private Logger logger = LoggerController.getInstance();

    private ResultMap resultMap = new ResultMap();

    RegistrationProcessor(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    //ToDo this looks terrible mby create util? Do we need User class at all?
    public Map<String, String> process(Map<String, String> body) {

        logger.info("Start of registration validation.");
        body = LoginValidationChain.getValidationChainProcessor().process(body);
        if (body.get("error") != null) {
            logger.error("Error while registration validating.");
            return body;
        }
        logger.info("End of registration validation.");

        UserData userData = new UserData(body.get("email"), body.get("password"));
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userData.getEmail());
        userEntity.setPassword(userData.getPassword());

        UserEntity userEntityResult = usersRepository.save(userEntity);

        return resultMap.createRegistrationSuccessMap(RegistrationMsg.REGISTRY_SUCCESSFUL.getErrorMsg(), userEntityResult.getEmail());
    }
}