package com.budgettracker.controller;


import com.budgettracker.entity.Category;
import com.budgettracker.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    // We use Service class, not the impl class because we generally program to an interface rather than a concrete implementation.
    // Spring injects the appropriate implementation automatically.


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);

    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);

    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id,  @RequestBody Category category) {
        //@Pathvariable extracts the particular id and @Requestbody gives new value to a category
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);

    }
}
