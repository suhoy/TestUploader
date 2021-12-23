package suhoy.ltuploader;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.sling.commons.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Attaches {
    private String url;
    private String user;
    private String pass;
    private long run_id;
    private List<String> tags = new ArrayList<>();
    private List<File> datas = new ArrayList<>();
    private List<String> filenames = new ArrayList<>();

    public Attaches(String url, String user, String pass, long run_id) {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.run_id = run_id;
    }

    public void addAttach(String tag, String filename, File data) {
        tags.add(tag);
        filenames.add(filename);
        datas.add(data);
    }

    public boolean sendAttaches() {
        boolean result = false;
        for (int i = 0; i < tags.size(); i++) {
            try {
                CloseableHttpClient httpclient = HttpClients.createDefault();

                HttpPost httpPost = new HttpPost(url + "?run_id=" + run_id + "&tag=" + URLEncoder.encode(tags.get(i), "UTF-8"));
                String encoding = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
                httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);

                HttpEntity entityreq = MultipartEntityBuilder
                        .create()
                        .addBinaryBody("data", datas.get(i), ContentType.APPLICATION_OCTET_STREAM, filenames.get(i))
                        .build();
                httpPost.setEntity(entityreq);

                CloseableHttpResponse response = httpclient.execute(httpPost);
                HttpEntity entityresp = response.getEntity();


                String json = EntityUtils.toString(entityresp, StandardCharsets.UTF_8);

                System.out.println("Req:");
                System.out.println(filenames.get(i));
                System.out.println("Res:");
                System.out.println(json);

                JSONObject jo = new JSONObject(json);
                if (jo.getString("result").equalsIgnoreCase("ok")) {
                    result = true;
                }
                entityreq.close();
                httpPost.cancel();

            } catch (Exception ex) {
                Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return result;
    }
}
