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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class AppConfiguration {
    final static Logger logger = Logger.getLogger(AppConfiguration.class);

    @Value("${stream.adminuser}")
    String streamDbAdminUser;

    @Value("${stream.adminpass}")
    String streamDbAdminPass;

    @Value("${stream.accessurl}")
    String streamaccessurl;

    @Value("${kafka.endpoint}")
    String kafka;

    @Value("${zookeeper.endpoint}")
    String zookeeper;

    @Value("${stream.dbtype}")
    String streamDbType;

    @Value("${stream.dbendpoint}")
    String streamDbUrl;

    @Value("${sentinel.db.type}")
    String sentinelDbType;

    @Value("${sentinel.db.endpoint}")
    String sentinelDbUrl;

    @Value("${topic.check.interval}")
    long topicwaitperiod;

    @Value("${series.format.cache.size}")
    int sFormatCSize;

    @Value("${admin.token}")
    String adminPass;

    @Value("${published.api.version}")
    String apiV;

    @Value("${kafka.key.serializer}")
    String kafkakeyserializer;

    @Value("${kafka.value.serializer}")
    String kafkavalueserializer;


    private static String streamDBUser;
    private static String streamDBPass;
    private static String KafkaURL;
    private static String ZookeeperURL;
    private static String streamDBType;
    private static String streamDBURL;
    private static String sentinelDBType;
    private static String sentinelDBURL;
    private static long topicCheckWaitingPeriod;
    private static String adminToken;
    private static int seriesFormatCacheSize;
    private static String publishedApiVersion;
    private static String kafkaKeySerializer;
    private static String kafkaValueSerializer;
    private static String streamAccessUrl;


    public static String getStreamDBUser()
    {
        return streamDBUser;
    }

    public static String getStreamDBPass()
    {
        return streamDBPass;
    }

    public static String getStreamDBType()
    {
        return streamDBType;
    }

    public static String getStreamDBURL()
    {
        return streamDBURL;
    }

    public static String getStreamAccessUrl()
    {
        return streamAccessUrl;
    }

    public static String getSentinelDBType()
    {
        return sentinelDBType;
    }

    public static String getSentinelDBURL()
    {
        return sentinelDBURL;
    }

    public static String getKafkaURL()
    {
        return KafkaURL;
    }

    public static String getZookeeperURL()
    {
        return ZookeeperURL;
    }

    public static long getTopicCheckWaitingPeriod()
    {
        return topicCheckWaitingPeriod;
    }

    public static String getAdminToken()
    {
        return adminToken;
    }

    public static int getSeriesFormatCacheSize()
    {
        return seriesFormatCacheSize;
    }

    public static String getPublishedApiVersion()
    {
        return publishedApiVersion;
    }

    public static String getKafkaKeySerializer()
    {
        return kafkaKeySerializer;
    }

    public static String getKafkaValueSerializer()
    {
        return kafkaValueSerializer;
    }

    @PostConstruct
    public void init() {
        Map<String, String> env = System.getenv();
        if(env.containsKey("STREAM_ADMINUSER"))
            streamDBUser = env.get("STREAM_ADMINUSER");
        else
            streamDBUser = streamDbAdminUser;
        if(env.containsKey("STREAM_ADMINPASS"))
            streamDBPass = env.get("STREAM_ADMINPASS");
        else
            streamDBPass = streamDbAdminPass;
        if(env.containsKey("STREAM_ACCESSURL"))
            streamAccessUrl = env.get("STREAM_ACCESSURL");
        else
            streamAccessUrl = streamaccessurl;
        if(env.containsKey("KAFKA_ENDPOINT"))
            KafkaURL = env.get("KAFKA_ENDPOINT");
        else
            KafkaURL = kafka;
        if(env.containsKey("ZOOKEEPER_ENDPOINT"))
            ZookeeperURL = env.get("ZOOKEEPER_ENDPOINT");
        else
            ZookeeperURL = zookeeper;
        if(env.containsKey("STREAM_DBTYPE"))
            streamDBType = env.get("STREAM_DBTYPE");
        else
            streamDBType = streamDbType;
        if(env.containsKey("STREAM_DBENDPOINT"))
            streamDBURL = env.get("STREAM_DBENDPOINT");
        else
            streamDBURL = streamDbUrl;
        if(env.containsKey("SENTINEL_DB_TYPE"))
            sentinelDBType = env.get("SENTINEL_DB_TYPE");
        else
            sentinelDBType = sentinelDbType;
        if(env.containsKey("SENTINEL_DB_ENDPOINT"))
            sentinelDBURL = env.get("SENTINEL_DB_ENDPOINT");
        else
            sentinelDBURL = sentinelDbUrl;
        if(env.containsKey("TOPIC_CHECK_INTERVAL"))
            topicCheckWaitingPeriod = Long.parseLong(env.get("TOPIC_CHECK_INTERVAL"));
        else
            topicCheckWaitingPeriod = topicwaitperiod;
        if(env.containsKey("ADMIN_TOKEN"))
            adminToken = env.get("ADMIN_TOKEN");
        else
            adminToken = adminPass;

        seriesFormatCacheSize = sFormatCSize;
        publishedApiVersion = apiV;
        kafkaKeySerializer = kafkakeyserializer;
        kafkaValueSerializer = kafkavalueserializer;

    }

}
