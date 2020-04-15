package Model;

public class Product {
    private int id;
    private String product_name;
    private int stock_quantity;
    private float price;

    public Product(){

    }
    public Product(int id, String product_name, int stock_quantity, float price) {
        this.id = id;
        this.product_name = product_name;
        this.stock_quantity = stock_quantity;
        this.price = price;
    }
    public Product(String product_name, int stock_quantity, float price) {
        this.product_name = product_name;
        this.stock_quantity = stock_quantity;
        this.price = price;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public float getPrice() {
        return price;
    }
}
