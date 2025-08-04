package ru.practicum.explorewithme.main.event.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentUserRequest {
    private Long id;

    @Size(min = 5, max = 2000, message = "Message must be between 5 and 2000 characters")
    @NotBlank(message = "Message must not be blank")
    private String message;
}
