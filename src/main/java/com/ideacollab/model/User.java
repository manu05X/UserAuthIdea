package com.ideacollab.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseModel {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String employeeId;

    @Column(nullable = false)
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    // Relationships
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Idea> ideas = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Collaboration> collaborations = new ArrayList<>();

    // Constructors
    public User() {
    }

    public User(String email, String name, String employeeId, String hashedPassword, Role role) {
        this.email = email;
        this.name = name;
        this.employeeId = employeeId;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    // Helper methods
    public void addIdea(Idea idea) {
        ideas.add(idea);
        idea.setCreator(this);
    }

    public void removeIdea(Idea idea) {
        ideas.remove(idea);
        idea.setCreator(null);
    }

    public void addVote(Vote vote) {
        votes.add(vote);
        vote.setUser(this);
    }

    public void removeVote(Vote vote) {
        votes.remove(vote);
        vote.setUser(null);
    }

    public void addCollaboration(Collaboration collaboration) {
        collaborations.add(collaboration);
        collaboration.setUser(this);
    }

    public void removeCollaboration(Collaboration collaboration) {
        collaborations.remove(collaboration);
        collaboration.setUser(null);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public List<Idea> getIdeas() {
        return ideas;
    }

    public void setIdeas(List<Idea> ideas) {
        this.ideas = ideas;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public List<Collaboration> getCollaborations() {
        return collaborations;
    }

    public void setCollaborations(List<Collaboration> collaborations) {
        this.collaborations = collaborations;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Add this method to convert roles to authorities
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }
}