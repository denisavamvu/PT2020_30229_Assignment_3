package Model;

public class Client {
    private int id;
    private String client_name;
    private String address;

    public Client(){
    }

    public Client(int client_id, String name, String address) {
        this.id = client_id;
        this.client_name = name;
        this.address = address;
    }
    public Client(String name, String address) {
        this.client_name = name;
        this.address = address;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setClient_name(String name) {
        this.client_name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getAddress() {
        return address;
    }
}
