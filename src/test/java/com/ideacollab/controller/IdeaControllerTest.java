package com.ideacollab.controller;

import com.ideacollab.dto.CollaborationDto;
import com.ideacollab.dto.IdeaDto;
import com.ideacollab.exception.ConflictException;
import com.ideacollab.exception.ResourceNotFoundException;
import com.ideacollab.exception.UnauthorizedAccessException;
import com.ideacollab.service.IdeaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class IdeaControllerTest {

    @InjectMocks
    private IdeaController ideaController;

    @Mock
    private IdeaService ideaService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllIdeas() {
        List<IdeaDto> mockIdeas = List.of(new IdeaDto(), new IdeaDto());
        when(ideaService.getAllIdeas("createdAt", "desc")).thenReturn(mockIdeas);

        ResponseEntity<List<IdeaDto>> response = ideaController.getAllIdeas("createdAt", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockIdeas, response.getBody());
        verify(ideaService, times(1)).getAllIdeas("createdAt", "desc");
    }

    @Test
    void testGetIdeaById() {
        IdeaDto mockIdea = new IdeaDto();
        when(ideaService.getIdeaById(1L)).thenReturn(mockIdea);

        ResponseEntity<IdeaDto> response = ideaController.getIdeaById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockIdea, response.getBody());
        verify(ideaService, times(1)).getIdeaById(1L);
    }

    @Test
    void testCreateIdea() {
        IdeaDto request = new IdeaDto();
        IdeaDto mockResponse = new IdeaDto();
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(ideaService.createIdea(request, "test@example.com")).thenReturn(mockResponse);

        ResponseEntity<IdeaDto> response = ideaController.createIdea(request, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(ideaService, times(1)).createIdea(request, "test@example.com");
    }

    @Test
    void testUpdateIdea() {
        IdeaDto request = new IdeaDto();
        IdeaDto mockResponse = new IdeaDto();
        when(ideaService.updateIdea(1L, request)).thenReturn(mockResponse);

        ResponseEntity<IdeaDto> response = ideaController.updateIdea(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(ideaService, times(1)).updateIdea(1L, request);
    }

    @Test
    void testDeleteIdea() {
        doNothing().when(ideaService).deleteIdea(1L);

        ResponseEntity<Map<String, String>> response = ideaController.deleteIdea(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Idea deleted successfully", response.getBody().get("message"));
        verify(ideaService, times(1)).deleteIdea(1L);
    }

    @Test
    void testVoteIdea_Upvote() throws UnauthorizedAccessException, ResourceNotFoundException {
        doNothing().when(ideaService).voteIdea(1L, true);

        ResponseEntity<Map<String, String>> response = ideaController.voteIdea(1L, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Upvoted successfully!", response.getBody().get("message"));
        verify(ideaService, times(1)).voteIdea(1L, true);
    }

    @Test
    void testVoteIdea_Exception() throws UnauthorizedAccessException, ResourceNotFoundException {
        doThrow(new UnauthorizedAccessException("Access denied")).when(ideaService).voteIdea(1L, true);

        ResponseEntity<Map<String, String>> response = ideaController.voteIdea(1L, true);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().get("error"));
        verify(ideaService, times(1)).voteIdea(1L, true);
    }

    @Test
    void testCollaborateOnIdea() throws ConflictException {
        CollaborationDto mockCollaboration = new CollaborationDto();
        when(ideaService.addCollaborator(1L)).thenReturn(mockCollaboration);

        ResponseEntity<CollaborationDto> response = ideaController.collaborateOnIdea(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCollaboration, response.getBody());
        verify(ideaService, times(1)).addCollaborator(1L);
    }

    @Test
    void testGetCollaborators() {
        List<CollaborationDto> mockCollaborators = List.of(new CollaborationDto(), new CollaborationDto());
        when(ideaService.getCollaborators(1L)).thenReturn(mockCollaborators);

        ResponseEntity<List<CollaborationDto>> response = ideaController.getCollaborators(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCollaborators, response.getBody());
        verify(ideaService, times(1)).getCollaborators(1L);
    }
}