package ru.job4j.dreamjob.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.dreamjob.servlet.PostServlet;
import ru.job4j.dreamjob.store.MemStore;
import ru.job4j.dreamjob.store.PsqlStore;
import ru.job4j.dreamjob.store.Store;
import ru.job4j.dreamjob.stub.ValidateStub;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
//класс который мы поменяем
@PrepareForTest(PsqlStore.class)
public class PostServletTest {

    @Test
    public void whenAddPostThenStoreIt() throws ServletException, IOException {
        //1. Создаем экземпляр класса
        Store validate = new MemStore();
        // 2. Это я так понимаю разрешает вызывать статические методы у класса
        PowerMockito.mockStatic(PsqlStore.class);
        //3. когда создается PsqlStore меняет его на MemStore
        when(PsqlStore.instOf()).thenReturn(validate);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("name")).thenReturn("Petr Arsentev");
        new PostServlet().doPost(req, resp);
        assertThat(validate.findAllPosts().iterator().next().getName(), is("Petr Arsentev"));
    }
}
