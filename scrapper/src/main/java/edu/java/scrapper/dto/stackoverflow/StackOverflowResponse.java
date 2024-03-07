package edu.java.scrapper.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record StackOverflowResponse(

    OffsetDateTime lastModified
) {
    public StackOverflowResponse(@JsonProperty("items") List<Question> questions) {
        this(
            questions.getFirst().lastModified()
        );
    }

    public record Question(
        @JsonProperty("last_activity_date")
        OffsetDateTime lastModified
    ) {

    }
}
