package ru.practicum.explorewithme.statserver.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.statdto.HitDto;
import ru.practicum.explorewithme.statdto.StatDto;
import ru.practicum.explorewithme.statserver.model.Stat;
import ru.practicum.explorewithme.statserver.model.StatWithHits;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class StatsMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Stat toStatFromHitDto(HitDto hitDto) {
        Stat stat = new Stat();
        stat.setApp(hitDto.getApp());
        stat.setUri(hitDto.getUri());
        stat.setIp(hitDto.getIp());
        stat.setCreationTime(LocalDateTime.parse(hitDto.getCreationTime(), formatter));
        return stat;
    }

    public StatDto statDtoFromStatWithHits(StatWithHits stat) {
        return StatDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .hits(stat.getHits())
                .build();
    }

    public static List<StatDto> statDtoFromStatWithHits(Collection<StatWithHits> stats) {
        return stats.stream()
                .map(StatsMapper::statDtoFromStatWithHits)
                .collect(Collectors.toList());
    }
}