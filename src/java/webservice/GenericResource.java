/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Base64;
import java.util.Scanner;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import main.CollectionsSingleton;

/**
 * REST Web Service
 *
 * @author mfernandes
 */
@Path("generic")
public class GenericResource {

    static String diretorio = "/home/mfernandes/testR/", 
            dir = "/var/www/html/tmp/";
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * Retrieves representation of an instance of webservice.GenericResource
     *
     * @param response
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@Context HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Origin", "http://192.168.1.10:8080");
        response.setHeader("Access-Control-Allow-Origin", "http://*");
        response.setHeader("Access-Control-Allow-Origin", "http://*:*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
        String token = context.getQueryParameters().getFirst("token");

        
        if (context.getQueryParameters().containsKey("file")) {
            String file = context.getQueryParameters().getFirst("file");
            System.out.println("file requisitado: " + file);
            if (!file.contains("rbokeh")) {
                return "{\"error ao tentar ler arquivo\":\"tipo de arquivo invalido, inclua rbokeh\"}";
            }

            try {
//                Scanner sc = new Scanner(new File("/home/mfernandes/testR/" + file));
//                boolean ler = false;
//                StringBuilder sb = new StringBuilder();
//                while (sc.hasNextLine()) {
//                    String line = sc.next();
//                    if (line.contains("<body>")) {
//                        ler = true;
//                        continue;
//                    }
//                    if (ler) {
//                        sb.append(line);
//                    }
//                    if (line.equals("</script>")) {
//                        break;
//                    }
//                }
//
//                FileWriter fw = new FileWriter(dir + file);
//                fw.write(sb.toString());
//                fw.close();
                
                Files.copy(new File("/home/mfernandes/testR/" + file).toPath(), new File(dir + file).toPath(), REPLACE_EXISTING);

                return "{\"file\":\"http://" + context.getBaseUri().getHost() + ":9000/tmp/" + file +"\"}";
            } catch (Exception ex) {
                return "{\"error ao tentar ler arquivo\":\"" + ex + "\"}";
            }

        }

        System.out.println("iniciando handlers para: " + token);

        String json = null;
        try {
            Process exec = Runtime.getRuntime().exec(
                    "java -jar /home/mfernandes/NetBeansProjects/JRIaccess/store/jriaccess.jar",
                     null, new File(diretorio));

            json = new Scanner(exec.getInputStream()).nextLine();

            CollectionsSingleton.getInstance().putProcess(token, exec);

        } catch (IOException ex) {
            return "{\"error\":\"" + ex.toString().replace("\"", "'").replace(":", ";") + "\"}";
        }

        if (json == null) {
            return "{\"error\":\"json is null, process fail. token " + token + "\"}";
        }
        return json;

    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String postJson(String script, @Context HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Origin", "http://192.168.1.10:8080");
        response.setHeader("Access-Control-Allow-Origin", "http://*");
        response.setHeader("Access-Control-Allow-Origin", "http://*:*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");

        Script scr;
        try {
            scr = new Script(script);
        } catch (IOException | InterruptedException ex) {
            return Base64.getEncoder().encodeToString(ex.toString().getBytes());
        }

        System.out.println("recebido post: " + scr);

        return Base64.getEncoder().encodeToString(scr.getResultado().getBytes());
    }
}
