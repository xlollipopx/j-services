package com.apigateway.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonInfo {
    private Long personId;
    private String username;
    private List<String> authorities;

}
