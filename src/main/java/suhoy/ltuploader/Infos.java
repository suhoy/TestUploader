package suhoy.ltuploader;

import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.methods.CloseableHttpResponse;
import org.apache.hc.client5.http.methods.HttpPost;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.entity.ContentType;
import org.apache.hc.core5.http.entity.EntityUtils;
import org.apache.hc.core5.http.entity.StringEntity;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Infos {

    private Map<String, String> infos = new HashMap<>();
    private String url;
    private String user;
    private String pass;
    private long run_id;

    public Infos(String url, String user, String pass, long run_id) {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.run_id = run_id;
    }

    public void setInfos(String tag, String data) {
        infos.put(tag, data);
    }

    public boolean sendInfos() {
        try {
            JSONArray message = new JSONArray();

            for (String tag : infos.keySet()) {
                JSONObject jo = new JSONObject();
                jo.put("tag", tag);
                jo.put("data", infos.get(tag));
                message.put(jo);
            }


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
                return true;
            } else {
                return false;
            }


        } catch (Exception ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
