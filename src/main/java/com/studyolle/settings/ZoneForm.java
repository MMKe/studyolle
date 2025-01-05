package com.studyolle.settings;

import com.studyolle.domain.Zone;
import lombok.Data;

@Data
public class ZoneForm {

    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public Zone getZone() {
        Zone zone = new Zone();
        zone.setCity(this.getCityName());
        zone.setLocalNameOfCity(this.getLocalNameOfCity());
        zone.setProvince(this.getProvinceName());
        return zone;
    }
}
