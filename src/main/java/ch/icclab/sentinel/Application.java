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

@Configuration
@ComponentScan
@SpringBootApplication
public class Application {

    public static KafkaThreadManager threadpool = new KafkaThreadManager();
    public static ExecutorService PersistenceWorkerPool = Executors.newFixedThreadPool((int)(Math.max(Math.ceil((Runtime.getRuntime().availableProcessors() * 0.3)) - 1.0, 1.0)));

    public static void main (String[] args)
    {
        TopicsManager topicSyncProcess = new TopicsManager();
        SpringApplication.run(Application.class, args);
        //boolean status = KafkaClient.createTopic("testing5");
        //System.out.println(status);
        //boolean status = KafkaClient.deleteTopic("testing5");
        //System.out.println(status);
        topicSyncProcess.start(); //this consumes 1 processor
    }
}
