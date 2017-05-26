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

import java.util.*;

import kafka.admin.RackAwareMode;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

public class KafkaClient {
    public static String[] listTopics()
    {
        Map<String, List<PartitionInfo>> topics;

        Properties props = new Properties();
        props.put("bootstrap.servers", AppConfiguration.getKafkaURL());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        topics = consumer.listTopics();
        Set<String> keys = topics.keySet();

        return keys.toArray(new String[keys.size()]);
    }

    public static boolean createTopic(String topicName)
    {
        ZkClient zkClient = null;
        ZkUtils zkUtils = null;
        try
        {
            String zookeeperHosts = AppConfiguration.getZookeeperURL(); // If multiple zookeeper then -> String zookeeperHosts = "192.168.20.1:2181,192.168.20.2:2181";

            int sessionTimeOutInMs = 15 * 1000; // 15 secs
            int connectionTimeOutInMs = 10 * 1000; // 10 secs

            zkClient = new ZkClient(zookeeperHosts, sessionTimeOutInMs, connectionTimeOutInMs, ZKStringSerializer$.MODULE$);

            boolean isSecureKafkaCluster = false;
            zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), isSecureKafkaCluster);

            int noOfPartitions = 1;
            int noOfReplication = 1;
            Properties topicConfiguration = new Properties();

            AdminUtils.createTopic(zkUtils, topicName, noOfPartitions, noOfReplication, topicConfiguration, RackAwareMode.Enforced$.MODULE$);

            if (zkClient != null)
            {
                zkClient.close();
            }
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally
        {
            if (zkClient != null)
            {
                zkClient.close();
            }
        }
    }

    public static boolean deleteTopic(String topicName)
    {
        ZkClient zkClient = null;
        ZkUtils zkUtils = null;
        try
        {
            String zookeeperHosts = AppConfiguration.getZookeeperURL(); // If multiple zookeeper then -> String zookeeperHosts = "192.168.20.1:2181,192.168.20.2:2181";

            int sessionTimeOutInMs = 15 * 1000; // 15 secs
            int connectionTimeOutInMs = 10 * 1000; // 10 secs

            zkClient = new ZkClient(zookeeperHosts, sessionTimeOutInMs, connectionTimeOutInMs, ZKStringSerializer$.MODULE$);

            boolean isSecureKafkaCluster = false;
            zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), isSecureKafkaCluster);

            AdminUtils.deleteTopic(zkUtils, topicName);

            if (zkClient != null)
            {
                zkClient.close();
            }
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally
        {
            if (zkClient != null)
            {
                zkClient.close();
            }
        }
    }
}
