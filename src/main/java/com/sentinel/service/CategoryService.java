package com.sentinel.service;

import com.sentinel.dto.CategoryRequest;
import com.sentinel.dto.CategoryResponse;
import com.sentinel.dto.CategoryUpdateRequest;
import com.sentinel.exception.BusinessException;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.model.Category;
import com.sentinel.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CategoryRequest request) {
        String normalizedName = request.getName().trim();

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new BusinessException("Já existe uma categoria com esse nome.");
        }

        Category category = Category.builder()
                .name(normalizedName)
                .active(true)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        String normalizedName = request.getName().trim();

        categoryRepository.findByNameIgnoreCase(normalizedName)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException("Já existe uma categoria com esse nome.");
                    }
                });

        category.setName(normalizedName);

        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .active(category.getActive())
                .build();
    }
}