package com.fruits.ecommerce.services.implementations;

import com.fruits.ecommerce.exceptions.exceptionsDomain.categories.CategoryAlreadyExistsException;
import com.fruits.ecommerce.exceptions.exceptionsDomain.categories.CategoryInUseException;
import com.fruits.ecommerce.exceptions.exceptionsDomain.categories.InvalidCategoryDataException;
import com.fruits.ecommerce.exceptions.global.ResourceNotFoundException;
import com.fruits.ecommerce.models.dtos.CategoryDTO;
import com.fruits.ecommerce.models.entities.Category;
import com.fruits.ecommerce.models.mappers.CategoryMapper;
import com.fruits.ecommerce.repository.CategoryRepository;
import com.fruits.ecommerce.services.Interfaces.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDTO createCategory(@Valid @NotNull CategoryDTO categoryDTO)
            throws CategoryAlreadyExistsException, InvalidCategoryDataException, ConstraintViolationException {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new CategoryAlreadyExistsException("Category with name " + categoryDTO.getName() + " already exists");
        }

        try {
            Category category = categoryMapper.toEntity(categoryDTO);
            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toDTO(savedCategory);
        } catch (Exception e) {
            throw new InvalidCategoryDataException("Invalid category data", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryById(@NotNull Long id) throws ResourceNotFoundException {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO);
    }

    @Override
    public CategoryDTO updateCategory(@NotNull Long id, @Valid @NotNull CategoryDTO categoryDTO)
            throws ResourceNotFoundException, InvalidCategoryDataException, CategoryAlreadyExistsException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getName().equals(categoryDTO.getName()) &&
                categoryRepository.existsByName(categoryDTO.getName())) {
            throw new CategoryAlreadyExistsException("Category with name " + categoryDTO.getName() + " already exists");
        }

        try {
            categoryMapper.updateEntityFromDTO(categoryDTO, category);
            Category updatedCategory = categoryRepository.save(category);
            return categoryMapper.toDTO(updatedCategory);
        } catch (Exception e) {
            throw new InvalidCategoryDataException("Invalid category data", e);
        }
    }

    @Override
    public void deleteCategory(@NotNull Long id) throws ResourceNotFoundException, CategoryInUseException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getProducts().isEmpty()) {
            throw new CategoryInUseException("Cannot delete category as it is associated with products");
        }
        categoryRepository.delete(category);
    }
}