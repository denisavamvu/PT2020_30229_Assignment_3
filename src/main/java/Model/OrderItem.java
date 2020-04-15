package Model;

public class OrderItem {
    private int client_id;
    private int product_id;

    public OrderItem(int client_id, int product_id) {
        this.client_id = client_id;
        this.product_id = product_id;
    }
    public OrderItem(){}

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public int getProduct_id() {
        return product_id;
    }
}
