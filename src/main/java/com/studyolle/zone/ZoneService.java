package com.studyolle.zone;

import com.studyolle.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository zoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        Resource resource = new ClassPathResource("zone_of_kr.csv");
        List<Zone> zones = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                .map(line -> {
                    String[] split = line.split(",");
                    Zone zone = new Zone();
                    zone.setCity(split[0]);
                    zone.setLocalNameOfCity(split[1]);
                    zone.setProvince(split[2]);
                    return zone;
                })
                .collect(Collectors.toList());

        zoneRepository.saveAll(zones);
    }
}
