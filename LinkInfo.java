import java.util.ArrayList;

public class LinkInfo {
    String parsedUrl;
    String finalUrl;
    Boolean secured;
    Boolean reachable = false;
    ArrayList<String> redirectedURLs = new ArrayList<>();
    long totalAccessDuration = 0;
    String contentLength;
    int responseCode = 0;
    String responseMessage;

    public String responseCodeToString(int responseCode){
        if(responseCode == 400){
            return  "Bad Request";
        } else if(responseCode == 404){
            return "Not Found";
        } else if(responseCode == 408){
            return "Request Timeout";
        } else if(responseCode == 503){
            return "Service Unavalible";
        } else if(responseCode == 200){
            return "OK";
        } else if(responseCode == 310){
            return "Too Many Redirects";
        }
        return "Unknown Error!";
    }
}
