package com.artificery.BobShopBooksAPI;


import com.artificery.BobShopBooksAPI.entity.Category;
import com.artificery.BobShopBooksAPI.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(String name, Long parentId) {
        Category category = new Category();
        category.setCategoryName(name);
        category.setParentCategoryId(parentId);

        return categoryRepository.save(category);
    }

    public boolean categoriesNotYetInitialized() {
        return categoryRepository.findAll().isEmpty();
    }
}
