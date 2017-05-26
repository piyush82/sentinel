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

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.ArrayList;
import java.util.Properties;

public class KafkaWorker implements Runnable {
    private ArrayList<String> topics;
    private boolean isInterrupted;

    public KafkaWorker()
    {
        topics = new ArrayList<>();
        isInterrupted = false;
    }

    public void updateTopics(String[] streams)
    {
        for(String stream:streams)
        {
            if(!topics.contains(stream)) topics.add(stream);
        }
    }

    public void removeTopic(String topic)
    {

        if(topics.contains(topic)) topics.remove(topic);
    }

    public void resetInterruptFlag()
    {
        isInterrupted = false;
    }

    public void setInterruptedFlag()
    {
        isInterrupted = true;
    }

    public String[] getTopics()
    {
        return topics.toArray(new String[topics.size()]);
    }

    @Override
    public String toString()
    {
        String temp = "Subscribed to channels: ";
        for (String topic:topics) {
            temp += topic + ", ";
        }
        return temp;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " starting with " + topics.size() + " topics.");
        for(String topic:topics)
        {
            System.out.println(Thread.currentThread().getName() + " subscribing to: " + topic);
        }

        Properties props = new Properties();

        props.put("bootstrap.servers", AppConfiguration.getKafkaURL());
        props.put("group.id", "group1");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("metadata.max.age.ms", 5000);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(topics);

        while(!isInterrupted)
        {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                Runnable dbWorker = new PersistenceWorker(record.topic(), record.key(), record.offset(), record.value());
                Application.PersistenceWorkerPool.execute(dbWorker);
                // print the offset,key and value for the consumer records.
                //System.out.printf("topic = %s, offset = %d, key = %s, value = %s\n", record.topic(), record.offset(), record.key(), record.value());
            }
        }

        System.out.println(Thread.currentThread().getName() + " quitting now. Was subscribed with " + (topics.size() - 1) + " topics.");
    }
}
