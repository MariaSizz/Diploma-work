package com.trade.tradeApplication.entity;

import javax.persistence.*;
@Entity
@Table(name = "ads")
public class AdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;
    private String image;
    @Column(nullable = false)
    private Integer price;
    @Column(nullable = false)
    private String title;
    @Column(length = 1024)
    private String description;

    public AdEntity(Integer pk, UserEntity author, String image, Integer price, String title, String description) {
        this.pk = pk;
        this.author = author;
        this.image = image;
        this.price = price;
        this.title = title;
        this.description = description;
    }

    public AdEntity() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

