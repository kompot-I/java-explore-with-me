package ru.practicum.explorewithme.main.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.explorewithme.main.request.enums.RequestState;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long event;

    @Column(name = "requester_id")
    private Long requester;

    @Enumerated(EnumType.STRING)
    private RequestState status;

    private LocalDateTime created;
}
