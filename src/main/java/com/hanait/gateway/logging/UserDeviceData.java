package com.hanait.gateway.logging;

import lombok.Data;

@Data
public class UserDeviceData {
    private String appVersion;
    private String deviceName;
    private String deviceOs;
    private String deviceOsVersion;
}
