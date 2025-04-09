package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;
    public Recommendation generateRecommendation(Activity activity) {
        String promt = createPromtForActivity(activity);
        String aiRespone = geminiService.getAnswer(promt);
        log.info("RESPONSE FROM AI: {} ", aiRespone);
        processAiResponse(activity, aiRespone);
        return processAiResponse(activity, aiRespone);
    }
    private Recommendation processAiResponse(Activity activity, String aiRespone) {
        try{
            ObjectMapper mapper = new ObjectMapper(); //ObjectMapper provides functionality for reading and writing Json
            JsonNode rootNode = mapper.readTree(aiRespone);

          JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");



            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();
            //log.info("PARSE REPONSE FROM AI: {} ", jsonContent);
            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");


            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace::");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories:");


            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));
            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        catch(Exception e){
            e.printStackTrace();
            return  createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailde analysis")
                .improvements(Collections.singletonList("Continue with your current routine "))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safetyList = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(item -> safetyList.add(item.asText()));
        }
        return safetyList.isEmpty() ?
                Collections.singletonList("Follow general safety guidelines"):
                safetyList;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestionList = new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestionList.add(String.format("%s (%s)", workout, description));
            });
        }
        return suggestionList.isEmpty() ?
                Collections.singletonList("No specific suggestions provided"):
                suggestionList;

    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s (%s)", area, detail));
            });
        }
        return improvements.isEmpty() ?
                Collections.singletonList("No specific improvements provided"):
                improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromtForActivity(Activity activity) {
        return String.format("""
                        Analyze this fitness activity and provide detail recommendation in the following EXACT JSON format:
                        {
                            "analysis:{
                                "overall": "Overall analysis here",
                                "pace": "Pace analysis here",
                                "heartRate": "Heart rate analysis here",
                                "caloriesBurned": "Calories analysis here",
                            },
                            "improvements": [
                                {
                                    "area": "Area name",
                                    "recommendation": "Detailed recommendation"
                                }
                            ],
                            "suggestions": [
                                {
                                    "workout": "Workout name",
                                    "description": "Detailed workout description"
                                }
                            ],
                            "safety": [
                                "Safety point 1",
                                "Safety point 2"
                            ]
                        }
                        Analyze this activity:
                        Activity Type: %s
                        Duration: %d minutes
                        Calories Burned: %d 
                        Additional Metrics: %S
                        
                        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
                        Ensure the response follows the EXACT JSON format shown above.
                        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );

    }
}
