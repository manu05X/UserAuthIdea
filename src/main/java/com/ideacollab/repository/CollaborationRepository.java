package com.ideacollab.repository;

import com.ideacollab.model.Collaboration;
import com.ideacollab.model.Idea;
import com.ideacollab.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborationRepository extends JpaRepository<Collaboration, Long> {
    boolean existsByUserAndIdea(User user, Idea idea);
    List<Collaboration> findByIdea(Idea idea);
    List<Collaboration> findByUser(User user);
}