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

import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.plugin.jdbc.define.StatementEnhanceInfos;
import org.apache.skywalking.apm.plugin.jdbc.trace.ConnectionInfo;

/**
 * @author zhang xin
 */
public class PreparedStatementExecuteMethodsInterceptor implements InstanceMethodsAroundInterceptor {

	@Override
	public final void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
		StatementEnhanceInfos cacheObject = (StatementEnhanceInfos) objInst.getSkyWalkingDynamicField();
		if (cacheObject != null && cacheObject.getConnectionInfo() != null) {
			ConnectionInfo connectInfo = cacheObject.getConnectionInfo();

			AbstractSpan span = ContextManager.createExitSpan(
					buildOperationName(connectInfo, method.getName(), cacheObject.getStatementName()),
					connectInfo.getDatabasePeer());
			Tags.DB_TYPE.set(span, "sql");
			Tags.DB_INSTANCE.set(span, connectInfo.getDatabaseName());
			Tags.DB_STATEMENT.set(span, cacheObject.getSql());
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
			System.out.println("啦啦啦");
			for(int i=0;i<allArguments.length;++i) {
				System.out.println(argumentsTypes[i].getName()+" "+allArguments[i]);
			}
			System.out.println("看看cacheObject");
			System.out.println("sql:"+cacheObject.getSql());
			for(int i=0;i<cacheObject.getParameters().length;++i) {
				System.out.println("参数："+cacheObject.getParameters()[i]);
			}
			
			String s = "[timestamp=" + System.currentTimeMillis() + "]" + "[connId="
					+ cacheObject.getConnectionInfo().getComponent().getId() + "]" + "[sql=" + (String) allArguments[0]
					+ "]";
			if (ret instanceof ResultSet) {
				ResultSet rs = ((ResultSet) ret);
				if (rs != null) {
					while (rs.next()) {
						s = s + "[res=";
						for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; ++i) {
							s = s + rs.getString(i);
							if (i != rs.getMetaData().getColumnCount()) {
								s = s + ",";
							}
						}
						s = s + "]";
					}
					rs.beforeFirst();
				}
			}
			System.out.println(s);

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
