/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.HashMap;
import javax.websocket.Session;

/**
 *
 * @author mfernandes
 */
public class CollectionsSingleton {

    private final HashMap<String, Process> processos;
    private final HashMap<String, ClienteDeSessao> sessoes;

    public Process putProcess(String token, Process process) {
        sessoes.put(token, new ClienteDeSessao(process, token));
        return processos.put(token, process);
    }

    public boolean putSession(String token, String server, Session session) {
        try {
            sessoes.get(token).addSession(server, session);
        } catch (Exception ex) {
            System.err.println("Erro ao adicionar sessao para: " + token + "." + server + ", " + ex);
            return false;
        }
        return true;
    }

    public Process getProcess(String token) {
        return processos.get(token);
    }

    private CollectionsSingleton() {
        processos = new HashMap<>();
        sessoes = new HashMap<>();
    }

    public static CollectionsSingleton getInstance() {
        return CollectionsSingletonHolder.INSTANCE;
    }

    public void removeSession(String token, String server, Session session) {
        //sessoes.get(token).remove(session);
    }

    private static class CollectionsSingletonHolder {

        private static final CollectionsSingleton INSTANCE = new CollectionsSingleton();
    }
}
