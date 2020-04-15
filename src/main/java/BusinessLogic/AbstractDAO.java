package BusinessLogic;

import DataAccess.ConnectionFactory;
import PresentationLayer.ReportGenerator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AbstractDAO <T>{

    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private Class<T> type;
    private ReportGenerator<T> reportGenerator=new ReportGenerator<T>();

    /**
     * for each AbstractDAO object obtain the class of the generic type T
     */
    @SuppressWarnings("unchecked")
    public AbstractDAO(){
        this.type=(Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    //create insert into query using an object and its type to determine the right table
    private String createInsertQuery(Object object){
        StringBuilder s=new StringBuilder();
        s.append("Insert into ");
        s.append(object.getClass().getSimpleName()+"s");
        s.append(" (");
        for(Field field: object.getClass().getDeclaredFields()){
            field.setAccessible(true);
            if(!field.getName().equals("id")) {
                s.append(field.getName());
                s.append(",");
            }
        }
        s.setLength(s.length() - 1);
        s.append(") VALUES (");
        for(Field field: object.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try{
                Object value;
                value=field.get(object);
                if(!field.getName().equals("id")) {
                    s.append("\""+value+"\""+",");
                }
            }catch(IllegalArgumentException e){

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        s.setLength(s.length() - 1);
        s.append(");");
        return s.toString();
    }

    /**
     * insert an object to the right table
     * @param object to be inserted
     */
    public void insert(Object object){
        Connection connection=null;
        PreparedStatement statement=null;
        String sqlQuery=createInsertQuery(object);
        try{
            connection=ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }

    //create select query conditioned by a field and its value
    private String createSelectQuery(String field,String fieldValue){
        StringBuilder s=new StringBuilder();
        s.append("select * from ");
        s.append(type.getSimpleName()+"s WHERE ");
        s.append(field+"="+"\""+fieldValue+"\""+";");
        return s.toString();
    }

    /**
     * execute select query
     * @param field field wanted
     * @param fieldValue value that we are looking for
     * @return the object wanted
     */
    public T findByName(String field,String fieldValue){
        Connection connection=null;
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        String query = createSelectQuery(field,fieldValue);
        try{
            connection=ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);
            resultSet=statement.executeQuery();

             List <T> objList = createObjects(resultSet);
             if(objList.size()>0)
                 return objList.get(0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //turn a result set into a generic list of objects to be used in others methods
    private List<T> createObjects(ResultSet resultSet){

        List<T> list =new ArrayList<T>();

        try{
            while(resultSet.next()){
                T instance = type.newInstance();
                for(Field field:type.getDeclaredFields()){
                    Object value=resultSet.getObject(field.getName());
                    PropertyDescriptor propertyDescriptor=new PropertyDescriptor(field.getName(),type);
                    Method method= propertyDescriptor.getWriteMethod();
                    method.invoke(instance,value);
                }
                list.add(instance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return list;
    }

    //create update query using field name, new value and object to be updated
    private String createUpdateQuery(String fieldname, int value,T object){
        StringBuilder s= new StringBuilder();
        s.append("UPDATE "+type.getSimpleName()+"s ");
        s.append("SET "+fieldname+"="+value+" WHERE ");
        for(Field field: type.getDeclaredFields()){
            Object fieldValue;
            field.setAccessible(true);
            s.append(field.getName()+"=");
            try {
                fieldValue=field.get(object);
                s.append("\""+fieldValue+"\""+" and ");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        s.setLength(s.length()-5);
        s.append(";");
        return s.toString();
    }

    /**
     * update a table by updating a field
     * @param fieldname name of the field to be updated
     * @param value new value for update
     * @param object from the database to be updated
     */
    public void update(String fieldname, int value,T object){
        Connection connection=null;
        PreparedStatement statement=null;
        String sqlQuery=createUpdateQuery(fieldname,value,object);
        try{
            connection=ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }

    //create delete query adding conditions mapped to the parameter
    private String createDeleteQuery(T object){
        StringBuilder s= new StringBuilder();
        s.append("DELETE FROM "+type.getSimpleName()+"s WHERE ");
        for(Field field: type.getDeclaredFields()){
            Object fieldValue;
            field.setAccessible(true);
            s.append(field.getName()+"=");
            try {
                fieldValue=field.get(object);
                s.append("\""+fieldValue+"\""+" and ");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        s.setLength(s.length()-5);
        s.append(";");
        return s.toString();
    }

    /**
     * execute delete statement
     * @param object to delete
     */
    public void delete(T object){
        Connection connection=null;
        PreparedStatement statement=null;
        String sqlQuery=createDeleteQuery(object);
        try{
            connection=ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }
    //create a select * query using class(table) name
    private String createSelectAllQuery(){
        StringBuilder s=new StringBuilder();
        s.append("select * from ");
        s.append(type.getSimpleName()+"s;");
        return s.toString();
    }

    /**
     * create a report file executing a generic sql select * query
     * @param number of the file
     */
    public void selectAll(int number){
        Connection connection=null;
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        String query = createSelectAllQuery();
        try{
            connection=ConnectionFactory.getConnection();
            statement=connection.prepareStatement(query);
            resultSet=statement.executeQuery();

            List <T> objList = createObjects(resultSet);
            reportGenerator.createReport(objList,number);


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //return null;
    }


}
