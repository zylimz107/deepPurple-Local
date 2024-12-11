package com.deeppurple.backend.controller;

import com.deeppurple.backend.dto.CommunicationDTO;
import com.deeppurple.backend.entity.Communication;
import com.deeppurple.backend.entity.EmotionDetails;
import com.deeppurple.backend.service.CommunicationService;
import jakarta.validation.Valid;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/communications")
public class CommunicationController {
    private final CommunicationService service;
    private String extractTextFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
            return text.toString().trim();
        }
    }
    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document).trim();
        }
    }





    public CommunicationController(CommunicationService service) {
        this.service = service;
    }

    // Get all communications
    @GetMapping
    public Flux<Communication> getAllCommunications() {
        return Flux.fromIterable(service.getAllCommunications());
    }

    // Save a new communication with model and classification type
    @PostMapping
    public Mono<ResponseEntity<Communication>> saveCommunication(
            @Valid @RequestBody CommunicationDTO communicationDTO) {
        Communication communication = new Communication();
        communication.setModelName(communicationDTO.getModelName());
        communication.setContent(communicationDTO.getContent());
        EmotionDetails primaryEmotion = communicationDTO.getPrimaryEmotion();
        if (primaryEmotion != null) {
            communication.setPrimaryEmotion(primaryEmotion); // Set EmotionDetails object (with emotion and percentage)
        }

        // Extract and set secondary emotions as a list of EmotionDetails
        List<EmotionDetails> secondaryEmotions = communicationDTO.getSecondaryEmotions();
        if (secondaryEmotions != null && !secondaryEmotions.isEmpty()) {
            communication.setSecondaryEmotions(secondaryEmotions); // Set list of EmotionDetails
        }

        communication.setSummary(communicationDTO.getSummary());
        communication.setConfidenceRating(communicationDTO.getConfidenceRating());

        return service.saveCommunication(communicationDTO.getModelName(), communication)
                .map(savedCommunication -> ResponseEntity.ok(savedCommunication))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    // Get communication by ID
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Communication>> getCommunicationById(@PathVariable Long id) {
        return service.getCommunicationById(id)
                .map(ResponseEntity::ok)  // Return 200 OK with the communication entity
                .defaultIfEmpty(ResponseEntity.notFound().build());  // Return 404 if not found
    }

    // Update communication by ID with model and classification type
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Communication>> updateCommunication(
            @PathVariable Long id,
            @Valid @RequestBody CommunicationDTO communicationDTO) {
        Communication updatedCommunication = new Communication();
        updatedCommunication.setModelName(communicationDTO.getModelName());
        updatedCommunication.setContent(communicationDTO.getContent());
        EmotionDetails primaryEmotion = communicationDTO.getPrimaryEmotion();
        if (primaryEmotion != null) {
            updatedCommunication.setPrimaryEmotion(primaryEmotion); // Set EmotionDetails object (with emotion and percentage)
        }

        // Extract and set secondary emotions as a list of EmotionDetails
        List<EmotionDetails> secondaryEmotions = communicationDTO.getSecondaryEmotions();
        if (secondaryEmotions != null && !secondaryEmotions.isEmpty()) {
            updatedCommunication.setSecondaryEmotions(secondaryEmotions); // Set list of EmotionDetails
        }
        updatedCommunication.setSummary(communicationDTO.getSummary());
        updatedCommunication.setConfidenceRating(communicationDTO.getConfidenceRating());

        return service.updateCommunication(id, communicationDTO.getModelName(), updatedCommunication)
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Delete communication by ID
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCommunication(@PathVariable Long id) {
        return service.deleteCommunication(id)
                .flatMap(deleted -> deleted
                        ? Mono.just(ResponseEntity.noContent().build()) // Return 204 No Content for successful deletion
                        : Mono.just(ResponseEntity.notFound().build())); // Return 404 if not found
    }

    @PostMapping("/upload")
    public Mono<Communication> uploadAndAnalyzeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("modelName") String modelName ){

        String fileType = file.getContentType();
        String extractedText;


        try {
            if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(fileType)) {
                extractedText = extractTextFromDocx(file);
            } else if ("application/pdf".equals(fileType)) {
                extractedText = extractTextFromPdf(file);
            } else if ("text/plain".equals(fileType)) {
                extractedText = new String(file.getBytes(), StandardCharsets.UTF_8);
            } else {
                return Mono.error(new RuntimeException("Unsupported file type: " + fileType));
            }
        } catch (IOException e) {
            return Mono.error(new RuntimeException("Error reading file: " + e.getMessage(), e));
        }

        Communication communication = new Communication();
        communication.setContent(extractedText);
        communication.setModelName(modelName);

        return service.saveCommunication(modelName, communication);
    }

}
