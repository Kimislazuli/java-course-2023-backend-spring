package edu.java.bot.repository;

import edu.java.bot.model.User;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap();

    public void addUser(Long id) {
        if (!users.containsKey(id)) {
            users.put(id, new User(id));
        }
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    public boolean isAuthenticated(Long id) {
        return users.containsKey(id);
    }
}
