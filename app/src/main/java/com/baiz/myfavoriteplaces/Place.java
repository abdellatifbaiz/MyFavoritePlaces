package com.baiz.myfavoriteplaces;

import java.io.Serializable;
import java.sql.Date;

// la class place et pour represente les places
public class Place implements Serializable {

    private int id ;
    private String name ;
    private String adresse ;
    private String category ;
    private String description ;
    private String createAt ;
    private String image ;

    public Place(int id, String name, String adresse, String category, String description, String image) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.category = category;
        this.description = description;
        this.image = image;
    }

    public Place() { }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", adresse='" + adresse + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", createAt='" + createAt + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
