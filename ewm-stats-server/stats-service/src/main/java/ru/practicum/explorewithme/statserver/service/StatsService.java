package ru.practicum.explorewithme.statserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.statdto.HitDto;
import ru.practicum.explorewithme.statdto.StatDto;
import ru.practicum.explorewithme.statserver.dal.StatsRepository;
import ru.practicum.explorewithme.statserver.exception.BadRequestException;
import ru.practicum.explorewithme.statserver.mapper.StatsMapper;
import ru.practicum.explorewithme.statserver.model.StatWithHits;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    @Transactional
    public void saveHit(HitDto hitDto) {
        statsRepository.save(StatsMapper.toStatFromHitDto(hitDto));
    }

    public Collection<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) throws BadRequestException {
        if (start == null) throw new BadRequestException("Start date in the filters cannot be empty");
        if (end == null) throw new BadRequestException("End date cannot be empty in the filters");
        if (end.isBefore(start)) throw new BadRequestException("Filters use the wrong date range.");

        Collection<StatWithHits> stats = unique ? statsRepository.findByParamsAndUniqueByIp(start, end, null) : statsRepository.findByParams(start, end, null); // test
        return StatsMapper.statDtoFromStatWithHits(stats);
    }
}