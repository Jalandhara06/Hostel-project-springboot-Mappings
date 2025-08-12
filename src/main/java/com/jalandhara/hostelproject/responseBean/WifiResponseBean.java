package com.jalandhara.hostelproject.responseBean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class WifiResponseBean {
    private UUID id;
    private String wifiName;
    private List<UUID> personIds;

}
