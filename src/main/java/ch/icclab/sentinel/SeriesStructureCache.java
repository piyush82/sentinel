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

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */

import ch.icclab.sentinel.SqlDriver;
import ch.icclab.sentinel.cache.HashElement;
import ch.icclab.sentinel.cache.ListElement;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;

public class SeriesStructureCache
{
    final static Logger logger = Logger.getLogger(SeriesStructureCache.class);
    HashMap<String, HashMap<String, HashElement>> seriesCache;

    SeriesStructureCache(int size)
    {
        seriesCache = new HashMap<>(size);
    }

    LinkedList<ListElement> getSeriesSignature(String topic, String key)
    {
        try
        {
            String[] topicParts = topic.split("-");
            int userId = Integer.parseInt(topicParts[1]);
            String space = topicParts[2];
            //check if the series is valid for the space?

            if (seriesCache.get(topic) == null)
            {
                if(SqlDriver.getSpaceId(userId, space) != -1)
                    seriesCache.put(topic, new HashMap<>());
                else
                {
                    logger.warn("Space does not belong to user - possible threat case");
                    return null;
                }
            }

            if (seriesCache.get(topic).get(key) == null)
            {
                if(SqlDriver.getSeriesId(key, SqlDriver.getSpaceId(userId, space)) != -1) {
                    HashElement temp = new HashElement();
                    temp.isDirty = true;
                    temp.elements = new LinkedList<>();
                    seriesCache.get(topic).put(key, temp);
                }
                else
                {
                    logger.warn("Series does not belong to space - possible threat case");
                    return null;
                }
            }

            if (seriesCache.get(topic).get(key).isDirty)
            {
                logger.info("cache miss - going to the db");
                //refresh
                int spaceId = SqlDriver.getSpaceId(userId, space);
                if(spaceId == -1) return null;
                String msgFormat = SqlDriver.getSeriesMsgFormat(key, spaceId);
                if(msgFormat == null) return null;
                //now recreating the updated cache entry
                String[] parts = msgFormat.split(" ");
                seriesCache.get(topic).get(key).elements = new LinkedList<>();
                for (String part : parts) {
                    if (part == null || part.trim().length() == 0) continue;
                    ListElement element = new ListElement();
                    String[] subPart = part.trim().split(":");
                    element.name = subPart[0];
                    element.type = subPart[1];
                    seriesCache.get(topic).get(key).elements.add(element);
                }
                seriesCache.get(topic).get(key).isDirty = false;
            }
            return seriesCache.get(topic).get(key).elements;
        }
        catch (Exception ex)
        {
            logger.warn("Exception is locating series signature: " + ex.getLocalizedMessage());
            return null;
        }
    }
}
