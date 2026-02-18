package com.sentinel.service;

import com.sentinel.dto.CategoryRequest;
import com.sentinel.dto.CategoryResponse;
import com.sentinel.entity.Category;
import com.sentinel.exception.BusinessException;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CategoryRequest request) {

        categoryRepository.findByName(request.getName())
                .ifPresent(c -> {
                    throw new BusinessException("Categoria já existe.");
                });

        Category category = Category.builder()
                .name(request.getName())
                .build();

        categoryRepository.save(category);

        return new CategoryResponse(category.getId(), category.getName());
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    public void delete(Long id) {

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada.");
        }

        categoryRepository.deleteById(id);
    }
}
