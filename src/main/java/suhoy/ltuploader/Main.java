package suhoy.ltuploader;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author suh1995
 */
public class Main {

    final static Map<String, List<String>> args = new HashMap<>();
    final static Properties prop = new Properties();

    public static void main(String[] arg) {
        ReadParams(arg);
        ReadProps();
        Test test = new Test(prop.getProperty("api.test"), prop.getProperty("api.user"),prop.getProperty("api.pass"), String.join(" ", args.get("name")), args.get("time_start").get(0), args.get("time_finish").get(0));
        test.createTest();

    }

    public static void ReadParams(String[] arg) {
        List<String> options = null;
        for (int i = 0; i < arg.length; i++) {
            final String a = arg[i];

            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    System.err.println("Error at argument " + a);
                    return;
                }

                options = new ArrayList<>();
                args.put(a.substring(1), options);
            } else if (options != null) {
                options.add(a);
            } else {
                System.err.println("Illegal parameter usage");
                return;
            }
        }
        System.out.println("Started with args:");
        for (Map.Entry<String, List<String>> entry : args.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    public static void ReadProps() {
        try {
            prop.load(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.txt"), Charset.forName("UTF-8")));
            System.out.println("\r\nGet config, unsorted:");
            Enumeration keys = prop.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = (String) prop.get(key);
                System.out.println(key + ": " + value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
