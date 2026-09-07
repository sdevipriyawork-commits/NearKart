package com.nearkart.controller;

import com.nearkart.dto.CategoryDTO;
import com.nearkart.entity.Category;
import com.nearkart.service.CategoryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // =========================
    // CONSTRUCTOR
    // =========================

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // =========================
    // CREATE CATEGORY
    // =========================

    @PostMapping
    public Category createCategory(@RequestBody Category category) {

        return categoryService.createCategory(category);
    }

    // =========================
    // GET ALL CATEGORIES
    // =========================

    @GetMapping
    public List<CategoryDTO> getAllCategories() {

        return categoryService.getAllCategories();
    }

    // =========================
    // GET CATEGORY BY ID
    // =========================

    @GetMapping("/{id}")
    public CategoryDTO getCategoryById(
            @PathVariable Long id) {

        return categoryService.getCategoryById(id);
    }
    // =========================
// UPDATE CATEGORY
// =========================

    @PutMapping("/{id}")
    public Category updateCategory(
            @PathVariable Long id,
            @RequestBody Category category) {

        return categoryService.updateCategory(id, category);
    }

    // =========================
    // DELETE CATEGORY
    // =========================

    @DeleteMapping("/{id}")
    public void deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);
    }
}