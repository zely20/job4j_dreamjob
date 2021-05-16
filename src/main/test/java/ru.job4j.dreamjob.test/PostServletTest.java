package ru.job4j.dreamjob.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.servlet.PostServlet;
import ru.job4j.dreamjob.store.MemStore;
import ru.job4j.dreamjob.store.PsqlStore;
import ru.job4j.dreamjob.store.Store;
import ru.job4j.dreamjob.stub.ValidateStub;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
//класс который мы поменяем
@PrepareForTest(PsqlStore.class)
public class PostServletTest {

    @Test
    public void whenAddPostThenStoreIt() throws ServletException, IOException {
        //1. Создаем экземпляр класса
        Store validate = MemStore.instOf();
        // 2. Это я так понимаю разрешает вызывать статические методы у класса
        PowerMockito.mockStatic(PsqlStore.class);
        //3. когда создается PsqlStore меняет его на MemStore
        when(PsqlStore.instOf()).thenReturn(validate);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("id")).thenReturn("1");
        when(req.getParameter("name")).thenReturn("Petr Arsentev");
        new PostServlet().doPost(req, resp);
        assertThat(validate.findAllPosts().iterator().next().getName(), is("Petr Arsentev"));
    }

    @Test
    public void whenDoPostUpdatePost() throws ServletException, IOException {
        Store validate = MemStore.instOf();
        Post post = new Post(1, "name");
        validate.save(post);
        PowerMockito.mockStatic(PsqlStore.class);
        when(PsqlStore.instOf()).thenReturn(validate);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("id")).thenReturn(String.valueOf(post.getId()));
        when(req.getParameter("name")).thenReturn("update name");
        new PostServlet().doPost(req, resp);
        assertThat(validate.findAllPosts().iterator().next().getName(), is("update name"));
    }

    @Test
    public void whenDoGet() throws ServletException, IOException {
        Store validate = MemStore.instOf();
        Post post = new Post(1, "name");
        PostServlet postServlet = new PostServlet();
        validate.save(post);
        PowerMockito.mockStatic(PsqlStore.class);
        when(PsqlStore.instOf()).thenReturn(validate);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);
        //nullPointer
        req.getSession().setAttribute("user", "test");
        when(req.getRequestDispatcher("posts.jsp")).thenReturn(rd);
        //when(req.getSession().getAttribute("user")).thenReturn("test");
        postServlet.doGet(req,resp);
        verify(req, times(1)).getRequestDispatcher("posts.jsp");
    }
}

