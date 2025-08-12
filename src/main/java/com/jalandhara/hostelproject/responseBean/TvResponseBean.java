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
public class TvResponseBean {
    private UUID id;
    private String tvBrand;
    private String tvSize;
}
