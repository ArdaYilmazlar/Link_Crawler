import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

//Used to find all links in given document
public class LinkCrawler {
    Elements links;
    ArrayList<LinkInfo> linkInfoArrayList = new ArrayList<>();

    public LinkCrawler(String URL, Document doc){
        links = doc.select("a[href]");
        analyzeLinks();
        printLinkInfoArrayList();
    }

    public void analyzeLinks(){
        for(Element link: links){
            long startTime = System.currentTimeMillis();

            LinkInfo linkInfo = new LinkInfo();
            linkInfo.parsedUrl = link.attr("abs:href");
            linkInfo.secured = checkIfSecured(linkInfo.parsedUrl);
            setResponseStatus(linkInfo);

            if(linkInfo.responseCode == 200){
                linkInfo.reachable = true;
            }

            long endTime = System.currentTimeMillis();
            linkInfo.totalAccessDuration = endTime - startTime;
            linkInfoArrayList.add(linkInfo);
        }
    }

    private int followRedirects(LinkInfo linkInfo){
        String URL = linkInfo.parsedUrl;
        try{
            for(int counter = 0; counter < 4; counter++) {
                if(counter == 4){
                    return 310;
                }
                Connection.Response response = Jsoup.connect(URL).followRedirects(false).execute();
                if(response.hasHeader("location")){
                    URL = response.header("location");
                    linkInfo.redirectedURLs.add(URL);
                }else{
                    linkInfo.finalUrl = URL;
                    linkInfo.contentLength = response.header("Content-Length");
                }
            }
        } catch(Exception e){
                //Won't happen
            }
        return 200;
    }

    private void setResponseStatus(LinkInfo linkInfo){
        Connection.Response response = null;
        try{
            response = Jsoup.connect(linkInfo.parsedUrl).timeout(3000).execute();
        }catch(HttpStatusException ex){
            linkInfo.responseCode = 404;
        }catch(SocketTimeoutException ex){
            linkInfo.responseCode = 408;
        }catch(UnknownHostException ex){
            linkInfo.responseCode = 503;
        }catch(MalformedURLException ex){
            linkInfo.responseCode = 400;
        }catch(IOException ex){
            linkInfo.responseCode = 999;
        }
        if(linkInfo.responseCode == 0){
            linkInfo.responseCode = followRedirects(linkInfo);
        }

        if(linkInfo.responseCode == 0)
            linkInfo.responseCode = 200;
        linkInfo.responseMessage = linkInfo.responseCodeToString(linkInfo.responseCode);
    }

    private boolean checkIfSecured(String link){
        link = link.trim();
        link = link.toLowerCase();
        if(link.substring(0, 5).equals("https")){
            return true;
        }
        return false;

    }

    void printLinkInfoArrayList(){
        for(LinkInfo linkInfo: linkInfoArrayList){
            System.out.println("Parsed URL: " + linkInfo.parsedUrl);
            System.out.println("Final URL: " + linkInfo.finalUrl);
            System.out.println("Secured: " + linkInfo.secured);
            System.out.println("Reachable: " + linkInfo.reachable);
            System.out.println("Total Access Duration: " + linkInfo.totalAccessDuration);
            System.out.println("Content Length: " + linkInfo.contentLength);
            System.out.println("Response Code: " + linkInfo.responseCode);
            System.out.println("Response Message: " + linkInfo.responseMessage);
            System.out.print("\n");
        }
    }

    void printLinks(){
        for(Element link : links){
            System.out.printf("*Link <%s>\n", link.attr("abs:href"));
        }
    }
}
