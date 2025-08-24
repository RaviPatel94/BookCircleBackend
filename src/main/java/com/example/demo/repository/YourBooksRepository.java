package com.example.demo.repository;

import com.example.demo.entity.YourBooks;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YourBooksRepository extends JpaRepository<YourBooks, Long> {

    List<YourBooks> findByUserOrderByCreatedDateDesc(User user);

    Optional<YourBooks> findByIdAndUser(Long id, User user);

    Page<YourBooks> findByUserOrderByCreatedDateDesc(User user, Pageable pageable);

    @Query("SELECT b FROM YourBooks b WHERE b.user = :user AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<YourBooks> findByUserAndTitleOrAuthorContainingIgnoreCase(
            @Param("user") User user,
            @Param("search") String search
    );

    long countByUser(User user);
}