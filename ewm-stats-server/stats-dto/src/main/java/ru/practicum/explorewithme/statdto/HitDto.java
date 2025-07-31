package ru.practicum.explorewithme.statdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HitDto {
    private String app;
    private String uri;
    private String ip;
    @JsonProperty("timestamp")
    private String creationTime;
}