package com.ideacollab.repository;

import com.ideacollab.model.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    // Basic CRUD operations are provided by JpaRepository
}