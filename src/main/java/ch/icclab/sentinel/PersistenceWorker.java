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

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
public class PersistenceWorker implements Runnable
{
    final static Logger logger = Logger.getLogger(PersistenceWorker.class);
    private String topic;
    private String key;
    private Long msgOffset;
    private String value;

    public PersistenceWorker(String t, String k, Long o, String v)
    {
        topic = t;
        key = k;
        msgOffset = o;
        value = v;
    }

    @Override
    public void run()
    {
        storeMsg();
    }

    private void storeMsg()
    {
        try
        {
            //get series format from DB entry for the
            logger.info(Thread.currentThread().getName() + " got: [Topic=" + topic + ", Offset=" + msgOffset + ", Key=" + key + ", Value=" + value + "]");
            if(AppConfiguration.getStreamDBType().equalsIgnoreCase("influxdb"))
                InfluxDBClient.addPoint(topic, key, value);
            //Thread.sleep(5000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
