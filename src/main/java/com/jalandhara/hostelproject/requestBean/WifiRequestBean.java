package com.jalandhara.hostelproject.requestBean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class WifiRequestBean {
    private UUID id;
    @NotBlank(message = "Wifi name is required.")
    @JsonProperty("wifi_name")
    private String wifiName;

    @NotBlank(message = "Wifi Password is required to connect.")
    @JsonProperty("wifi_password")
    private String wifiPassword;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("person_ids")
    private List<UUID> personIds;

}
