package Model;

public class Order {
    private String client_name;
    private String product_name;
    private int quantity;

    public Order(){

    }
    public Order(String client_name, String product_name, int quantity) {
        this.client_name = client_name;
        this.product_name = product_name;
        this.quantity = quantity;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public int getQuantity() {
        return quantity;
    }
}
