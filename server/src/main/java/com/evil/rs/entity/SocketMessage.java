package com.evil.rs.entity;

import lombok.Data;

@Data
public class SocketMessage {

    private String route;

    private String data;

    private String token;

}
