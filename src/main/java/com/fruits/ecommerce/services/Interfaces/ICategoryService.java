package com.fruits.ecommerce.services.Interfaces;

import com.fruits.ecommerce.exceptions.exceptionsDomain.categories.CategoryAlreadyExistsException;
import com.fruits.ecommerce.exceptions.exceptionsDomain.categories.CategoryInUseException;
import com.fruits.ecommerce.exceptions.exceptionsDomain.categories.InvalidCategoryDataException;
import com.fruits.ecommerce.exceptions.global.ResourceNotFoundException;
import com.fruits.ecommerce.models.dtos.CategoryDTO;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Validated
public interface ICategoryService {

    /**
     * Creates a new category.
     * @param categoryDTO The category data to create.
     * @return The created category.
     * @throws CategoryAlreadyExistsException If a category with the same name already exists.
     * @throws InvalidCategoryDataException If the category data is invalid.
     * @throws jakarta.validation.ConstraintViolationException If the category name is blank.
     */
    CategoryDTO createCategory(@Valid @NotNull CategoryDTO categoryDTO) throws
            CategoryAlreadyExistsException, InvalidCategoryDataException, ConstraintViolationException;

    /**
     * Retrieves all categories.
     * @return A list of all categories.
     */
    List<CategoryDTO> getAllCategories();

    /**
     * Retrieves a category by its ID.
     * @param id The ID of the category to retrieve.
     * @return The category if found.
     * @throws ResourceNotFoundException If no category is found with the given ID.
     */
    Optional<CategoryDTO> getCategoryById(@NotNull Long id) throws ResourceNotFoundException;


    /**
     * Updates an existing category.
     * @param id The ID of the category to update.
     * @param categoryDTO The updated category data.
     * @return The updated category.
     * @throws ResourceNotFoundException If no category is found with the given ID.
     * @throws InvalidCategoryDataException If the updated category data is invalid.
     * @throws CategoryAlreadyExistsException If updating would result in a duplicate category name.
     * @throws jakarta.validation.ConstraintViolationException If the updated category name is blank.
     */
    CategoryDTO updateCategory(@NotNull Long id, @Valid @NotNull CategoryDTO categoryDTO)
            throws ResourceNotFoundException, InvalidCategoryDataException, CategoryAlreadyExistsException;


    /**
     * Deletes a category by its ID.
     * @param id The ID of the category to delete.
     * @throws ResourceNotFoundException If no category is found with the given ID.
     * @throws CategoryInUseException If the category is associated with any products.
     */
    void deleteCategory(@NotNull Long id) throws ResourceNotFoundException, CategoryInUseException;
}


