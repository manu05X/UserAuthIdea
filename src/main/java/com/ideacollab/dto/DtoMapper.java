package com.ideacollab.dto;



import com.ideacollab.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public IdeaDto toIdeaDto(Idea idea) {
        IdeaDto dto = new IdeaDto();
        // Basic fields
        dto.setTitle(idea.getTitle());
        dto.setDescription(idea.getDescription());

        // Creator info
        if (idea.getCreator() != null) {
            dto.setCreatorId(idea.getCreator().getId());
            dto.setCreatorName(idea.getCreator().getName());
        }

        // Timestamps
        dto.setCreatedAt(idea.getCreatedAt());
        dto.setUpdatedAt(idea.getUpdatedAt());

        // Votes count
        dto.setVoteCount((int) idea.getVotes().stream()
                .filter(Vote::isUpvote)
                .count());

        // Collaborations count
        dto.setCollaborationCount(idea.getCollaborations().size());

        // Tags - EAGER fetch or proper initialization needed
        dto.setTags(idea.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet()));

        return dto;
    }

    public UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        return dto;
    }

    public TagDto toTagDto(Tag tag) {
        TagDto dto = new TagDto();
        dto.setName(tag.getName());
        return dto;
    }

    public VoteDto toVoteDto(Vote vote) {
        VoteDto dto = new VoteDto();
        dto.setUserName(vote.getUser().getEmail());
        dto.setIdeaTitle(vote.getIdea().getTitle());
        dto.setUpvote(vote.isUpvote());
        return dto;
    }

    public CollaborationDto toCollaborationDto(Collaboration collaboration) {
        CollaborationDto dto = new CollaborationDto();
        dto.setIdeaTitle(collaboration.getIdea().getTitle());
        dto.setJoinedAt(collaboration.getJoinedAt());
        return dto;
    }

}