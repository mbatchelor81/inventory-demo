package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    List<Category> findByParentCategoryId(Long parentId);
    List<Category> findByActiveTrue();
}
