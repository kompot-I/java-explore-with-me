package ru.practicum.explorewithme.statserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.practicum.explorewithme.statdto.HitDto;
import ru.practicum.explorewithme.statdto.StatDto;
import ru.practicum.explorewithme.statserver.dal.StatsRepository;
import ru.practicum.explorewithme.statserver.model.Stat;
import ru.practicum.explorewithme.statserver.model.StatWithHits;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StatsServiceTest {

    private StatsRepository statsRepository;
    private StatsService statsService;

    @BeforeEach
    void setUp() {
        statsRepository = mock(StatsRepository.class);
        statsService = new StatsService(statsRepository);
    }

    @Test
    void saveHit_shouldCallRepositorySave() {
        HitDto hitDto = HitDto.builder()
                .app("test-app")
                .uri("/test")
                .ip("198.0.0.1")
                .creationTime("2025-06-03 00:00:00")
                .build();

        statsService.saveHit(hitDto);

        ArgumentCaptor<Stat> statCaptor = ArgumentCaptor.forClass(Stat.class);
        verify(statsRepository, times(1)).save(statCaptor.capture());

        Stat savedStat = statCaptor.getValue();
        assertThat(savedStat.getApp()).isEqualTo("test-app");
        assertThat(savedStat.getUri()).isEqualTo("/test");
        assertThat(savedStat.getIp()).isEqualTo("198.0.0.1");
        assertThat(savedStat.getCreationTime()).isEqualTo(LocalDateTime.of(2025, 6, 3, 0, 0, 0));
    }

    @Disabled
    @Test
    void getStats_shouldReturnStatsFromRepository() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/test");

        StatWithHits stat = new StatWithHits() {
            public String getApp() {
                return "test-app";
            }

            public String getUri() {
                return "/test";
            }

            public Integer getHits() {
                return 99;
            }
        };

        when(statsRepository.findByParams(start, end, uris)).thenReturn(List.of(stat));

        List<StatDto> result = (List<StatDto>) statsService.getStats(start, end, uris, false);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getApp()).isEqualTo("test-app");
        assertThat(result.getFirst().getUri()).isEqualTo("/test");
        assertThat(result.getFirst().getHits()).isEqualTo(99);
    }
}