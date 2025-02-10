package com.deeppurple.backend.controller;

import com.deeppurple.backend.dto.CommunicationDTO;
import com.deeppurple.backend.entity.Communication;
import com.deeppurple.backend.entity.EmotionDetails;
import com.deeppurple.backend.service.CommunicationService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfWriter;
import jakarta.validation.Valid;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/communications")
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
        communication.setModelVersion(communicationDTO.getModelVersion());
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

    @PostMapping("/batch-upload")
    public Mono<ResponseEntity<byte[]>> batchUploadAndAnalyzeFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("modelName") String modelName) {

        // Debugging: Log input parameters
        System.out.println("Batch upload requested");
        System.out.println("Files: " + files.size() + " files received.");
        System.out.println("Model Name: " + modelName);

        List<Mono<Communication>> uploadTasks = files.stream()
                .map(file -> Mono.delay(Duration.ofSeconds(1)) // Introduce a delay of 1 second before each upload
                        .flatMap(aLong -> uploadAndAnalyzeFile(file, modelName))) // Chain the delay with the upload call
                .collect(Collectors.toList());

        // Debugging: Log the number of upload tasks being processed
        System.out.println("Number of tasks to upload and analyze: " + uploadTasks.size());

        return Flux.fromIterable(uploadTasks)
                .concatMap(uploadTask ->
                        uploadTask
                                .delayElement(Duration.ofMillis(500)) // Delay of 500ms between each task
                                .doOnSubscribe(subscription -> System.out.println("Starting upload for file"))
                )
                .collectList()
                .flatMap(communications -> {
                    try {
                        // Debugging: Log the number of communications received after analysis
                        System.out.println("Number of communications after processing: " + communications.size());

                        byte[] pdfBytes = generatePdfReport(communications);

                        // Debugging: Log the PDF generation
                        System.out.println("PDF report generated, size: " + pdfBytes.length + " bytes.");

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_PDF);
                        headers.setContentDisposition(ContentDisposition.attachment().filename("Batch_Report.pdf").build());

                        return Mono.just(new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK));
                    } catch (Exception e) {
                        // Debugging: Log error during PDF generation
                        System.err.println("Error generating PDF report: " + e.getMessage());
                        return Mono.error(new RuntimeException("Failed to generate PDF report", e));
                    }
                });
    }

    private byte[] generatePdfReport(List<Communication> communications) throws Exception {
        // Debugging: Log the start of PDF generation
        System.out.println("Generating PDF report for " + communications.size() + " communications...");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        for (Communication comm : communications) {
            // Debugging: Log details for each communication
            System.out.println("Processing communication for model: " + comm.getModelName());
            System.out.println("Primary Emotion: " + comm.getPrimaryEmotion().getEmotion() + " (" + comm.getPrimaryEmotion().getPercentage() + "%)");
            System.out.println("Summary: " + comm.getSummary());

            document.add(new Paragraph("Model: " + comm.getModelName()));
            document.add(new Paragraph("Primary Emotion: " + comm.getPrimaryEmotion().getEmotion() + " (" + comm.getPrimaryEmotion().getPercentage() + "%)"));
            document.add(new Paragraph("Secondary Emotions: " + comm.getSecondaryEmotions().stream()
                    .map(e -> e.getEmotion() + " (" + e.getPercentage() + "%)")
                    .collect(Collectors.joining(", "))));
            document.add(new Paragraph("Summary: " + comm.getSummary()));
            document.add(new Paragraph("Confidence Rating: " + comm.getConfidenceRating()));
            document.add(new Paragraph("Model Version: " + comm.getModelVersion()));
            document.add(new Paragraph("-----------------------------------"));
        }

        document.close();

        // Debugging: Log completion of PDF generation
        System.out.println("PDF report generation completed.");

        return outputStream.toByteArray();
    }



}
