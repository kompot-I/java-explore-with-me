package ru.practicum.explorewithme.main.event.comment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.main.event.model.Event;
import ru.practicum.explorewithme.main.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_message")
    private String userMessage;

    @Column(name = "admin_message")
    private String adminMessage;

    @Column(name = "accepted")
    private Boolean accepted;
}
