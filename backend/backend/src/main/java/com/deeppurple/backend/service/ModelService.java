package com.deeppurple.backend.service;

import com.deeppurple.backend.entity.Model;
import com.deeppurple.backend.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModelService {
    @Autowired
    private ModelRepository modelRepository;

    public Model createModel(String name) {
        if (modelRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Model with this name already exists");
        }

        Model model = new Model();
        model.setName(name);
        model.setEmotionCategories(new ArrayList<>()); // Initialize the list explicitly
        Model savedModel = modelRepository.save(model);

        System.out.println("Created Model: " + savedModel);
        return savedModel;
    }

    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    public void deleteModel(Long id) {
        modelRepository.deleteById(id);
    }
}

