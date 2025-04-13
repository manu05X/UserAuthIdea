package com.ideacollab.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*

@Entity
@Table(name = "collaborations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "idea_id"})
})
@Data
@NoArgsConstructor
public class Collaboration extends BaseModel{


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false)
    private Idea idea;

    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Idea getIdea() {
        return idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
*/



@Entity
@Table(name = "collaborations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "idea_id"})
})
@Data
@NoArgsConstructor
public class Collaboration extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false)
    private Idea idea;

    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Idea getIdea() {
        return idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}