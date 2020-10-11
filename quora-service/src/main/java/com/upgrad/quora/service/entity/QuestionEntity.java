package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "question", schema = "public")
@NamedQueries(
        {
                @NamedQuery(name = "questionsByUser", query = "select q from QuestionEntity q where q.userId = :userId"),

        }
)
public class QuestionEntity implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "CONTENT")
    @Size(max = 500)
    private String content;

    @Column(name = "DATE")
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity userId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }


    @Column(name = "user_id")
    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionEntity that = (QuestionEntity) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, content, date, userId);
    }
}
