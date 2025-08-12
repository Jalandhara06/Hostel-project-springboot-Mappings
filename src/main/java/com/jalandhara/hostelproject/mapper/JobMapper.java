package com.jalandhara.hostelproject.mapper;

import com.jalandhara.hostelproject.entity.Job;
import com.jalandhara.hostelproject.requestBean.JobRequestBean;
import com.jalandhara.hostelproject.responseBean.JobResponseBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JobMapper {

    // Entity -> ResponseBean
    public JobResponseBean toResponse(Job job) {

        return Optional.ofNullable(job)
                .map(d -> JobResponseBean.builder()
                        .id(d.getId())
                        .jobName(d.getJobName())
                        .jobCompany(d.getJobCompany())
                        .build())
                .orElse(null);

    }

    // RequestBean -> Entity
    public Job toEntity(JobRequestBean bean) {
        return Optional.ofNullable(bean)
                .map(d -> Job.builder()
                        .jobName(d.getJobName())
                        .jobCompany(d.getJobCompany())
                        .build())
                .orElse(null);
    }

}

