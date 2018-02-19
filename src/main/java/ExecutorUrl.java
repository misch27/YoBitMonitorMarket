
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

public class ExecutorUrl {
    private final String market;
    private final String url = "https://yobit.net/ajax/system_markets.php";

    public ExecutorUrl(String market) {
        this.market = market;
    }


    public String sendPost() throws IOException {

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        String USER_AGENT = "Mozilla/5.0";
        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("method", "change_market_base"));
        urlParameters.add(new BasicNameValuePair("csrf_token", ""));
        urlParameters.add(new BasicNameValuePair("locale", "ru"));
        urlParameters.add(new BasicNameValuePair("base", market));
        urlParameters.add(new BasicNameValuePair("pid", "9"));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);


        if(response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";

            while ((line = rd.readLine()) != null) {
                result.append(line
                        .replaceAll("\\\\n","")
                        .replaceAll("\\\\t",""));
            }

            JSONObject json = new JSONObject(result.toString());
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>")
                    .append("<html lang=\"en\">")
                    .append("<head>")
                    .append("<meta charset=\"UTF-8\">")
                    .append("<title>Document</title>")
                    .append("</head><body><table>")
                    .append(json.get("html").toString())
                    .append("</table></body></html>");
            return html.toString();
        }
        return null;
    }
}

