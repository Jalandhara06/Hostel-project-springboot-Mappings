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
public class JobRequestBean {
    private UUID id;
    @NotBlank(message = "Job name is required.")
    @JsonProperty("job_name")
    private String jobName;

    @NotBlank(message = "Company name is required.")
    @JsonProperty("job_company")
    private String jobCompany;

}
