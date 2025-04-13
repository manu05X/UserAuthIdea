package com.ideacollab.repository;

import com.ideacollab.model.Idea;
import com.ideacollab.model.User;
import com.ideacollab.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndIdea(User user, Idea idea);
    long countByIdeaAndUpvoteTrue(Idea idea);
    long countByIdeaAndUpvoteFalse(Idea idea);
}