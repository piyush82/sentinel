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

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
public class TopicsManager extends Thread {
    final static Logger logger = Logger.getLogger(TopicsManager.class);
    public void run()
    {
        logger.info("Starting topics manager thread.");
        while(true)
        {
            String[] topics = KafkaClient.listTopics();
            //for( String topic: topics) System.out.println(topic + " ");
            LinkedList<String> registeredTopics = SqlDriver.getGlobalTopicsList();
            //for( String topic: registeredTopics) System.out.println(topic + " ");

            //checking if all topics found are registered with sentinel - if not they must be deleted
            //now assign the topics to worker threads
            for(String topic:topics)
            {
                if (!topic.startsWith("__")) {
                    if (registeredTopics.contains(topic))
                        Application.threadpool.addTopic(topic);
                    else {
                        if (!topic.equalsIgnoreCase("zane-sensor-data"))
                        {
                            boolean status = KafkaClient.deleteTopic(topic);
                            Application.threadpool.removeTopic(topic);
                            if (status)
                                logger.info("Topic: " + topic + " has been deleted from kafka cluster");
                            else
                                logger.warn("Topic: " + topic + " could not be removed from kafka cluster");
                        }
                        else //just for zane-sensor-data
                        {
                            Application.threadpool.addTopic(topic);
                        }
                    }
                }
            }

            for(String topic:registeredTopics)
            {
                if(!Arrays.asList(topics).contains(topic))
                {
                    boolean status = KafkaClient.createTopic(topic);
                    if(status)
                    {
                        logger.info("Topic: " + topic + " has been registered with kafka cluster");
                        Application.threadpool.addTopic(topic);
                    }
                    else
                    {
                        logger.warn("Topic: " + topic + " could not be registered with kafka cluster");
                    }
                }
            }

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
