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

package io.skywalking.apm.plugin.jdbc.oracle;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import zsy.org.apache.skywalking.apm.plugin.jdbc.define.StatementEnhanceInfos;
import zsy.org.apache.skywalking.apm.plugin.jdbc.trace.ConnectionInfo;

public class CreatePreparedStatementInterceptor implements InstanceMethodsAroundInterceptor {
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        MethodInterceptResult result) throws Throwable {
    	allArguments[1]=ResultSet.TYPE_SCROLL_INSENSITIVE;
		allArguments[2]=ResultSet.CONCUR_READ_ONLY;
    	
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        Object ret) throws Throwable {
        if (ret instanceof EnhancedInstance) {
            ((EnhancedInstance)ret).setSkyWalkingDynamicField(new StatementEnhanceInfos((ConnectionInfo)objInst.getSkyWalkingDynamicField(), (String)allArguments[0], "PreparedStatement"));
        }
        return ret;
    }

    @Override public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
        Class<?>[] argumentsTypes, Throwable t) {

    }
}
