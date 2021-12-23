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
 * @author suh1995
 */
public class Main {

    final static Map<String, List<String>> args = new HashMap<>();
    final static Properties prop = new Properties();

    public static void main(String[] arg) {
        //считывание параметров
        ReadParams(arg);
        ReadProps();

        //создание теста
        System.out.println("\nСоздание теста...");
        Run run = new Run(prop.getProperty("api.run"), Long.parseLong(prop.getProperty("system.id")), prop.getProperty("api.user"), prop.getProperty("api.pass"), String.join(" ", args.get("name")), args.get("time_start").get(0), args.get("time_finish").get(0));
        if (run.createTest()) {
            System.out.println("Тест создан\n");
        } else {
            System.out.println("Ошибка при создании теста\n");
            System.exit(1);
        }

        //отправка инфы по тесту
        System.out.println("Добавление информации...");
        if (Boolean.parseBoolean(prop.getProperty("infos.enabled"))) {
            Infos infos = new Infos(prop.getProperty("api.infos"),prop.getProperty("api.user"), prop.getProperty("api.pass"), run.getId());
            for (int i = 0; i < Integer.parseInt(prop.getProperty("infos.count")); i++) {
                infos.setInfos(prop.getProperty("info" + (i + 1) + ".tag"), prop.getProperty("info" + (i + 1) + ".data"));
            }
            if (infos.sendInfos()) {
                System.out.println("Информация добавлена\n");
            } else {
                System.out.println("Ошибка при добавлении информации\n");
            }
        }

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
        System.out.println("\nStarted with args:\n");
        for (Map.Entry<String, List<String>> entry : args.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    public static void ReadProps() {
        try {
            prop.load(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"), Charset.forName("UTF-8")));
            System.out.println("\nGet config, unsorted:\n");
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
