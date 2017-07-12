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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by piyush on 7/11/17.
 */
public class HelperMethodsTest
{
    @Test
    public void testSHA256generation()
    {
        assertEquals("generates hash for 'test'", "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",
                HelperMethods.generateSHA256Hash("test"));
    }

    @Test
    public void testJsonValidator()
    {
        assertTrue(HelperMethods.isJSONValid("{\"username\":\"piyush\"}"));
    }

    @Test
    public void testRandomStringGeneration()
    {
        assertNotNull(HelperMethods.randomString(10));
    }
}
