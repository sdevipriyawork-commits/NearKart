package com.nearkart.service;

import com.nearkart.dto.CategoryDTO;
import com.nearkart.entity.Category;
import com.nearkart.exception.ResourceNotFoundException;
import com.nearkart.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    // =========================
    // CONSTRUCTOR
    // =========================

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    // =========================
    // CREATE CATEGORY
    // =========================

    public Category createCategory(Category category) {

        return categoryRepository.save(category);
    }


    // =========================
    // GET ALL CATEGORIES
    // =========================

    public List<CategoryDTO> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================
    // GET CATEGORY BY ID
    // =========================

    public CategoryDTO getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        return convertToDTO(category);
    }
    // =========================
// UPDATE CATEGORY
// =========================

    public Category updateCategory(
            Long id,
            Category updatedCategory) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        category.setName(updatedCategory.getName());
        category.setDescription(updatedCategory.getDescription());

        return categoryRepository.save(category);
    }


    // =========================
    // DELETE CATEGORY
    // =========================

    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        categoryRepository.delete(category);
    }


    // =========================
    // CONVERT CATEGORY TO DTO
    // =========================

    private CategoryDTO convertToDTO(Category category) {

        CategoryDTO dto = new CategoryDTO();

        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());

        return dto;
    }
}