package com.example.back.dto.user;

import lombok.Data;

@Data
public class UserSummaryDTO {

    private Long id;
    private String username;

    public UserSummaryDTO() {
    }

    public UserSummaryDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

}
