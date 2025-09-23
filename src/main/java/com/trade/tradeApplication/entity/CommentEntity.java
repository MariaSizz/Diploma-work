package com.trade.tradeApplication.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;
    @Column(length = 1024, nullable = false)
    private String text;
    @Column(nullable = false)
    private Long createdAt;

    public CommentEntity() {
    }

    public CommentEntity(Integer pk, UserEntity author, String text, Long createdAt) {
        this.pk = pk;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}

