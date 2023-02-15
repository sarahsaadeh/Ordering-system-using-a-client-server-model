
import java.util.*;

public class Order {
    public int code;
    private ArrayList<Item> items = new ArrayList();
    private Date dateCreated = new Date();
    
    public Order(int code){
        this.code = code;
    }
    public Order(int code, Order order){
        this.code = code;
        for(int i=0; i<items.size(); i++)
            items.add(new Item(order.items.get(i).name,order.items.get(i).getPriceWithoutTax(), order.items.get(i).id));
    }
    public ArrayList<Item> getItems(){
        return items;
    }
    public Date getDateCreated(){return dateCreated;}
    public void addItem(Item item){
        items.add(item);
    }
    public double orderValue(){
       double sum=0;
       for(int i=0; i<items.size(); i++)
            sum += items.get(i).getPriceWithTax();
       return sum;
    }
    public int getCode(){
    return this.code;
    }
    @Override
    public String toString(){
        return code+"-"+dateCreated;
    }
}
