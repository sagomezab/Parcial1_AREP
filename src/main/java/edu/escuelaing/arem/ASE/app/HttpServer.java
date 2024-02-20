package edu.escuelaing.arem.ASE.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HttpServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine = "";
            String path = "";
            boolean firstline = true;
            while ((inputLine = in.readLine()) != null) {
                if(firstline){
                    firstline = false;
                    path = inputLine.split(" ")[1];
                }
                if (!in.ready()) {break; }
            }
            System.out.println("Path: " + path);
            ArrayList<Float> datos = new ArrayList<>();
            if(path.startsWith("/calculadora")){
                outputLine = getDeafult();
            }else if(path.split("\\?").length > 1){
                String[] param = path.split("\\?");
                try{
                    String comando = param[1].split("=")[1];
                    String funcion = comando.split("\\(")[0];
                    String[] valores = comando.split("\\(")[1].split(",");
                    if(valores.length > 2){
                        for(int i = 0; i < valores.length; i++){
                            if(i + 1 == valores.length){
                                datos.add(Float.valueOf(valores[i].split("\\)")[0]));
                            }else{
                                datos.add(Float.valueOf(valores[i]));
                            }
                        }
                        for (Float dato : datos){
                            System.out.println("dato: " + dato);
                        }
                    }else{
                        datos.add(Float.valueOf(valores[0].split("\\)")[0]));
                        System.out.println("dato único: " + datos.get(0));
                    }
                    System.out.println("función: " + funcion);
                    outputLine = getDeafult();
                }catch (Exception e){

                }

            }else{
                outputLine = "";
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public static String getDeafult(){
        return "HTTP/1.1 200 OK"
                + "Content-Type: text/html\r\n"
                + "\r\n" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>AREP</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Calculadora Reflexiva</h1>\n" +
                "<form id=\"movieForm\">\n" +
                "    <label for=\"valor\">Ingrese una funcion:</label>\n" +
                "    <input type=\"text\" id=\"valor\" required>\n" +
                "    <button type=\"button\" onclick=\"\">Submit</button>\n" +
                "</form>\n" +
                "<div id=\"getrespmsg\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "        function loadGetMsg() {\n" +
                "            let nameVar = document.getElementById(\"name\").value;\n" +
                "            const xhttp = new XMLHttpRequest();\n" +
                "            xhttp.onload = function() {\n" +
                "                document.getElementById(\"valor\").innerHTML =\n" +
                "                this.responseText;\n" +
                "            }\n" +
                "            xhttp.open(\"GET\", \"/comando?comando=\"+encodeURIComponent(valor));\n" +
                "            xhttp.send();\n" +
                "        }\n" +
                "    </script>" +
                "</body>\n" +
                "</html>\n";
    }
}
