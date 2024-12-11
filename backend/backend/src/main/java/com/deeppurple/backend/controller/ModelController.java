package com.deeppurple.backend.controller;

import com.deeppurple.backend.entity.Model;
import com.deeppurple.backend.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/models")
public class ModelController {
    @Autowired
    private ModelService modelService;

    @PostMapping
    public Model createModel(@RequestParam String name) {
        return modelService.createModel(name);
    }

    // Fetch all models and their associated emotion categories
    @GetMapping
    public ResponseEntity<List<Model>> getAllModels() {
        List<Model> models = modelService.getAllModels();
        return ResponseEntity.ok(models);  // This will return the List<Model> with emotion categories
    }


    @DeleteMapping("/{id}")
    public void deleteModel(@PathVariable Long id) {
        modelService.deleteModel(id);
    }
}

