import java.io.*;
import java.net.Socket;
import java.util.*;

public class Handler {
    public void handle(Socket clientSocket, String currentName, Map<String, Socket> clients){

        // логика обработки
        System.out.printf("Подключен клиент: %s%n", currentName);

        try(Scanner reader = getReader(clientSocket);
            PrintWriter writer = getWriter(clientSocket);
            clientSocket){
            sendResponse("Привет " + currentName, writer);
            clients.forEach((k, v) -> {
                if(!currentName.equalsIgnoreCase(k)){
                    mailing(v, currentName, "подключился к чату!");
                }
            });
            while (true) {
                String message = reader.nextLine().strip();
                System.out.printf("%s: %s%n", currentName, message);
                if (isQuitMsg(message)) {
                    sendResponse("Пока " + currentName, writer);
                    throw new NoSuchElementException();

                }
                clients.forEach((k, v) -> {
                    if(!currentName.equalsIgnoreCase(k)){
                        mailing(v, currentName, message);
                    }
                });
//                sendResponse(message, writer);
            }
        } catch (NoSuchElementException e){
            System.out.println("Клиент закрыл соединение!");
            clients.forEach((k, v) -> {
                if(!currentName.equalsIgnoreCase(k)){
                    mailing(v, currentName, "вышел из чата!");
                }
            });
            clients.remove(currentName);
        } catch (IOException ex){
            ex.printStackTrace();
            clients.forEach((k, v) -> {
                if(!currentName.equalsIgnoreCase(k)){
                    mailing(v, currentName, "вышел из чата!");
                }
            });
            clients.remove(currentName);
            System.out.printf("Клиент отключен: %s%n", currentName);
        }
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream stream = socket.getOutputStream();
        return new PrintWriter(stream);
    }

    private void mailing(Socket socket, String name, String message){
        try{
            PrintWriter writer = getWriter(socket);
            sendResponse(name + ": " + message, writer);
        } catch (IOException e) {
            System.out.println("Vse");
        }

    }

    private void newComers(Socket socket, String name, String message){
        try{
            PrintWriter writer = getWriter(socket);
            sendResponse(name + " " + message, writer);
        } catch (IOException e) {
            System.out.println("Vse");
        }
    }

    private Scanner getReader(Socket socket) throws IOException {
        InputStream stream = socket.getInputStream();
        InputStreamReader input = new InputStreamReader(stream, "UTF-8");
        return new Scanner(input);
    }

    private boolean isQuitMsg(String message){
        return "bye".equalsIgnoreCase(message);
    }

    private void sendResponse(String response, Writer writer) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }


}
