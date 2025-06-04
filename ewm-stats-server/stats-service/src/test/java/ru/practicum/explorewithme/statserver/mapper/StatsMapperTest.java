package ru.practicum.explorewithme.statserver.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.statdto.HitDto;
import ru.practicum.explorewithme.statserver.model.Stat;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StatsMapperTest {

    @Test
    void toStatFromHitDto_shouldMapCorrectly() {
        HitDto hitDto = HitDto.builder()
                .app("test")
                .uri("/uri")
                .ip("127.0.0.1")
                .timestamp("2025-06-04 10:00:00")
                .build();

        Stat stat = StatsMapper.toStatFromHitDto(hitDto);

        assertThat(stat.getApp()).isEqualTo("test");
        assertThat(stat.getUri()).isEqualTo("/uri");
        assertThat(stat.getIp()).isEqualTo("127.0.0.1");
        assertThat(stat.getTimeStamp()).isEqualTo(LocalDateTime.of(2025, 6, 4, 10, 0));
    }
}
