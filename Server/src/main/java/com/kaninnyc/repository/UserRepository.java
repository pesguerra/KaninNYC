package com.kaninnyc.repository;

import com.kaninnyc.model.AppUser;
import com.kaninnyc.model.UserRole;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AppUser> findByEmail(String email) {
        List<AppUser> users = jdbcTemplate.query(
                "select id, email, password, role, approved, created_at from `user` where email = ?",
                this::mapUser,
                email
        );
        return users.stream().findFirst();
    }

    public Optional<AppUser> findById(Integer id) {
        List<AppUser> users = jdbcTemplate.query(
                "select id, email, password, role, approved, created_at from `user` where id = ?",
                this::mapUser,
                id
        );
        return users.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from `user` where email = ?",
                Integer.class,
                email
        );
        return count != null && count > 0;
    }

    public List<AppUser> findAll() {
        return jdbcTemplate.query(
                "select id, email, password, role, approved, created_at from `user` order by created_at desc",
                this::mapUser
        );
    }

    public AppUser save(AppUser user) {
        if (user.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var statement = connection.prepareStatement(
                        "insert into `user` (email, password, role, approved) values (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getRole().name());
                statement.setBoolean(4, user.isApproved());
                return statement;
            }, keyHolder);
            user.setId(keyHolder.getKey().intValue());
            return findById(user.getId()).orElse(user);
        }

        jdbcTemplate.update(
                "update `user` set email = ?, password = ?, role = ?, approved = ? where id = ?",
                user.getEmail(),
                user.getPassword(),
                user.getRole().name(),
                user.isApproved(),
                user.getId()
        );
        return findById(user.getId()).orElse(user);
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("delete from `user` where id = ?", id);
    }

    private AppUser mapUser(ResultSet rs, int rowNum) throws SQLException {
        AppUser user = new AppUser();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setApproved(rs.getBoolean("approved"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
