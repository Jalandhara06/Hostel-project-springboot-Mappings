package com.jalandhara.hostelproject.responseBean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RoomResponseBean {
    private UUID id;
    private Integer roomNo;
    private Integer floorNo;
    private UUID tvId;
    private TvResponseBean tv;
}
