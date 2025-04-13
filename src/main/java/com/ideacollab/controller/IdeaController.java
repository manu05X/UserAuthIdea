package com.ideacollab.controller;

import com.ideacollab.dto.IdeaDto;
import com.ideacollab.service.IdeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ideas")
public class IdeaController {

    @Autowired
    private IdeaService ideaService;

    @GetMapping("/")
    public ResponseEntity<List<IdeaDto>> getAllIdeas() {
        List<IdeaDto> ideas = ideaService.getAllIdeas();
        return ResponseEntity.ok(ideas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdeaDto> getIdeaById(@PathVariable Long id) {
        return ResponseEntity.ok(ideaService.getIdeaById(id));
    }

    @PostMapping("/creates")
    public ResponseEntity<IdeaDto> createIdea(
            @RequestBody IdeaDto ideaDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        // userDetails is automatically available after successful auth
        String userEmail = userDetails.getUsername();

        IdeaDto createdIdea = ideaService.createIdea(ideaDto, userEmail);
        return ResponseEntity.ok(createdIdea);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdeaDto> updateIdea(@PathVariable Long id, @RequestBody IdeaDto ideaDto) {
        return ResponseEntity.ok(ideaService.updateIdea(id, ideaDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteIdea(@PathVariable Long id) {
        ideaService.deleteIdea(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Idea deleted successfully");
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/vote")
    public ResponseEntity<Void> voteIdea(@PathVariable Long id, @RequestParam boolean upvote) {
        ideaService.voteIdea(id, upvote);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/collaborate")
    public ResponseEntity<Void> collaborateOnIdea(@PathVariable Long id) {
        ideaService.addCollaborator(id);
        return ResponseEntity.ok().build();
    }
}