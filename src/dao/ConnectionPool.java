
package dao;


import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConnectionPool {

    private Set<Connection> connections = new HashSet<>();

    private static ConnectionPool instance;

    static final int MAX_CONNECTIONS = 10;

    private final String db = "jdbc:ucanaccess://";
    

    private ConnectionPool(){
    	Path pathToDB = Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath().toString(), "cmsj.mdb");
    	System.out.println("Path to DB validation: " + pathToDB);
    	try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        for (int i = 0; i < MAX_CONNECTIONS; i++){
            try{
                Connection connection = DriverManager.getConnection(db + pathToDB + ";memory=true");
                connections.add(connection);
            }
            catch (SQLException e){
                System.out.print(e.getMessage());
            }
        }
        System.out.println("Validation: Pool created successfully, contains: "+ connections.size());
    }

    public synchronized static ConnectionPool getInstance(){
        if (instance == null){
            instance = new ConnectionPool();
        }
        return instance;
    }

    public synchronized Connection getConnection(){
        while (connections.isEmpty()){
            try{
                wait();
            }
            catch (InterruptedException e){
                System.out.print(e.getMessage());
            }
        }

        Iterator<Connection> iter = connections.iterator();
        Connection connection = iter.next();
        iter.remove();
        return connection;
    }

    public synchronized void returnConnection(Connection connection){
        connections.add(connection);
        notifyAll();
    }

    public synchronized void closeAll(){
        int count = 0;
        while (count < MAX_CONNECTIONS){
            while (connections.isEmpty()){
                try{
                    wait();
                }
                catch (InterruptedException e){
                    System.out.print(e.getMessage());
                }
            }
            Iterator<Connection> iter = connections.iterator();
            while (iter.hasNext()){
                Connection connection = iter.next();
                try{
                    connection.close();
                    count++;
                }
                catch (SQLException e){
                    System.out.print(e.getMessage());
                }
            }
        }
    }

}
