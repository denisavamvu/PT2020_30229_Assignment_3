package PresentationLayer;

import BusinessLogic.*;
import Model.*;
import java.io.*;
import java.util.*;

public class Parser {
    private String filename;
    private ReportGenerator reportGenerator= new ReportGenerator();
    private int nrOfClientReports=1;
    private int nrOfOrderReports=1;
    private int nrOfProductReports=1;

    public Parser(String filename) {
        this.filename = filename;
    }

    private boolean checkEasyCase(String data) throws FileNotFoundException {
        if(data.equals("Report client"))
        {
            AbstractDAO clientDao= new ClientDAO();
            clientDao.selectAll(nrOfClientReports);
            nrOfClientReports++;
            return true;
        }
        if(data.equals("Report order")){
            AbstractDAO orderDao= new OrderDAO();
            orderDao.selectAll(nrOfOrderReports);
            nrOfOrderReports++;
            return true;
        }

        if(data.equals("Report product")){
            AbstractDAO productDao= new ProductDAO();
            productDao.selectAll(nrOfProductReports);
            nrOfProductReports++;
            return true;
        }
        return false;
    }
    private void checkOtherCases(String data){
        String[] values = data.split(": ");
        String[] values1 = values[1].split(", ");
        if(values[0].equals("Insert client")){
            insertClient(values1[0],values1[1]);
            return ;
        }
        if(values[0].equals("Delete client")) {
            deleteClient(values1[0]);
            return;
        }
        if(values[0].equals("Insert product")){
            insertProduct(values1[1],values1[2],values1[0]);
            return;
        }
        if(values[0].equals("Delete product")){
            deleteProduct(values1[0]);
            return ;
        }
        if(values[0].equals("Order")){
            takeOrder(values1[2],values1[0],values1[1]);
            return ;
        }
    }
    private void insertClient(String name, String address){
        AbstractDAO abstractDAO=new ClientDAO();
        Client client=new Client(name,address);
        abstractDAO.insert(client);
    }
    private void deleteClient(String client_name){
        AbstractDAO clientDAO=new ClientDAO();
        AbstractDAO orderDAO=new OrderDAO();
        Client client= (Client) clientDAO.findByName("client_name",client_name);
        Order order=(Order)orderDAO.findByName("client_name",client_name);
        while(order!=null){
            orderDAO.delete(order);
            order=(Order)orderDAO.findByName("client_name",client_name);
        }
        clientDAO.delete(client);
    }
    private void insertProduct(String product_quantity,String product_price,String name){
        AbstractDAO abstractDAO=new ProductDAO();
        int quantity=Integer.parseInt(product_quantity);
        float price=Float.parseFloat(product_price);
        Product product=new Product(name,quantity,price);
        Product p= (Product) abstractDAO.findByName("product_name",name);
        if(p==null)
            abstractDAO.insert(product);
        else{
            abstractDAO.update("stock_quantity",p.getStock_quantity()+quantity,p);
        }
    }
    private void deleteProduct(String values1){
        AbstractDAO productDAO=new ProductDAO();
        AbstractDAO orderDAO=new OrderDAO();
        Product product= (Product) productDAO.findByName("product_name",values1);
        Order order=(Order)orderDAO.findByName("product_name",values1);
        while(order!=null){
            orderDAO.delete(order);
            order=(Order)orderDAO.findByName("product_name",values1);
        }
        productDAO.delete(product);
    }
    private void takeOrder(String order_quantity,String client_name,String product_name){
        AbstractDAO orderDAO=new OrderDAO();
        AbstractDAO productDAO=new ProductDAO();

        int quantity=Integer.parseInt(order_quantity);
        Order order=new Order(client_name,product_name,quantity);
        Product p= (Product) productDAO.findByName("product_name",product_name);
        float total = quantity * p.getPrice();
        String name= order.getClient_name();
        String prod=order.getProduct_name();
        if(p==null){
            return ;
        }
        if(quantity>p.getStock_quantity())
        {
            reportGenerator.generateBill("Not enough "+prod+"s in stock","We are sorry but your order cannot be processed! At the moment, product "+prod+" is not available in this quantity. Current stock quantity is: "+p.getStock_quantity());
            return ;
        }

        orderDAO.insert(order);

        productDAO.update("stock_quantity",p.getStock_quantity()-quantity,p);
        reportGenerator.generateBill("Bill "+name+" "+prod+"s","Order placed for client "+name+". Product: "+prod+", quantity: " +quantity+", total: "+total);
        OrderItem orderItem=new OrderItem();
    }

    /**
     * open the text file, read from it line by line, parse the information
     */
    public void readFromFile(){
        String data=new String();
        try {
            File f = new File(filename);
            Scanner s = new Scanner(f);
            while(s.hasNextLine()){
                data = s.nextLine();
                if(checkEasyCase(data)==false){
                    checkOtherCases(data);
                }
            }
        }
        catch(FileNotFoundException e){
            System.out.println("error file not found");
            e.printStackTrace();
        }
    }
}
