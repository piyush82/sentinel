package ch.icclab.sentinel;/*
 * Copyright (c) 2017. Cyclops-Labs Gmbh
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
public class TopicsManager extends Thread {

    public void run()
    {
        System.out.println("Starting topics manager thread.");
        while(true)
        {
            String[] topics = KafkaClient.listTopics();
            //now assign the topics to worker threads
            for(String topic:topics)
                if(!topic.startsWith("__"))
                    Application.threadpool.addTopic(topic);
            
            try
            {
                Thread.sleep(AppConfiguration.getTopicCheckWaitingPeriod());
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
