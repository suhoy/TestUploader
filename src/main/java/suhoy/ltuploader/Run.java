package suhoy.ltuploader;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.methods.CloseableHttpResponse;
import org.apache.hc.client5.http.methods.HttpPost;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.entity.ContentType;
import org.apache.hc.core5.http.entity.EntityUtils;
import org.apache.hc.core5.http.entity.StringEntity;
import org.apache.sling.commons.json.JSONObject;
import java.util.Base64;

/**
 *
 * @author suh1995
 */
public class Run {

    private long id;
    private long system_id;
    private String url;
    private String start;
    private String end;
    private String name;
    private String user;
    private String pass;
    private JSONObject message;

    public Run(String url, long system_id, String user, String pass, String name, String start, String end) {
        try {
            this.url = url;
            this.system_id=system_id;
            this.start = start;
            this.end = end;
            this.user = user;
            this.pass = pass;
            this.name = name;
            message = new JSONObject();
            message.put("name", name);
            message.put("time_start", start);
            message.put("time_finish", end);
        } catch (Exception ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean createTest() {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            
            HttpPost httpPost = new HttpPost(url+"?system_id="+system_id);
            String encoding = Base64.getEncoder().encodeToString((user+":"+pass).getBytes());
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
            httpPost.removeHeaders("Content type");
            //httpPost.setHeader("Content Type", "application/json");
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
            this.id = jo.getLong("id");
            return true;

        } catch (Exception ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public long getId() {
        return id;
    }

}
