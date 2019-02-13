/**
 *
 * TCPServer.java - Server code
 *
 */

package com.utd.distributed.root;

public class Root {

    /*private ServerSocket serverSocket;
    private String directoryPath;
    private static Map ClientList;
    private int noOfClients;
    private final String ServerID;

    public TCPServer(Properties properties, String serverID) throws IOException {
        this.serverSocket = new ServerSocket(Integer.valueOf(properties.getProperty("serverport")));
        this.directoryPath = properties.getProperty("directoryserver" + serverID);
        ClientList = new LinkedHashMap();
        this.noOfClients = 0;
        this.ServerID = serverID;
    }

    *//**
     * Start the server to start listening from clients
     *//*
    public void startServer()   {

        System.out.println("Starting Server " + ServerID);
        System.out.println("-------------------------------------------------------");
        System.out.println("Waiting for client on port " +
                serverSocket.getLocalPort() + "...");
        System.out.println();
        ExecutorService pool = Executors.newFixedThreadPool(3);
        while(true) {

            try {
                Socket server = serverSocket.accept();
                String clientIP = server.getInetAddress().toString().substring(1);
                DataInputStream in = new DataInputStream(server.getInputStream());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());

                String msg = in.readUTF();
                System.out.println("message received: " + msg + " from " + clientIP);

                int clientID = Integer.parseInt(msg.substring(msg.indexOf("Client ID: ") + 11, msg.indexOf("Client ID: ") + 12).trim());

                if (!ClientList.containsKey(clientID)) {
                    ClientList.put(clientID, clientIP);
                }

                // To handle READ request from Clients
                if (msg.contains("READ: ")) {
                    String fileName = msg.split(";")[1].split(":")[1].trim();
                    Callable<String> readFileCallable = new ReadFile(directoryPath, fileName);
                    Future<String> futureRead = pool.submit(readFileCallable);
                    String content = futureRead.get();
                    out.writeUTF(content);
                }
                // To handle WRITE request from Clients
                else if (msg.contains("WRITE: ")) {
                    String fileName = msg.split(";")[1].split(":")[1].trim();
                    Callable<Boolean> writeFileCallable = new WriteFile(directoryPath, fileName,
                            clientID + ",  " + msg.split(";")[2].split(":")[1].trim());
                    Future<Boolean> futureWrite = pool.submit(writeFileCallable);
                    Boolean flag = futureWrite.get();
                    if (flag) {
                        out.writeUTF("File Successfully Updated");
                    } else {
                        out.writeUTF("File Not Updated");
                    }
                }
                // To handle ENQUIRY request from Clients
                else if (msg.contains("ENQUIRY")) {
                    Callable<String> getAllFileCallable = new GetAllFiles(directoryPath);
                    Future<String> futureRead = pool.submit(getAllFileCallable);
                    String content = futureRead.get();
                    out.writeUTF(content);
                }
                else    {
                    out.writeUTF("Operation not supported");
                }

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (Exception ex)  {
                ex.printStackTrace();
                break;
            }
        }
        pool.shutdown();
    }*/
}
