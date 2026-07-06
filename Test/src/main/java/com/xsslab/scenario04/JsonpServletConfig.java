package com.xsslab.scenario04;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class JsonpServletConfig {

    @Bean
    public ServletRegistrationBean<HttpServlet> jsonpServlet() {
        ServletRegistrationBean<HttpServlet> bean = new ServletRegistrationBean<>(
                new HttpServlet() {
                    @Override
                    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                            throws IOException {
                        String callback = request.getParameter("callback");
                        if (callback == null || callback.isEmpty()) {
                            callback = "callback";
                        }
                        String jsonData = "[{\"id\":1,\"name\":\"张三\"},{\"id\":2,\"name\":\"李四\"}]";
                        response.setContentType("application/javascript;charset=UTF-8");
                        PrintWriter out = response.getWriter();
                        out.print(callback);
                        out.print("(");
                        out.print(jsonData);
                        out.print(");");
                    }
                },
                "/scenario04/api/jsonp"
        );
        return bean;
    }
}
