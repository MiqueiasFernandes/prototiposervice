/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author mfernandes
 */
class AddParamsToHeader extends HttpServletResponseWrapper {

    public AddParamsToHeader(HttpServletResponse response) {
        super(response);
        super.addHeader("Access-Control-Allow-Origin", "*");
        super.addHeader("Access-Control-Allow-Credentials", "true");
        super.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        super.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
    }

}

public class NewFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
       chain.doFilter(request, new AddParamsToHeader((HttpServletResponse) response));
    }

    @Override
    public void destroy() {

    }

}
