package com.jalandhara.hostelproject.mapper;

import com.jalandhara.hostelproject.entity.Room;
import com.jalandhara.hostelproject.entity.Tv;
import com.jalandhara.hostelproject.requestBean.RoomRequestBean;
import com.jalandhara.hostelproject.responseBean.RoomResponseBean;
import com.jalandhara.hostelproject.responseBean.TvResponseBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoomMapper {

    // RequestBean -> Entity
    public Room  toEntity(RoomRequestBean bean, Tv tv) {
        return Room.builder()
                .roomNo(bean.getRoomNo())
                .floorNo(bean.getFloorNo())
                .tv(tv)
                .build();
    }

    // Entity -> ResponseBean
    public RoomResponseBean toResponse(Room room) {

        return Optional.ofNullable(room)
                .map(r-> RoomResponseBean.builder()
                        .id(r.getId())
                        .roomNo(r.getRoomNo())
                        .floorNo(r.getFloorNo())
                        .tvId(r.getTv() != null ? r.getTv().getId() : null)
                        .tv(r.getTv() != null ? TvResponseBean.builder()
                                .tvBrand(r.getTv().getTvBrand())
                                .tvSize(r.getTv().getTvSize())
                                .build() : null)
                        .build())
                .orElse(null);
        
    }

}
