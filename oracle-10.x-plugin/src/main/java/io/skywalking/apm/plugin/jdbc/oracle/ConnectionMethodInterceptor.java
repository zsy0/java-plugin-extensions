package io.skywalking.apm.plugin.jdbc.oracle;

import java.lang.reflect.Method;

import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.plugin.jdbc.trace.ConnectionInfo;

import io.skywalking.apm.plugin.jdbc.oracle.define.ConnectionInstrumentation;

public class ConnectionMethodInterceptor implements InstanceMethodsAroundInterceptor {

	@Override
	public final void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
		ConnectionInfo connectInfo = (ConnectionInfo) objInst.getSkyWalkingDynamicField();
		if (connectInfo != null) {
			AbstractSpan span = ContextManager.createExitSpan(
					connectInfo.getDBType() + "/JDBI/Connection/" + method.getName(), connectInfo.getDatabasePeer());
			Tags.DB_TYPE.set(span, "sql");
			Tags.DB_INSTANCE.set(span, connectInfo.getDatabaseName());
			Tags.DB_STATEMENT.set(span, "");
			span.setComponent(connectInfo.getComponent());
			SpanLayer.asDB(span);
		}
	}

	@Override
	public final Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Object ret) throws Throwable {
		ConnectionInfo connectInfo = (ConnectionInfo) objInst.getSkyWalkingDynamicField();
		if (connectInfo != null && allArguments.length == 0 && objInst instanceof ConnectionInstrumentation.T4C_CONNECTION_CLASS) {
//			Exception e = new Exception();
//			e.printStackTrace();
			ContextManager.stopSpan();
			if (method.getName().equals("commit") || method.getName().equals("rollback")) {
				String s = "[timestamp=" + System.currentTimeMillis() + "]" + "[connId="
						+ connectInfo.getComponent().getId() + "]" + "[sql=" + method.getName() + "]";
				System.out.println(s);
			}
		}
		return ret;
	}

	@Override
	public final void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Throwable t) {
		ContextManager.activeSpan().errorOccurred().log(t);
	}

}
