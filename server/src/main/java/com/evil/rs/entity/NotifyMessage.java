package com.evil.rs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotifyMessage {

    private String type;

    private String data;

}
