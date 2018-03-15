package ekek;

public class Product {
    String name;
    Double price;

    public Product(String name, Double price){
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "name= '"+name+"' \\ price= "+price;
    }
}
