package edu.java.scrapper.domain.model.connection;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionPK implements Serializable {
    private long chatId;

    private long linkId;
}
