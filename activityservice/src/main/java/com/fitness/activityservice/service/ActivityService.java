package com.fitness.activityservice.service;

import com.fitness.activityservice.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    @Autowired
    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {
        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if (!isValidUser) {
            throw new  RuntimeException("Invalid user " + request.getUserId());
        }

        Activity activity   = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();
        Activity saveActivity  = activityRepository.save(activity);
        //Publish to RabbitMQ for AI Processing
        try{
            rabbitTemplate.convertAndSend(exchange, routingKey, saveActivity);
        }
        catch(Exception e){
                log.error("Failed to publish activity to RabbitMQ: ", e);
        }

        return maptoActivityResponse(saveActivity);
    }
    private ActivityResponse maptoActivityResponse(Activity activity) {
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(activity.getId());
        activityResponse.setType(activity.getType());
        activityResponse.setUserId(activity.getUserId());
        activityResponse.setDuration(activity.getDuration());
        activityResponse.setCaloriesBurned(activity.getCaloriesBurned());
        activityResponse.setStartTime(activity.getStartTime());
        activityResponse.setAdditionalMetrics(activity.getAdditionalMetrics());
        activityResponse.setCreateAt(activity.getCreateAt());
        activityResponse.setUpdateAt(activity.getUpdateAt());
        return activityResponse;
    }

    public List<ActivityResponse> getUserActivities(String userId) {

        List<Activity> activities = activityRepository.findByUserId(userId);


        return activities.stream().map(this::maptoActivityResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String activityId) {
        return activityRepository.findById(activityId)
                .map(this::maptoActivityResponse)
                .orElseThrow(() -> new  RuntimeException("Activity not found with id: " + activityId));

    }
}
