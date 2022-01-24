# TestDataUploader
Uploading test data util for [load testing hub](https://github.com/suhoy/cms-boot)  

Util creates run entity and upload info, graph, attach and stat.  

To collect graph use [grafana downloader](https://github.com/suhoy/GrafanaDownloader)  
To collect stat use [influx exporter json](https://github.com/suhoy/InfluxExporterJson)  



### Arguments
```java
-name           Run name  
-config         Path to config file  
-time_start     Run start time  
-time_finish    Run finish time  
-graphs         Folder with graphs (absolute or relative path)  
-attaches       Folder with graphs (absolute or relative path)  
-stats          Folder with stats (absolute or relative path)  

```

### Start example
```java
java -jar TestUploader-1.0.jar -name Примерный запуск -config config.txt -time_start 2021-12-01T07:55 -time_finish 2021-12-02T22:01 -graphs .\input\graphs -attaches .\input\attaches -stats .\input\stats  
```  
  
  
### Config example  
```properties
#links
system.id=1
api.run=http://localhost:8080/api/add/run
api.infos=http://localhost:8080/api/add/infos
api.graph=http://localhost:8080/api/add/graph
api.attach=http://localhost:8080/api/add/attach
api.stat=http://localhost:8080/api/add/stats
api.result=http://localhost:8080/run_view?id=

#auth
api.user=user@admin.com
api.pass=user

#infos
infos.enabled=true
infos.count=2

info1.tag=Описание теста
info1.data=Тест прошёл...

info2.tag=Проблемы теста
info2.data=Начнём с того...


#graphs
graphs.enabled=true
graphs.count=3

graph1.file=graph1.png
graph1.tag=Тэг графика 1
graph1.about=Описание графика 1

graph2.file=graph2.png
graph2.tag=Тэг графика 1
graph2.about=Описание графика 2

graph3.file=graph3.png
graph3.tag=Тэг графика 2
graph3.about=Описание графика 3


#attaches
attaches.enabled=true
attaches.count=2

attach1.file=cat1.rar
attach1.tag=Тэг вложения 1

attach2.file=cat2.rar
attach2.tag=Тэг вложения 2


#stats
stats.enabled=true
stats.count=1
stat1.file=stat1.json
```
