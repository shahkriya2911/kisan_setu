package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //interface that talks with DB
public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
}
