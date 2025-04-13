package com.ideacollab.service;

import com.ideacollab.dto.IdeaDto;
import com.ideacollab.exception.ResourceNotFoundException;
import com.ideacollab.exception.UnauthorizedAccessException;
import com.ideacollab.model.Collaboration;
import com.ideacollab.model.Idea;
import com.ideacollab.model.User;
import com.ideacollab.model.Vote;
import com.ideacollab.model.Tag;
import com.ideacollab.repository.CollaborationRepository;
import com.ideacollab.repository.IdeaRepository;
import com.ideacollab.repository.UserRepository;
import com.ideacollab.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CollaborationRepository collaborationRepository;

    public List<IdeaDto> getAllIdeas() {
        return ideaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public IdeaDto getIdeaById(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found"));
        return convertToDto(idea);
    }

    public IdeaDto createIdea(IdeaDto ideaDto, String userEmail) {

        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Idea idea = new Idea();
        idea.setTitle(ideaDto.getTitle());
        idea.setDescription(ideaDto.getDescription());
        idea.setCreator(creator);
        idea.setCreatedAt(LocalDateTime.now());
        idea.setUpdatedAt(LocalDateTime.now());

        Idea savedIdea = ideaRepository.save(idea);
        return convertToDto(savedIdea);
    }

    public IdeaDto updateIdea(Long id, IdeaDto ideaDto) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found"));

        User currentUser = getCurrentUser();
        
        if (!idea.getCreator().getEmail().equals(currentUser.getEmail())) {
            throw new UnauthorizedAccessException("Not authorized to update this idea");
        }

        idea.setTitle(ideaDto.getTitle());
        idea.setDescription(ideaDto.getDescription());
        return convertToDto(ideaRepository.save(idea));
    }

    public void deleteIdea(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found"));

        User currentUser = getCurrentUser();

        if (!idea.getCreator().getEmail().equals(currentUser.getEmail())) {
            throw new UnauthorizedAccessException("Not authorized to delete this idea");
        }

        ideaRepository.delete(idea);
    }

    public void voteIdea(Long ideaId, boolean upvote) {
        User currentUser = getCurrentUser();
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found"));

        Vote vote = voteRepository.findByUserAndIdea(currentUser, idea)
                .orElse(new Vote());
        
        vote.setUser(currentUser);
        vote.setIdea(idea);
        vote.setUpvote(upvote);
        
        voteRepository.save(vote);
    }

    public void addCollaborator(Long ideaId) {
        User currentUser = getCurrentUser();
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found"));

        if (collaborationRepository.existsByUserAndIdea(currentUser, idea)) {
            return;
        }

        Collaboration collaboration = new Collaboration();
        collaboration.setUser(currentUser);
        collaboration.setIdea(idea);
        collaborationRepository.save(collaboration);
    }

    private User getCurrentUser() {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(emailId)
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
    }

    public IdeaDto convertToDto(Idea idea) {
        IdeaDto dto = new IdeaDto();
        dto.setTitle(idea.getTitle());
        dto.setDescription(idea.getDescription());

        // Map creator information
        if (idea.getCreator() != null) {
            dto.setCreatorId(idea.getCreator().getId());
            dto.setCreatorName(idea.getCreator().getName());
        }

        // Map timestamps
        dto.setCreatedAt(idea.getCreatedAt());
        dto.setUpdatedAt(idea.getUpdatedAt());

        // Map counts
        dto.setVoteCount(idea.getVotes() != null ? idea.getVotes().size() : 0);
        dto.setCollaborationCount(idea.getCollaborations() != null ? idea.getCollaborations().size() : 0);

        // Map tags if needed
        if (idea.getTags() != null) {
            dto.setTags(idea.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }
}