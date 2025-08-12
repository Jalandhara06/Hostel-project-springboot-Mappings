package com.jalandhara.hostelproject.requestBean;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TvRequestBean {
    private UUID id;
    @NotBlank(message = "Tv brand is required.")
    @JsonProperty("tv_brand")
    private String tvBrand;

    @NotBlank(message = "Tv size is required.")
    @JsonProperty("tv_size")
    private String tvSize;

}
