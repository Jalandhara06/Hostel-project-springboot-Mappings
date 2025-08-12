package com.jalandhara.hostelproject.requestBean;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class HostelRequestBean {

    @Valid
    private List<PersonRequestBean> persons;
    @Valid
    private RoomRequestBean room;
    @Valid
    private WifiRequestBean wifi;
    @Valid
    private TvRequestBean tv;
    @Valid
    private List<JobRequestBean> jobs;
    @Valid
    private PersonRequestBean person;

}
