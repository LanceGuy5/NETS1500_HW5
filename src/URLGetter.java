import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class will use HTTP to get the contents of a page
 */
public class URLGetter {

    private URL url;
    private HttpURLConnection httpConnection;

    /**
     * Creates a URL from a string.
     * Opens the connection to be used later.
     * @param url the url to get information from
     */
    public URLGetter(String url) {
        try {
            this.url = new URL(url);

//            httpConnection = new HttpURLConnection(this.url);

            URLConnection connection = this.url.openConnection();
            httpConnection = (HttpURLConnection) connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will print the status code and message
     * from the connection.
     */
    public void printStatusCode() {
        try {
            int code = httpConnection.getResponseCode();
            String message = httpConnection.getResponseMessage();

            System.out.println(code + " : " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will get the HTML contents and return an arraylist
     * @return the arraylist of strings from the HTML page.
     */
    public ArrayList<String> getContents() {
        ArrayList<String> contents = new ArrayList<>();

        try {
            Scanner in = new Scanner(httpConnection.getInputStream());

            while(in.hasNextLine()) {
                String line = in.nextLine();
                contents.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return contents;
    }


}
