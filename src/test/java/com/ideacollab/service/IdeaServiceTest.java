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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdeaServiceTest {

    @InjectMocks
    private IdeaService ideaService;

    @Mock
    private IdeaRepository ideaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private CollaborationRepository collaborationRepository;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllIdeas() {
        Idea idea = new Idea();
        idea.setTitle("Test Idea");
        List<Idea> ideas = List.of(idea);

        when(ideaRepository.findAll(any(Sort.class))).thenReturn(ideas);

        List<IdeaDto> result = ideaService.getAllIdeas("createdAt", "desc");

        assertEquals(1, result.size());
        assertEquals("Test Idea", result.get(0).getTitle());
        verify(ideaRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testGetIdeaById() {
        Idea idea = new Idea();
        idea.setTitle("Test Idea");

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));

        IdeaDto result = ideaService.getIdeaById(1L);

        assertEquals("Test Idea", result.getTitle());
        verify(ideaRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateIdea() {
        User user = new User();
        user.setEmail("test@example.com");

        Idea idea = new Idea();
        idea.setTitle("Test Idea");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(ideaRepository.save(any(Idea.class))).thenReturn(idea);

        IdeaDto result = ideaService.createIdea(new IdeaDto(), "test@example.com");

        assertEquals("Test Idea", result.getTitle());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(ideaRepository, times(1)).save(any(Idea.class));
    }

    @Test
    void testUpdateIdea_Unauthorized() {
        Idea idea = new Idea();
        User creator = new User();
        creator.setEmail("creator@example.com");
        idea.setCreator(creator);

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));

        User currentUser = new User();
        currentUser.setEmail("user@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));

        assertThrows(UnauthorizedAccessException.class, () -> ideaService.updateIdea(1L, new IdeaDto()));
    }

    @Test
    void testDeleteIdea() {
        Idea idea = new Idea();
        User creator = new User();
        creator.setEmail("test@example.com");
        idea.setCreator(creator);

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(creator));

        ideaService.deleteIdea(1L);

        verify(ideaRepository, times(1)).delete(idea);
    }

}