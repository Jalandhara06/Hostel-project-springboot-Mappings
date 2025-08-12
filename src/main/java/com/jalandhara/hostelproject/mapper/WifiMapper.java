package com.jalandhara.hostelproject.mapper;

import com.jalandhara.hostelproject.entity.Person;
import com.jalandhara.hostelproject.entity.Wifi;
import com.jalandhara.hostelproject.requestBean.WifiRequestBean;
import com.jalandhara.hostelproject.responseBean.WifiResponseBean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WifiMapper {

    // RequestBean -> Entity
    public Wifi toEntity(WifiRequestBean bean) {
        return Wifi.builder()
                .wifiName(bean.getWifiName())
                .wifiPassword(bean.getWifiPassword())
                .build();
    }

    // Entity -> ResponseBean
    public WifiResponseBean toResponse(Wifi wifi) {

        return Optional.ofNullable(wifi)
                .map(w-> WifiResponseBean.builder()
                        .id(w.getId())
                        .wifiName(w.getWifiName())
                        .personIds(w.getPersons() != null ? w.getPersons().stream().map(Person :: getId).collect(Collectors.toList()) : null)
                        .build())
                .orElse(null);

    }

}