import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IllegalArgumentException{
        if(args.length != 1){
            throw new IllegalArgumentException("Too many or too little arguments!");
        }

        Connection.Response response = null;
        Document doc = null;

        try{
            doc = Jsoup.connect(args[0]).get();
        } catch(IOException ex){
            ex.getStackTrace();
        }

        LinkCrawler linkCrawler = new LinkCrawler(args[0], doc);

    }
}
