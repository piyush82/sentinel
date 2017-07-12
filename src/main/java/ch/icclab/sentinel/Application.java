/*
 * Copyright (c) 2017. ZHAW - ICCLab
 *  All Rights Reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may obtain
 *     a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *     WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *     License for the specific language governing permissions and limitations
 *     under the License.
 */

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */

package ch.icclab.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackageClasses = {
        APIController.class
})
public class Application {
    final static Logger logger = Logger.getLogger(Application.class);
    public static KafkaThreadManager threadpool = new KafkaThreadManager();
    public static SeriesStructureCache msgFormatCache;
    public static ExecutorService PersistenceWorkerPool = Executors.newFixedThreadPool((int)(Math.max(Math.ceil((Runtime.getRuntime().availableProcessors() * 0.3)) - 1.0, 1.0)));

    public static void main (String[] args)
    {
        SpringApplication.run(Application.class, args);
        //boolean status = KafkaClient.createTopic("zane-sensor-data");
        //System.out.println(status);
        //boolean status = KafkaClient.deleteTopic("testing5");
        //System.out.println(status);
        if(!Initialize.isDbValid()) {
            Initialize.prepareDbInitScripts();
            Initialize.initializeDb();
        }
        msgFormatCache = new SeriesStructureCache(AppConfiguration.getSeriesFormatCacheSize());
        if(AppConfiguration.getStreamDBType().equalsIgnoreCase("influxdb")) InfluxDBClient.init();
        //SqlDriver.isDuplicateUser("piyush@zhaw.ch");
        TopicsManager topicSyncProcess = new TopicsManager();
        topicSyncProcess.start(); //this consumes 1 processor core
    }

}
