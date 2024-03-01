package edu.java.bot.repository;

import edu.java.bot.model.User;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();

    public void addUser(Long id) {
        users.putIfAbsent(id, new User(id));
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    public boolean isAuthenticated(Long id) {
        return users.containsKey(id);
    }
}
