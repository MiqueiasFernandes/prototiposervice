/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import javax.websocket.Session;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author mfernandes
 */
public class ClienteDeSessao {

    private final Process process;
    private final String token;
    private MultivaluedHashMap<String, Session> sessoes;
    private Scanner reader;

    public ClienteDeSessao(Process process, String token) {
        this.process = process;
        this.token = token;
        this.sessoes = new MultivaluedHashMap();
        this.reader = new Scanner(new InputStreamReader(process.getInputStream()));

        new Thread(() -> {

            while (reader.hasNextLine() && process.isAlive()) {
                String next = getString(reader.nextLine());

                if (next == null || next.isEmpty() || next.length() < 3) {
                    continue;
                }

                int index = 0;

                String server = next.substring(1, index = next.indexOf("]"));

                if (index == -1) {
                    continue;
                }

                next = next.substring(index + 1);
                System.out.println("a enviar texto: " + next + " para " + server);

                List<Session> sessions = sessoes.get(server);
                if (sessions != null && !sessions.isEmpty()) {
                    for (Session session : sessions) {
                        if (session.isOpen()) {
                            System.out.println("enviando texto: " + next + " para " + server);
                            session.getAsyncRemote().sendText(next);
                        }
                    }
                }

            }

        }).start();

    }

    public String getString(String line) {
        System.err.println("recebido: " + line);
          byte[] decode = Base64.getDecoder().decode(line);
        String s = new String(decode, 0, decode.length);
        return s;
    }

    public boolean compareTo(ClienteDeSessao t) {
        return (token == null ? t.getToken() == null : token.equals(t.getToken()));
    }

    public Process getProcess() {
        return process;
    }

    public String getToken() {
        return token;
    }

    public MultivaluedMap<String, Session> getSessoes() {
        return sessoes;
    }

    public void addSession(String server, Session session) {
        sessoes.add(server, session);
    }

    void remove(Session session) {

    }

}
