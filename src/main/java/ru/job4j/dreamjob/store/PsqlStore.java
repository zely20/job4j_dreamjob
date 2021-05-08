package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Post;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private final BasicDataSource pool = new BasicDataSource();
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private static final String DELETE_CANDIDATE = "DELETE FROM candidate WHERE id=(?)";
    private static final String FIND_ALL_POSTS = "SELECT * FROM post";
    private static final String FIND_ALL_CANDIDATES = "SELECT * FROM candidate";
    private static final String CREATE_CANDIDATES = "INSERT INTO candidate(name) VALUES (?)";
    private static final String CREATE_POST = "INSERT INTO post(name) VALUES (?)";
    private static final String UPDATE_POST = "UPDATE post SET name = (?) WHERE id = (?)";
    private static final String UPDATE_CANDIDATES = "UPDATE candidate SET name = (?) WHERE id = (?)";
    private static final String FIND_BY_ID_POST = "SELECT * FROM post WHERE id=(?)";
    private static final String FIND_BY_ID_CANDIDATES = "SELECT * FROM candidate WHERE id=(?)";

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(FIND_ALL_POSTS)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            LOG.error("find All post error", e);
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_ALL_CANDIDATES)
        ){
            try (ResultSet it = ps.executeQuery()){
                while (it.next()) {
                    candidates.add(new Candidate(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (SQLException e) {
            LOG.error("find All candidate error", e);
        }
        return candidates;
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    @Override
    public void save(Candidate candidate) {
        if(candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    private Post create(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(CREATE_POST, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("create post error", e);
        }
        return post;
    }

    private Candidate create(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(CREATE_CANDIDATES, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("create candidate error", e);
        }
        return candidate;
    }

    private void update(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_POST, PreparedStatement.RETURN_GENERATED_KEYS)
        ){
            ps.setString(1, post.getName());
            ps.setInt(2, post.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.error("update post error", e);
        }
    }

    private void update(Candidate candidate) {
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement(UPDATE_CANDIDATES, PreparedStatement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.error("update candidate error", e);
        }
    }

    @Override
    public Post findByIdPost(int id) {
        Post result = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(FIND_BY_ID_POST)
        ) {
            ps.setInt(1, id);
            ps.execute();
            try (ResultSet post = ps.getResultSet()) {
                if (post.next()) {
                    result = new Post(post.getInt(1), post.getString(2));

                }
            }
        } catch (Exception e) {
            LOG.error("find by id post error", e);
        }
        return result;
    }

    @Override
    public Candidate findByIdCandidate(int id) {
        Candidate result = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(FIND_BY_ID_CANDIDATES)
        ) {
            ps.setInt(1, id);
            ps.execute();
            try (ResultSet post = ps.getResultSet()) {
                if (post.next()) {
                    result = new Candidate(post.getInt(1), post.getString(2));

                }
            }
        } catch (Exception e) {
            LOG.error("find by id candidate", e);
        }
        return result;
    }

    @Override
    public Integer deleteCandidate(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(DELETE_CANDIDATE)
        ) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            LOG.error("Don't delete candidate with id= " + id, e);
        }
        return id;
    }
}
