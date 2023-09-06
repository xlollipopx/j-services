package com.authservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "person", schema = "public")
public class Person {

    @Id
    @GeneratedValue
    @Column(name = "person_id", nullable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @ManyToMany
    @JoinTable(name = "person_role_map",
    joinColumns = @JoinColumn(name = "person_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

}
