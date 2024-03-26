package edu.java.scrapper.domain.model.connection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(ConnectionPK.class)
@EqualsAndHashCode
@Table(name = "chat_to_link_connection")
public class ChatToLinkConnection {
    @Id
    @Column(name = "chat_id")
    private long chatId;
    @Id
    @Column(name = "link_id")
    private long linkId;
}
