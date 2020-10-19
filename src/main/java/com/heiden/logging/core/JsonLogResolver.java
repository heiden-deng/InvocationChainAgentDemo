/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.heiden.logging.core;

import com.google.gson.Gson;
import com.heiden.logging.api.ILog;
import com.heiden.logging.api.LogResolver;

public class JsonLogResolver implements LogResolver {
    private static final Gson GSON = new Gson();

    @Override
    public ILog getLogger(Class<?> aClass) {
        return new JsonLogger(aClass, GSON);
    }

    @Override
    public ILog getLogger(String s) {
        return new JsonLogger(s, GSON);
    }
}
