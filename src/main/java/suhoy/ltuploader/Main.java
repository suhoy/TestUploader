package suhoy.ltuploader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        System.out.println("\n==========TestUploader started==========");

        //считывание параметров
        ReadParams(arg);
        ReadProps(args.get("config").get(0));

        boolean testcreated = false;

        //создание теста
        System.out.println("\nСоздание теста...");
        Run run = new Run(prop.getProperty("api.run"), Long.parseLong(prop.getProperty("system.id")), prop.getProperty("api.user"), prop.getProperty("api.pass"), String.join(" ", args.get("name")), args.get("time_start").get(0), args.get("time_finish").get(0));
        if (run.createTest()) {
            System.out.println("Тест создан.\n");
            testcreated = true;
        } else {
            System.out.println("Ошибка при создании теста.\n");
            System.exit(1);
        }


        //отправка инфы по тесту
        if (Boolean.parseBoolean(prop.getProperty("infos.enabled"))) {
            System.out.println("Добавление информации...");
            Infos infos = new Infos(prop.getProperty("api.infos"), prop.getProperty("api.user"), prop.getProperty("api.pass"), run.getId());
            for (int i = 0; i < Integer.parseInt(prop.getProperty("infos.count")); i++) {
                try {
                    String tag = prop.getProperty("info" + (i + 1) + ".tag");
                    String data = prop.getProperty("info" + (i + 1) + ".data");
                    infos.addInfos(tag, data);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (infos.sendInfos()) {
                System.out.println("Информация добавлена\n");
            } else {
                System.out.println("Ошибка при добавлении информации.\n");
            }
        }


        //отправка графиков
        if (Boolean.parseBoolean(prop.getProperty("graphs.enabled"))) {
            System.out.println("Добавление графиков...");

            Graphs graphs = new Graphs(prop.getProperty("api.graph"), prop.getProperty("api.user"), prop.getProperty("api.pass"), run.getId());
            for (int i = 0; i < Integer.parseInt(prop.getProperty("graphs.count")); i++) {
                try {
                    String tag = prop.getProperty("graph" + (i + 1) + ".tag");
                    String about = prop.getProperty("graph" + (i + 1) + ".about");
                    String filename = prop.getProperty("graph" + (i + 1) + ".file");
                    String path = args.get("graphs").get(0);
                    File file = new File(path + "\\" + filename);

                    graphs.addGraphs(tag, about, filename, file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (graphs.sendGraphs()) {
                System.out.println("Графики добавлены.\n");
            } else {
                System.out.println("Ошибка при добавлении графиков.\n");
            }
        }


        //отправка вложений
        if (Boolean.parseBoolean(prop.getProperty("attaches.enabled"))) {
            System.out.println("Добавление вложений...");

            Attaches attaches = new Attaches(prop.getProperty("api.attach"), prop.getProperty("api.user"), prop.getProperty("api.pass"), run.getId());
            for (int i = 0; i < Integer.parseInt(prop.getProperty("attaches.count")); i++) {
                try {
                    String tag = prop.getProperty("attach" + (i + 1) + ".tag");
                    String filename = prop.getProperty("attach" + (i + 1) + ".file");
                    String path = args.get("attaches").get(0);
                    File file = new File(path + "\\" + filename);

                    attaches.addAttach(tag, filename, file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (attaches.sendAttaches()) {
                System.out.println("Вложения добавлены.\n");
            } else {
                System.out.println("Ошибка при добавлении вложений.\n");
            }
        }

        //отправка статистики
        if (Boolean.parseBoolean(prop.getProperty("stats.enabled"))) {
            System.out.println("Добавление статистики...");

            Stats stats = new Stats(prop.getProperty("api.stat"), prop.getProperty("api.user"), prop.getProperty("api.pass"), run.getId());
            for (int i = 0; i < Integer.parseInt(prop.getProperty("stats.count")); i++) {
                try {
                    String filename = prop.getProperty("stat" + (i + 1) + ".file");
                    String path = args.get("stats").get(0);
                    String jas = readFile(path + "\\" + filename, StandardCharsets.UTF_8);
                    stats.addStat(jas);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (stats.sendStats()) {
                System.out.println("Статистика добавлена.\n");
            } else {
                System.out.println("Ошибка при добавлении статистики.\n");
            }
        }

        if (testcreated) {
            System.out.println("Ссылка на протокол: " + prop.getProperty("api.result") + run.getId());
        }

        System.out.println("\n==========TestUploader stopped==========");
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
        System.out.println("\nStarted with args:");
        for (Map.Entry<String, List<String>> entry : args.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    public static void ReadProps(String config) {
        try {
            //prop.load(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"), Charset.forName("UTF-8")));

            //InputStream input = new FileInputStream(config);
            prop.load(new InputStreamReader(new FileInputStream(config), "UTF-8"));
            //input.close();

            System.out.println("\nGet config, unsorted:");
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

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
