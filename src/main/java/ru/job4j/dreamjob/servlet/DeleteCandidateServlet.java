package ru.job4j.dreamjob.servlet;

import ru.job4j.dreamjob.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DeleteCandidateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.valueOf(req.getParameter("id"));
        File file = new File("d:\\images\\"  + req.getParameter("id"));
        if (file.exists()) {
            file.delete();
        }
        PsqlStore.instOf().deleteCandidate(id);
        req.getRequestDispatcher("/candidates.do").forward(req, resp);
    }
}
