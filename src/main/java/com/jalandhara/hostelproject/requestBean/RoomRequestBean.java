package com.jalandhara.hostelproject.requestBean;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RoomRequestBean {
    private UUID id;
    @NotNull(message = "Room number is required.")
    @Min(value = 1, message = "Room number must be valid and positive")
    @JsonProperty("room_no")
    private Integer roomNo;

    @NotNull(message = "Floor number cannot be null.")
    @JsonProperty("floor_no")
    private Integer floorNo;

    @JsonProperty("tv_id")
    private UUID tvId;

    @Valid
    @JsonProperty("tv")
    private TvRequestBean tv;

}
