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

import ch.icclab.sentinel.cache.ListElement;
import ch.icclab.sentinel.dao.SentinelDockerStatsAgent;
import ch.icclab.sentinel.dao.SentinelDockerStatsAgentMetric;
import ch.icclab.sentinel.dao.SentinelDockerStatsAgentValue;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static ch.icclab.sentinel.Application.msgFormatCache;

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
public class InfluxDBClient
{
    final static Logger logger = Logger.getLogger(InfluxDBClient.class);
    private static InfluxDB influxDB;

    static boolean init()
    {
        if(AppConfiguration.getStreamDBType().equalsIgnoreCase("influxdb")) {
            try {
                createAdminUser();
                influxDB = InfluxDBFactory.connect("http://" + AppConfiguration.getStreamDBURL(), AppConfiguration.getStreamDBUser(), AppConfiguration.getStreamDBPass());
                //influxDB.enableBatch(100, 100, TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                influxDB = null;
                logger.warn("Exception caught in InfluxDBClient init method: " + ex.getLocalizedMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    static boolean createAdminUser()
    {
        //curl "http://localhost:8086/query" --data-urlencode "q=CREATE USER root WITH PASSWORD 'root' WITH ALL PRIVILEGES"
        try
        {
            String url = "http://" + AppConfiguration.getStreamDBURL() + "/query";
            String query = "CREATE USER \"" + AppConfiguration.getStreamDBUser() + "\" WITH PASSWORD '" + AppConfiguration.getStreamDBPass() + "' WITH ALL PRIVILEGES";
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("q", query)
                    .build();
            Request request = new Request.Builder().url(url).addHeader("Content-Type", "application/x-www-form-urlencoded").
                    post(body).build();
            Response response = client.newCall(request).execute();
            if(response.code() != 401)
                logger.info("register admin user::response code: " + response.code());
            else
                logger.warn("can not create admin-user, maybe it already exists: " + response.code());
            response.body().close();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    static boolean addUser(String topic, String user, String password)
    {
        //curl "http://localhost:8086/query" --data-urlencode "q=CREATE USER root WITH PASSWORD 'root' WITH ALL PRIVILEGES"
        try
        {
            String url = "http://" + AppConfiguration.getStreamDBURL() + "/query?u=" + AppConfiguration.getStreamDBUser() + "&p=" + AppConfiguration.getStreamDBPass();
            String query = "CREATE USER \"" + user + "\" WITH PASSWORD '" + password + "'";
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("q", query)
                    .build();

            Request request = new Request.Builder().url(url).addHeader("Content-Type", "application/x-www-form-urlencoded").
                    post(body).build();
            Response response = client.newCall(request).execute();
            logger.info("Create influxdb user::response code: " + response.code());
            response.body().close();

            //now proceed to grant read,write, all [GRANT [READ,WRITE,ALL] ON <database_name> TO <username>]
            query = "GRANT READ ON \"" + topic + "\" TO \"" + user + "\"";
            body = new FormBody.Builder()
                    .add("q", query)
                    .build();
            request = new Request.Builder().url(url).addHeader("Content-Type", "application/x-www-form-urlencoded").
                    post(body).build();
            response = client.newCall(request).execute();
            logger.info("grant db privileges to user::response code: " + response.code());
            response.body().close();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static boolean addDB(String topic)
    {
        if(influxDB != null) {
            try {
                String dbName = topic;
                influxDB.createDatabase(dbName);
                return true;
            } catch (Exception ex) {
                logger.warn("Exception caught in InfluxDBClient addDB method: " + ex.getLocalizedMessage());
                return false;
            }
        }
        return false;
    }

    static boolean removeDB(String topic)
    {
        if(influxDB != null) {
            try {
                String dbName = topic;
                influxDB.deleteDatabase(dbName);
                return true;
            } catch (Exception ex) {
                logger.warn("Exception caught in InfluxDBClient removeDB method: " + ex.getLocalizedMessage());
                return false;
            }
        }
        return false;
    }

    static boolean addPoint(String topic, String key, String msg)
    {
        if(key == null || key.trim().length() == 0) key = "default";
        if(influxDB != null)
        {
            if(topic.equalsIgnoreCase("zane-sensor-data") && key.equalsIgnoreCase("id-00001"))
            {
                String[] msgParts = msg.split(" ");
                Double sourceTime = new Double(Double.parseDouble(msgParts[0]));
                Point point1 = Point.measurement(key)
                        .time(sourceTime.longValue(), TimeUnit.SECONDS)
                        .addField("CO", Float.parseFloat(msgParts[1].split("=")[1]))
                        .addField("LPG", Float.parseFloat(msgParts[2].split("=")[1]))
                        .addField("SMOKE", Float.parseFloat(msgParts[3].split("=")[1]))
                        .build();

                influxDB.write(topic, "autogen", point1);
            }
            else
            {
                Point.Builder builder = Point.measurement(key);
                //retrieving the msg format for the series
                LinkedList<ListElement> formatElements = Application.msgFormatCache.getSeriesSignature(topic, key);
                if(formatElements == null)
                {
                    logger.warn("Failed to add data point due to error in getting series format string");
                    return false;
                }
                //handle cases: if msg is json or msg is string
                if(HelperMethods.isJSONValid(msg))
                {
                    JSONParser parser = new JSONParser();
                    try
                    {
                        JSONObject json = (JSONObject) parser.parse(msg);
                        String agentType = "";
                        for (Object jsonKey:json.keySet())
                        {
                            if(((String)(jsonKey)).equalsIgnoreCase("agent"))
                            {
                                agentType = (String)json.get("agent");
                            }
                        }
                        if(agentType.equalsIgnoreCase("sentinel-docker-agent"))
                        {
                            Gson gson = new Gson();
                            SentinelDockerStatsAgent data = gson.fromJson(msg, SentinelDockerStatsAgent.class);

                            for(SentinelDockerStatsAgentValue value: data.values)
                            {
                                builder = Point.measurement(key);
                                builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                                builder.addField("host", data.host);
                                Double sourceTime = new Double(Double.parseDouble(data.unixtime));
                                builder.addField("agent-time", sourceTime.longValue());
                                builder.addField("container-id", value.id);
                                builder.addField("container-name", value.name);
                                for(SentinelDockerStatsAgentMetric metric: value.metrics)
                                {
                                    builder.addField(metric.key, metric.value);
                                }
                                Point point1 = builder.build();
                                influxDB.write(topic, "autogen", point1);
                            }
                            return true;
                        }
                    }
                    catch (Exception ex)
                    {
                        logger.warn("Exception caught in parssing json: " + ex.getLocalizedMessage());
                        return false;
                    }
                }
                else
                {
                    String[] msgParts = msg.split(" ");
                    if(formatElements.get(0).name.equalsIgnoreCase("unixtime"))
                    {
                        Double sourceTime = new Double(Double.parseDouble(msgParts[0].split(":")[1]));
                        if(formatElements.get(0).type.equalsIgnoreCase("s"))
                        {
                            builder.time(sourceTime.longValue(), TimeUnit.SECONDS);
                        }
                        else if(formatElements.get(0).type.equalsIgnoreCase("ms"))
                        {
                            builder.time(sourceTime.longValue(), TimeUnit.MILLISECONDS);
                        }
                        else if(formatElements.get(0).type.equalsIgnoreCase("ns"))
                        {
                            builder.time(sourceTime.longValue(), TimeUnit.NANOSECONDS);
                        }
                        else if(formatElements.get(0).type.equalsIgnoreCase("us"))
                        {
                            builder.time(sourceTime.longValue(), TimeUnit.MICROSECONDS);
                        }
                    }
                    else
                    {
                        //assume time unit is missing
                        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    }

                    int elementPlace = 0;
                    for(ListElement ele:formatElements)
                    {
                        elementPlace++;
                        if(ele.name.equalsIgnoreCase("unixtime") && elementPlace == 1) continue;
                        String[] msgSubparts = msgParts[elementPlace-1].split(":");
                        switch(ele.type)
                        {
                            case "bool":
                                break;
                            case "string":
                            case "json":
                                String toStore = msgSubparts[1];
                                toStore = toStore.replaceAll("_", " ");
                                toStore = toStore.replaceAll("@", ":");
                                builder.addField(msgSubparts[0], toStore);
                                break;
                            case "int":
                            case "long":
                                builder.addField(msgSubparts[0], Long.parseLong(msgSubparts[1]));
                                break;
                            case "float":
                            case "double":
                                builder.addField(msgSubparts[0], Float.parseFloat(msgSubparts[1]));
                                break;
                        }
                    }
                }

                Point point1 = builder.build();
                influxDB.write(topic, "autogen", point1);
            }
            return true;
        }
        return false;
    }
}
