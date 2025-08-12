package com.jalandhara.hostelproject.mapper;

import com.jalandhara.hostelproject.entity.Tv;
import com.jalandhara.hostelproject.requestBean.TvRequestBean;
import com.jalandhara.hostelproject.responseBean.TvResponseBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TvMapper {

    // RequestBean -> Entity
    public Tv toEntity(TvRequestBean bean) {
        return Optional.ofNullable(bean)
                .map(t-> Tv.builder()
                        .tvBrand(t.getTvBrand())
                        .tvSize(t.getTvSize())
                        .build())
                .orElse(null);

    }

    // Entity -> ResponseBean
    public TvResponseBean toResponse(Tv tv) {

        return Optional.ofNullable(tv)
                .map(t -> TvResponseBean.builder()
                        .id(t.getId())
                        .tvBrand(t.getTvBrand())
                        .tvSize(t.getTvSize())
                        .build())
                .orElse(null);

    }

}