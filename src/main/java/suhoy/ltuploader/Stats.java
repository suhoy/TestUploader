package suhoy.ltuploader;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Stats {

    private String url;
    private String user;
    private String pass;
    private long run_id;
    private List<JSONArray> messages = new ArrayList<>();

    public Stats(String url, String user, String pass, long run_id) {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.run_id = run_id;
    }

    public void addStat(String message) {
        try {
            messages.add(new JSONArray(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean sendStats() {
        boolean result = false;
        for (JSONArray message : messages) {


            try {
                CloseableHttpClient httpclient = HttpClients.createDefault();

                HttpPost httpPost = new HttpPost(url + "?run_id=" + run_id);
                String encoding = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
                httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);

                HttpEntity stringEntity = new StringEntity(message.toString(), ContentType.APPLICATION_JSON);
                httpPost.setEntity(stringEntity);

                CloseableHttpResponse response = httpclient.execute(httpPost);
                HttpEntity entity = response.getEntity();

                String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                System.out.println("Req:");
                System.out.println(message.toString());
                System.out.println("Res:");
                System.out.println(json);

                JSONObject jo = new JSONObject(json);
                if (jo.getString("result").equalsIgnoreCase("ok")) {
                    result = true;
                }


            } catch (Exception ex) {
                Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
}
