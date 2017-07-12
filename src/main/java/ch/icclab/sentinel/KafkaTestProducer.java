package ch.icclab.sentinel;
/*
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

import org.apache.kafka.clients.producer.*;
import org.apache.log4j.Logger;

import java.util.Properties;

public class KafkaTestProducer {
    final static Logger logger = Logger.getLogger(KafkaTestProducer.class);

    //change send to main if you wish to have this as a standalone sender application
    public static void send(String[] args)
    {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka.demonstrator.info:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        TestCallback callback = new TestCallback();
        for(int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<String, String>("testing2", "udr", Integer.toString(i)), callback);

        producer.close();
    }


    private static class TestCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                System.out.format("Error while producing message to topic : {}", recordMetadata);
                e.printStackTrace();
            } else {
                String message = String.format("Sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                logger.info(message);
            }
        }
    }
}
