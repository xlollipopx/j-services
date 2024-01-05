package com.apigateway.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "person")
public class Person {

    @Id
    @Column("person_id")
    private Long personId;

    @Column("username")
    private String username;

    @Column("name")
    private String name;

    @Column("picture")
    private String picture;

}
