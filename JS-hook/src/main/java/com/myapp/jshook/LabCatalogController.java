package com.myapp.jshook;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class LabCatalogController {
    private final LabCatalogService labCatalogService;

    public LabCatalogController(LabCatalogService labCatalogService) {
        this.labCatalogService = labCatalogService;
    }

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        return labCatalogService.getOverview();
    }

    @GetMapping("/challenges")
    public List<Map<String, Object>> challenges(
        @RequestParam(value = "trackKey", required = false) String trackKey,
        @RequestParam(value = "difficulty", required = false) String difficulty,
        @RequestParam(value = "mode", required = false) String mode
    ) {
        return labCatalogService.listChallenges(trackKey, difficulty, mode);
    }

    @GetMapping("/challenges/{challengeId}")
    public ResponseEntity<?> challenge(@PathVariable("challengeId") String challengeId) {
        Map<String, Object> item = labCatalogService.getChallenge(challengeId);
        if (item.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("challenge not found", challengeId));
        }
        return ResponseEntity.ok(item);
    }

    @PostMapping("/sync")
    public Map<String, Object> sync() {
        int count = labCatalogService.syncChallengesFromJson();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("synced", count);
        return result;
    }

    @PostMapping("/sync-rules")
    public Map<String, Object> syncRules() {
        int count = labCatalogService.syncJudgeRulesFromJson();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put("syncedRules", count);
        return result;
    }

    @PostMapping("/submissions")
    public ResponseEntity<?> createSubmission(@RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(labCatalogService.saveSubmission(body));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage(), body.get("challengeId")));
        }
    }

    @PostMapping("/judge")
    public ResponseEntity<?> judge(@RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(labCatalogService.judgeSubmission(body));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage(), body.get("challengeId")));
        }
    }

    @GetMapping("/submissions")
    public List<Map<String, Object>> submissions(
        @RequestParam(value = "challengeId", required = false) String challengeId
    ) {
        return labCatalogService.listSubmissions(challengeId);
    }

    @GetMapping("/rules")
    public List<Map<String, Object>> rules() {
        return labCatalogService.listJudgeRules();
    }

    private Map<String, Object> error(String message, Object detail) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", false);
        result.put("message", message);
        result.put("detail", detail);
        return result;
    }
}
