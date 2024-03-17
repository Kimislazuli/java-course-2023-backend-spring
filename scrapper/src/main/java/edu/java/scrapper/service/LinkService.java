package edu.java.scrapper.service;

import edu.java.scrapper.domain.model.link.Link;
import edu.java.scrapper.exception.AlreadyExistException;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import java.net.URI;
import java.util.Collection;

public interface LinkService {
    Link add(long tgChatId, URI url) throws AlreadyExistException, RepeatedRegistrationException;

    Link remove(long tgChatId, URI url) throws NotExistException;

    Collection<Link> listAll(long tgChatId);
}
