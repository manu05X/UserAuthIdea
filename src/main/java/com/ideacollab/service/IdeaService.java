package com.ideacollab.service;

import com.ideacollab.dto.CollaborationDto;
import com.ideacollab.dto.IdeaDto;
import com.ideacollab.exception.ConflictException;
import com.ideacollab.exception.ResourceNotFoundException;
import com.ideacollab.exception.UnauthorizedAccessException;
import com.ideacollab.model.*;
import com.ideacollab.repository.CollaborationRepository;
import com.ideacollab.repository.IdeaRepository;
import com.ideacollab.repository.UserRepository;
import com.ideacollab.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private AuthService authService;

    @Autowired
    private CollaborationRepository collaborationRepository;


    public IdeaService(IdeaRepository ideaRepository,
                       UserRepository userRepository,
                       CollaborationRepository collaborationRepository) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.collaborationRepository = collaborationRepository;
    }


//    public List<IdeaDto> getAllIdeas(String sortBy, String sortOrder) {
//        Sort sort = createSort(sortBy, sortOrder);
//        return ideaRepository.findAll(sort).stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }

    public List<IdeaDto> getAllIdeas(String sortBy, String sortOrder) {
        if ("votes".equalsIgnoreCase(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                return ideaRepository.findAllOrderByVoteCountAsc().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
            } else {
                return ideaRepository.findAllOrderByVoteCountDesc().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
            }
        } else {
            Sort sort = createSort(sortBy, sortOrder);
            return ideaRepository.findAll(sort).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
    }

    private Sort createSort(String sortBy, String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }

    public IdeaDto getIdeaById(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found"));
        return convertToDto(idea);
    }

//    private Sort createSort(String sortBy, String sortOrder) {
//        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//
//        switch (sortBy.toLowerCase()) {
//            case "votes":
//                return Sort.by(direction, "votes.size");
//            case "createdat":
//                return Sort.by(direction, "createdAt");
//            case "updatedat":
//                return Sort.by(direction, "updatedAt");
//            default:
//                return Sort.by(direction, "createdAt");
//        }
//    }


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
        User currentUser = getCurrentUser(); // Gets from SecurityContext
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found"));

        // Check if the current user is the creator of the idea
        if (idea.getCreator().getEmail().equals(currentUser.getEmail())) {
            throw new UnauthorizedAccessException("Creators cannot vote on their own ideas");
        }

        Vote vote = voteRepository.findByUserAndIdea(currentUser, idea)
                .orElse(new Vote());

        vote.setUser(currentUser);
        vote.setIdea(idea);
        vote.setUpvote(upvote);

        voteRepository.save(vote);
    }



    public CollaborationDto addCollaborator(Long ideaId) throws ConflictException {
        User currentUser = getCurrentUser();
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found"));

        // Prevent self-collaboration
        if (idea.getCreator().equals(currentUser)) {
            throw new UnauthorizedAccessException("You cannot collaborate on your own idea");
        }

        // Check if already collaborating
        if (collaborationRepository.existsByUserAndIdea(currentUser, idea)) {
            throw new ConflictException("You are already collaborating on this idea");
        }

        Collaboration collaboration = new Collaboration();
        collaboration.setUser(currentUser);
        collaboration.setIdea(idea);
        Collaboration saved = collaborationRepository.save(collaboration);

        return convertToDto(saved);
    }

    public List<CollaborationDto> getCollaborators(Long ideaId) {
        // Find the idea by its ID
        Optional<Idea> optionalIdea = ideaRepository.findById(ideaId);
        if (optionalIdea.isEmpty()) {
            throw new ResourceNotFoundException("Idea not found");
        }

        Idea idea = optionalIdea.get();

        // Find all collaborations for the idea
        List<Collaboration> collaborations = collaborationRepository.findByIdea(idea);

        // Convert each collaboration to DTO
        List<CollaborationDto> dtoList = new ArrayList<>();
        for (Collaboration collaboration : collaborations) {
            CollaborationDto dto = convertToDto(collaboration);
            dtoList.add(dto);
        }

        return dtoList;
    }


    private CollaborationDto convertToDto(Collaboration collaboration) {
        Idea idea = collaboration.getIdea();
        CollaborationDto dto = new CollaborationDto();

        dto.setId(collaboration.getId());
        dto.setUserId(collaboration.getUser().getId());
        dto.setUserName(collaboration.getUser().getName());
        dto.setIdeaId(collaboration.getIdea().getId());
        dto.setIdeaTitle(collaboration.getIdea().getTitle());
        dto.setCreatorId(idea.getCreator().getId());
        dto.setJoinedAt(collaboration.getJoinedAt());


        return dto;
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