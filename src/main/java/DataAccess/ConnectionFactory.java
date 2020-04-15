package DataAccess;

import java.sql.*;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger LOGGER= Logger.getLogger(ConnectionFactory.class.getName());
    //driver for mysql, initialized through reflection
    private static final String DRIVER= "com.mysql.cj.jdbc.Driver";
    //url connection to the database / database location
    private static final String DBURL="jdbc:mysql://localhost:3306/assignment3db";
    // user of the database
    private static final String USER="root";
    // password of the database
    private static final String PASS="root";

    //used to achieve singleton pattern
    private static ConnectionFactory singleInstance = new ConnectionFactory();

    //used to achieve singleton pattern, connection to the DB placed in a singleton object
    private ConnectionFactory(){
        try{
            Class.forName(DRIVER);
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    //create connection to the database
    private Connection createConnection(){
        Connection connection = null;
        try{
            connection= DriverManager.getConnection(DBURL,USER,PASS);
        }catch(SQLException e){
            System.out.println("ERROR: Unable to connect to the DataBase");
        }
        return connection;
    }

    /**
     * provides access to the database
     * @return the connection already created, ready for use
     */
    public static Connection getConnection(){
        return singleInstance.createConnection();
    }

    /**
     * closes a connection
     * @param connection that is wanted to be closed
     */
    public static void close(Connection connection){
        if(connection!=null){
            try{
                connection.close();
            }catch(SQLException e){

            }
        }
    }

    /**
     * closes a statement
     * @param statement that is wanted to be closed
     */
    public static void close (Statement statement){
        if(statement!=null){
            try{
                statement.close();
            }catch(SQLException e){

            }
        }
    }

    /**
     * closes a result set
     * @param resultSet that is wanted to be closed
     */
    public static void close (ResultSet resultSet){
        if(resultSet!=null){
            try{
                resultSet.close();
            }catch(SQLException e){

            }
        }
    }

}
