package fr.yeensight.tarpinfur.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * This entity is an event§. It will be used to list events, their dates and informations
 */
@Document(collection = "events")
public class EventEntity {
    @Id
    private String id;

    private String title;
    private Date date;
    private String location;

    private EventEntity() {
        // Don't use default construtor
    }

    public EventEntity(String title, Date date, String location) {
        this.title = title;
        this.date = date;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
