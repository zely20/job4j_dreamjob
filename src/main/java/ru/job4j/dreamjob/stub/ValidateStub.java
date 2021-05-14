package ru.job4j.dreamjob.stub;

import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.store.Store;

import java.util.*;

public class ValidateStub implements Store {
    private final Map<Integer, Post> store = new HashMap<>();
    private int ids = 0;

    @Override
    public Collection<Post> findAllPosts() {
        return new ArrayList<Post>(this.store.values());
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        return null;
    }

    @Override
    public void save(Post post) {
        post.setId(this.ids++);
        this.store.put(post.getId(), post);
    }

    @Override
    public void save(Candidate candidate) {

    }

    @Override
    public Post findByIdPost(int id) {
        return null;
    }

    @Override
    public Candidate findByIdCandidate(int id) {
        return null;
    }

    @Override
    public Integer deleteCandidate(int id) {
        return null;
    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public User findByEmail(String email) {
        return null;
    }
}
