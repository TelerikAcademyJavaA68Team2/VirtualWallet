package com.example.virtualwallet.loggers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.String.format;

@Aspect
@Component
public class LoggerAspect {

    private static final Logger logger = Logger.getLogger(LoggerAspect.class.getName());

    private final ObjectMapper mapper;

    @Autowired
    public LoggerAspect(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Pointcut("within(com.example.virtualwallet.controllers.rest..*)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void logServiceMethods(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequestMapping mapping = AnnotationUtils.findAnnotation(signature.getMethod(), RequestMapping.class);

        Map<String, Object> parameters = getParameters(joinPoint);

        try {
            logger.info(format("==> path: {%s}, method: {%s}, headers: {%s} ,  arguments: {%s} ",
                    Arrays.toString(mapping.path()), Arrays.toString(mapping.method()),
                    Arrays.toString(mapping.headers()), mapper.writeValueAsString(parameters)));
        } catch (JsonProcessingException e) {
            logger.info("Error while converting" + e);
        }
    }

    @AfterReturning(pointcut = "pointcut()", returning = "entity")
    public void logMethodAfter(JoinPoint joinPoint, ResponseEntity<?> entity) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequestMapping mapping = AnnotationUtils.findAnnotation(signature.getMethod(), RequestMapping.class);

        try {
            logger.info(format("<== path: {%s}, method: {%s}, headers: {%s} , retuning: {%s}",
                    Arrays.toString(mapping.path()), Arrays.toString(mapping.method()),
                    Arrays.toString(mapping.headers()), mapper.writeValueAsString(entity)));
        } catch (JsonProcessingException e) {
            logger.info("Error while converting" + e);
        }
    }


    private Map<String, Object> getParameters(JoinPoint joinPoint) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();

        HashMap<String, Object> map = new HashMap<>();

        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], joinPoint.getArgs()[i]);
        }

        return map;
    }
}
