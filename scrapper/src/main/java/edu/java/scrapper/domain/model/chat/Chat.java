package edu.java.scrapper.domain.model.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "chat")
public class Chat {
    @Id
    long id;

    int state;

    public Chat(long id) {
        this.id = id;
        this.state = 0;
    }
}
