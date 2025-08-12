package com.jalandhara.hostelproject.requestBean;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PersonRequestBean {
    private UUID id;
    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 2, max = 40, message = "Name must be between 2 and 40 characters.")
    private String name;

    @NotNull(message = "Age is required.")
    @Min(value = 20, message = "Age must be greater than 20.")
    @Max(value = 50, message = "Age must be valid (less than 50).")
    private Integer age;

    //@NotNull(message = "Room ID must not be null.")
    @JsonProperty("room_id")
    private UUID roomId;

    //@NotNull(message = "Wifi ID must not be null.")
    @JsonProperty("wifi_id")
    private UUID wifiId;

    @JsonProperty("job_ids")
    private Set<UUID> jobIds;

}
