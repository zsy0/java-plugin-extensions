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
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.plugin.jdbc.define.StatementEnhanceInfos;
import org.apache.skywalking.apm.plugin.jdbc.trace.ConnectionInfo;

public class StatementExecuteMethodsInterceptor implements InstanceMethodsAroundInterceptor {
	@Override
	public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
			MethodInterceptResult result) throws Throwable {
		StatementEnhanceInfos cacheObject = (StatementEnhanceInfos) objInst.getSkyWalkingDynamicField();
		if (cacheObject != null && cacheObject.getConnectionInfo() != null) {
			ConnectionInfo connectInfo = cacheObject.getConnectionInfo();

			AbstractSpan span = ContextManager.createExitSpan(
					buildOperationName(connectInfo, method.getName(), cacheObject.getStatementName()),
					connectInfo.getDatabasePeer());
			Tags.DB_TYPE.set(span, "sql");
			Tags.DB_INSTANCE.set(span, connectInfo.getDatabaseName());

			String sql = "";
			if (allArguments.length > 0) {
				sql = (String) allArguments[0];
			}

			Tags.DB_STATEMENT.set(span, sql);
			span.setComponent(connectInfo.getComponent());

			SpanLayer.asDB(span);
		}
	}

	@Override
	public final Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Object ret) throws Throwable {
		StatementEnhanceInfos cacheObject = (StatementEnhanceInfos) objInst.getSkyWalkingDynamicField();
		if (cacheObject != null && cacheObject.getConnectionInfo() != null) {
			ContextManager.stopSpan();
		}
		System.out.println("55555555555555555555555555");
		if (cacheObject == null) {
			System.out.println("cacheObject==null");
		} else if (cacheObject.getConnectionInfo() == null) {
			System.out.println("cacheObject.getConnectionInfo() == null");
		} else {
			try {
				System.out.println(
						"[connId=" + cacheObject.getConnectionInfo().getComponent().getId() + "]"
								+ cacheObject.getSql() + " "
								+cacheObject.getStatementName());
				for (int i = 0; i < cacheObject.getParameters().length; ++i) {
					System.out.println(cacheObject.getParameters()[i]);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return ret;
	}

	@Override
	public final void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Throwable t) {
		StatementEnhanceInfos cacheObject = (StatementEnhanceInfos) objInst.getSkyWalkingDynamicField();
		if (cacheObject.getConnectionInfo() != null) {
			ContextManager.activeSpan().errorOccurred().log(t);
		}
	}

	private String buildOperationName(ConnectionInfo connectionInfo, String methodName, String statementName) {
		return connectionInfo.getDBType() + "/JDBI/" + statementName + "/" + methodName;
	}
}
