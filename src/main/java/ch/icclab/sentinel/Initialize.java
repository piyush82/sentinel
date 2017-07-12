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

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
public class Initialize {
    final static Logger logger = Logger.getLogger(Initialize.class);
    static String[] tables = {"user", "space", "series"};
    public static HashMap<String, String> tableInitScripts = new HashMap<String, String>();

    static void prepareDbInitScripts()
    {
        if(AppConfiguration.getSentinelDBType().equalsIgnoreCase("sqlite") || AppConfiguration.getSentinelDBType().equalsIgnoreCase("sqlite3"))
        {
            tableInitScripts.put("user", "create table user (id INTEGER PRIMARY KEY AUTOINCREMENT, login VARCHAR(64), passwordhash VARCHAR(128), apikey VARCHAR(128))");
            tableInitScripts.put("space", "create table space (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32), queryuser VARCHAR(32), querypass VARCHAR(32), userid INT)");
            tableInitScripts.put("series", "create table series (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32), structure VARCHAR(512), spaceid INT)");
        }
        logger.info("Table initialization scripts have been initialized.");
    }

    static boolean isDbValid()
    {
        ArrayList<String> tablesFound = SqlDriver.getDbTablesList();
        for(int i=0; i < tables.length; i++)
        {
            String candidate = tables[i];
            if(!tablesFound.contains(candidate))
            {
                logger.info("The following table: {" + candidate + "} was not found in the existing database!");
                return false;
            }
        }
        return true;
    }

    static boolean initializeDb()
    {
        Connection con = SqlDriver.getDBConnection();
        try {
            Statement statement = con.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            for (int i=0; i < tables.length; i++)
            {
                statement.executeUpdate("drop table if exists " + tables[i]);
                statement.executeUpdate(tableInitScripts.get(tables[i]));
                logger.info("(Re)Created table: " + tables[i]);
            }
            logger.info("Database (re)initialized successfully!");
            con.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            logger.error("Exception caught while initializing sentinel sql database.");
            return false;
        }
        return true;
    }

}
