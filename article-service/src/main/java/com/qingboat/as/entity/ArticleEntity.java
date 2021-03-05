package com.qingboat.as.entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class ArticleEntity {
    // The @Document  annotation is the Spring Data annotation that
    // marks this class as defining a MongoDB document data model.
    // @Id is used to specify the _id field.

    @Id
    public String id;

    public String title;
    public String url;

}
