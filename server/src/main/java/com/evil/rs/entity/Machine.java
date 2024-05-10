package com.evil.rs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Machine {

    private String id;

    private String os;

    private String arch;

    @JsonProperty("kernel_version")
    private String kernelVersion;

    @JsonProperty("host_name")
    private String hostName;

    @JsonProperty("mac_address")
    private String macAddress;

    private String cwd;

    private String remark;

    private Boolean flag;

    private String currentProtocol;

    private String token;

}
