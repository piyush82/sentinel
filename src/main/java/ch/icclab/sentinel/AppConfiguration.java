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
import java.sql.*;
import static org.jooq.impl.DSL.*;

@Component
public class AppConfiguration {
    final static Logger logger = Logger.getLogger(AppConfiguration.class);

    @Value("${stream.db.adminuser}")
    String streamDbAdminUser;

    @Value("${stream.db.adminpass}")
    String streamDbAdminPass;

    @Value("${kafka.endpoint}")
    String kafka;

    @Value("${zookeeper.endpoint}")
    String zookeeper;

    @Value("${stream.db.type}")
    String streamDbType;

    @Value("${stream.db.endpoint}")
    String streamDbUrl;

    @Value("${sentinel.db.type}")
    String sentinelDbType;

    @Value("${sentinel.db.endpoint}")
    String sentinelDbUrl;

    @Value("${topic.check.interval}")
    long topicwaitperiod;


    private static String streamDBUser;
    private static String streamDBPass;
    private static String KafkaURL;
    private static String ZookeeperURL;
    private static String streamDBType;
    private static String streamDBURL;
    private static String sentinelDBType;
    private static String sentinelDBURL;
    private static long topicCheckWaitingPeriod;


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

    @PostConstruct
    public void init() {
        streamDBUser = streamDBUser;
        streamDBPass = streamDBPass;
        streamDBType = streamDBType;
        streamDBURL = streamDBURL;
        sentinelDBType = sentinelDBType;
        sentinelDBURL = sentinelDBURL;
        KafkaURL = kafka;
        ZookeeperURL = zookeeper;
        topicCheckWaitingPeriod = topicwaitperiod;
    }

}
