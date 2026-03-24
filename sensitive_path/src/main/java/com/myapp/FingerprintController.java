package com.myapp;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FingerprintController {
    private final FingerprintCatalogService fingerprintCatalogService;

    public FingerprintController(FingerprintCatalogService fingerprintCatalogService) {
        this.fingerprintCatalogService = fingerprintCatalogService;
    }

    @GetMapping(value = "/", produces = "text/html;charset=UTF-8")
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/fingerprint")
    public String fingerprint() {
        return "redirect:/";
    }

    @GetMapping("/sensitive-path")
    public String legacySensitivePath() {
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/fingerprint/api/catalog")
    public Map<String, Object> catalog(
        @RequestParam(value = "dataset", required = false) String dataset,
        @RequestParam(value = "matchType", required = false) String matchType,
        @RequestParam(value = "q", required = false) String keyword
    ) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("overview", fingerprintCatalogService.getOverview());
        result.put("items", fingerprintCatalogService.listSummaries(dataset, matchType, keyword));
        return result;
    }

    @ResponseBody
    @GetMapping("/fingerprint/api/records/{recordId}")
    public ResponseEntity<?> record(@PathVariable("recordId") String recordId) {
        Map<String, Object> item = fingerprintCatalogService.getRecordDetail(recordId);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("record not found", recordId));
        }
        return ResponseEntity.ok(item);
    }

    @ResponseBody
    @GetMapping("/fingerprint/api/sample/{recordId}")
    public ResponseEntity<?> sample(@PathVariable("recordId") String recordId) {
        Map<String, Object> sample = fingerprintCatalogService.getSample(recordId);
        if (sample == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("record not found", recordId));
        }
        return ResponseEntity.ok(sample);
    }

    @ResponseBody
    @PostMapping("/fingerprint/api/match")
    public Map<String, Object> match(@RequestBody Map<String, Object> payload) {
        return fingerprintCatalogService.match(payload);
    }

    @ResponseBody
    @PostMapping("/fingerprint/api/reload")
    public Map<String, Object> reload() {
        return fingerprintCatalogService.reloadCatalog();
    }

    private Map<String, Object> error(String message, Object detail) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", false);
        result.put("message", message);
        result.put("detail", detail);
        return result;
    }
}
