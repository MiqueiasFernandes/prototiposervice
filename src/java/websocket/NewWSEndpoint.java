/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import main.CollectionsSingleton;

/**
 *
 * @author mfernandes
 */
@ServerEndpoint("/rlive/{token}/{server}")
public class NewWSEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        String token = session.getPathParameters().get("token");
        String server = session.getPathParameters().get("server");
        if (CollectionsSingleton.getInstance().putSession(token, server, session)) {
            System.out.println("aberto sessao de " + token + " para " + server);
        } else {
            System.err.println("houve falha ao adicionar sessao para: " + token + "." + server);
        }
    }

    @OnClose
    public void onClose(Session session) {
        String token = session.getPathParameters().get("token");
        String server = session.getPathParameters().get("server");
        CollectionsSingleton.getInstance().removeSession(token, server, session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        String token = session.getPathParameters().get("token");
        String server = session.getPathParameters().get("server");

        System.out.println("mensagem de " + token + " para " + server);

        message = "[" + server + "]" + message;

        System.out.println("enviando a process: " + message);

        try {
            OutputStream outputStream = CollectionsSingleton.getInstance()
                    .getProcess(token)
                    .getOutputStream();
            outputStream.write(
                   (Base64.getEncoder().encodeToString(message.getBytes()) + System.lineSeparator()).getBytes());
          // message.getBytes());
                    outputStream.flush();
        } catch (IOException ex) {
            System.err.println("erro ao enviar a processo: " + ex);
        }
    }

}
