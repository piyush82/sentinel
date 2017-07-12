package ch.icclab.sentinel;
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

import org.apache.log4j.Logger;

import java.util.LinkedList;

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
public class KafkaThreadManager {
    final static Logger logger = Logger.getLogger(KafkaThreadManager.class);
    private static int cores = (int)(Math.floor((Runtime.getRuntime().availableProcessors() * 0.7)));
    private WorkerMetaInformation[] workerPool;
    private int nextPointer = 0;

    public KafkaThreadManager()
    {
        logger.info("Using process count - " + cores);
        workerPool = new WorkerMetaInformation[cores];
    }

    public boolean removeTopic(String topicstring)
    {
        boolean found = false;
        for(WorkerMetaInformation metaWorker:workerPool)
        {
            if(metaWorker == null) continue;
            if(metaWorker.subscribedTopics.contains(topicstring))
            {
                metaWorker.subscribedTopics.remove(topicstring);
                metaWorker.worker.setInterruptedFlag();
                while(metaWorker.t.isAlive())
                {
                    //wait -
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch(InterruptedException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                //now we can proceed
                metaWorker.worker.removeTopic(topicstring);
                metaWorker.worker.resetInterruptFlag();
                metaWorker.t = new Thread(metaWorker.worker);
                metaWorker.t.start(); //this may cause kafka consumer to be delayed by up to 5 minutes
                found = true;
                break;
            }
        }
        return found;
    }

    public boolean addTopic(String topicstring)
    {
        //first check if this topic is already assigned to some worker
        boolean found = false;
        for(WorkerMetaInformation worker:workerPool)
        {
            if(worker == null) continue;
            if(worker.subscribedTopics.contains(topicstring))
            {
                found = true;
                break;
            }
        }
        if(!found)
        {
            logger.info("Previously unseen topic found: proceeding to add - " + topicstring);

            if(AppConfiguration.getStreamDBType().equalsIgnoreCase("influxdb"))
                InfluxDBClient.addDB(topicstring);

            if(workerPool[nextPointer] != null)
            {
                workerPool[nextPointer].subscribedTopics.add(topicstring);
                String[] tempTopics = new String[1];
                tempTopics[0] = topicstring;
                workerPool[nextPointer].worker.updateTopics(tempTopics);
                workerPool[nextPointer].worker.setInterruptedFlag(); //this will make the thread exit
                while(workerPool[nextPointer].t.isAlive())
                {
                    //wait -
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch(InterruptedException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                //now we can proceed
                workerPool[nextPointer].worker.resetInterruptFlag();
                workerPool[nextPointer].t = new Thread(workerPool[nextPointer].worker);
                workerPool[nextPointer].t.start(); //this may cause kafka consumer to be delayed by up to 5 minutes
            } else {
                logger.info("No existing worker located, creating a new one.");
                workerPool[nextPointer] = new WorkerMetaInformation();
                workerPool[nextPointer].subscribedTopics.add(topicstring);
                String[] tempTopics = new String[1];
                tempTopics[0] = topicstring;
                workerPool[nextPointer].worker.updateTopics(tempTopics);
                workerPool[nextPointer].t = new Thread(workerPool[nextPointer].worker);
                workerPool[nextPointer].t.start();
            }
            nextPointer = (nextPointer + 1) % cores;
            return true;
        }
        return false;
    }

    public WorkerMetaInformation getWorker(int position)
    {
        if(position >=0 && position < cores) return workerPool[position];
        return null;
    }

}

