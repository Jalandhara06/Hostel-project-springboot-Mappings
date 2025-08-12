package com.jalandhara.hostelproject.responseBean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class HostelResponseBean {

    private List<PersonResponseBean> persons;
    private RoomResponseBean room;
    private WifiResponseBean wifi;
    private TvResponseBean tv;
    private List<JobResponseBean> jobs;
    private PersonResponseBean person;

}
