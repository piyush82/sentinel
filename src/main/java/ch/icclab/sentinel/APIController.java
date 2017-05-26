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

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedList;

@RestController
public class APIController {

    @RequestMapping(value = {"/api/", "/apis/", "/apis", "/api"}, method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody
    ResponseEntity getApis()
    {
        KafkaClient.listTopics();
        LinkedList<APIEndpoints> value = new LinkedList<>();
        APIEndpoints value1 = new APIEndpoints();
        value1.endpoint = "/api/space/"; //each space corresponds to an individual db and vectors in a space make for tables in a db
        value1.method = "GET";
        value1.description = "list of monitored spaces";
        value1.contentType = "application/json";
        value.add(value1);
        APIEndpoints value2 = new APIEndpoints();
        value2.endpoint = "/api/space/";
        value2.method = "POST";
        value2.description = "register / create a new monitored space";
        value2.contentType = "application/json";
        value.add(value2);
        APIEndpoints value3 = new APIEndpoints();
        value3.endpoint = "/api/";
        value3.method = "GET";
        value3.description = "get list of all supported APIs";
        value3.contentType = "application/json";
        value.add(value3);
        Gson gson = new Gson();
        String jsonInString = gson.toJson(value);
        return ResponseEntity.status(HttpStatus.OK).body(jsonInString);
    }

    @RequestMapping(value={"/api/tenants/"}, method = RequestMethod.POST, produces = {"application/json"})
    public @ResponseBody
    String createTenant()
    {
        return "";
    }

}
