package com.artificery.BobShopBooksAPI.repository;

import com.artificery.BobShopBooksAPI.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
