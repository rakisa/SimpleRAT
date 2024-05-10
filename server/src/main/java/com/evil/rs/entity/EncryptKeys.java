package com.evil.rs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EncryptKeys {

    private String ip;

    private String aesKey;

}
