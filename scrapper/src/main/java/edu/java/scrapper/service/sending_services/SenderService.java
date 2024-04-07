package edu.java.scrapper.service.sending_services;

import edu.java.models.dto.LinkUpdate;

public interface SenderService {
    void send(LinkUpdate update);
}
