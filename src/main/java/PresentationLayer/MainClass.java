package PresentationLayer;

public class MainClass {
    /**
     * main method, runs the entire program
     * @param args received from the command line
     */
    public static void main(String[]args) {
        //create an object which will parse the data from a .txt file and apply the operations needed
        Parser parser=new Parser(args[0]);
        parser.readFromFile();
    }
}
