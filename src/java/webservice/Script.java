/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mfernandes
 */
@XmlRootElement
public class Script {

    static String diretorio = "/home/mfernandes/testR/";

    @XmlElement
    public String token;
    @XmlElement
    public String script;

    private final Process process;
    StringBuilder res;

    public Script(String msg) throws IOException, InterruptedException {
        this.token = msg.split(",")[0].substring(1).split(":")[1].replace("\"", "");
        this.script = msg.split(",")[1];
        this.script = new String(Base64.getDecoder().decode(
                this.script.substring(10, this.script.length() - 2)));
        File file = new File(diretorio + token + ".R");
        file.createNewFile();
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(script);
            fileWriter.close();
        }

        process = Runtime.getRuntime().exec("R CMD BATCH " + token + ".R", null, new File(diretorio));

        while (process.isAlive()) {
            Thread.sleep(2000);
        }

        Scanner scanner = new Scanner(new File(diretorio + token + ".Rout"));
        res = new StringBuilder();
        while (scanner.hasNextLine()) {
            res.append(scanner.nextLine());
        }

        System.out.println("rs: " + res.toString());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getResultado() {
        String tmp = Base64.getEncoder().encodeToString(res.toString().getBytes());

        String files = "";

        for (String file : new File(diretorio).list()) {
            if (files.length() > 1) {
                files += "/";
            }
            files += file;
        }
        return "{\"files\":\"" + files + "\",\"resultado\":\"" + tmp + "\"}";
    }

    @Override
    public String toString() {
        return "Script{" + "token=" + token + ", script=" + script + '}';
    }

}
