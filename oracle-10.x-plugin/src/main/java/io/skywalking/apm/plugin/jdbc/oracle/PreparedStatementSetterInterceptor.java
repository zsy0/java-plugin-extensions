package io.skywalking.apm.plugin.jdbc.oracle;

import java.lang.reflect.Method;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.plugin.jdbc.define.StatementEnhanceInfos;

public class PreparedStatementSetterInterceptor implements InstanceMethodsAroundInterceptor {
	public final void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
		StatementEnhanceInfos statementEnhanceInfos = (StatementEnhanceInfos) objInst.getSkyWalkingDynamicField();
		int index = ((Integer) allArguments[0]).intValue();
		Object parameter = allArguments[1];
		statementEnhanceInfos.setParameter(index, parameter);
		System.out.println("来了来了来了");
	}

	public final Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Object ret) throws Throwable {
		return ret;
	}

	public final void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Throwable t) {
	}
}