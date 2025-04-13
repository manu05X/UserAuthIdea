package com.ideacollab.repository;

import com.ideacollab.model.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    @Query("SELECT i FROM Idea i LEFT JOIN i.votes v GROUP BY i ORDER BY COUNT(v) ASC")
    List<Idea> findAllOrderByVoteCountAsc();

    @Query("SELECT i FROM Idea i LEFT JOIN i.votes v GROUP BY i ORDER BY COUNT(v) DESC")
    List<Idea> findAllOrderByVoteCountDesc();
}