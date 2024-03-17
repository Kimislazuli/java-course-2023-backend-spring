package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.domain.model.chat.Chat;
import edu.java.scrapper.domain.model.chat.ChatRowMapper;
import edu.java.scrapper.exception.NotExistException;
import edu.java.scrapper.exception.RepeatedRegistrationException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatDao {
    private final JdbcClient client;
    private final ChatRowMapper mapper;

    // Пока метод добавления возвращает по факту свой же параметр (чтобы формировать с
    // DAO для ссылок единый контракт),
    // Я не знаю, как лучше сделать. Было бы логично отдавать айди, но у меня это
    // не генерируемое суррогатное значение, а то же, которое я передаю, так что
    // я заранее его знаю. Не нашла в интернете контракта для таких методов
    // + так и не поняла, какой эксепшн ловить при повторной попытке выполнить операцию.
    // нужно ли это вообще делать или на уровне запроса решать проблему?

    public long add(long chatId) throws RepeatedRegistrationException {
        try {
            String query = "INSERT INTO chat (id) VALUES (?)";

            int rowsAffected = client.sql(query).param(chatId).update();

            return rowsAffected == 1 ? chatId : -1;
        } catch (Exception e) {
            throw new RepeatedRegistrationException("This chat already exists");
        }
    }

    public void remove(long chatId) throws NotExistException {
        String query = "DELETE FROM chat WHERE id = ?";

        int rowsAffected = client.sql(query).param(chatId).update();

        if (rowsAffected == 0) {
            throw new NotExistException("This chat doesn't exist.");
        }
    }

    public List<Chat> findAll() {
        String query = "SELECT * FROM chat";

        return client.sql(query).query(mapper).list();
    }

    public Optional<Chat> getById(long id) {
        String query = "SELECT FROM chat WHERE id = ?";

        return client.sql(query).param(id).query(mapper).optional();
    }
}
