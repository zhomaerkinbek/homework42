import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {

    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private Map<String, Socket> clients = new HashMap<>();

    private List<String> names = List.of("Red", "Blue", "Black", "Pink", "Gray", "Write", "Yellow", "Green", "Brown", "Gold", "Violet", "Cyan", "Magento", "Purple", "Beige", "Lightblue", "Silver", "Bronze", "Vinous");

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (var server = new ServerSocket(port)) {
            // обработка подключения
            System.out.println("Сервер запущен!");
//            try(var clientSocket = server.accept()) {
//                handle(clientSocket);
//            }
            while(!server.isClosed()){
                var currentName = newName(names.get(new Random().nextInt(names.size())));
                Socket clientSocket = server.accept();
                clients.put(currentName, clientSocket);
                pool.submit(() -> new Handler().handle(clientSocket, currentName, clients));
            }
        } catch (IOException e) {
            System.out.printf("Вероятнее всего порт %s занят.%n", port);
            e.printStackTrace();
        }
    }

    private String newName(String name){
        if(clients.containsKey(name)){
            return newName(names.get(new Random().nextInt(names.size())));
        }
        return name;
    }
}
