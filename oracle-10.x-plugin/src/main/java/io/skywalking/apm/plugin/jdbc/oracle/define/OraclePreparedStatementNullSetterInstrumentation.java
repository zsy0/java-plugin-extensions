package io.skywalking.apm.plugin.jdbc.oracle.define;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;

import zsy.org.apache.skywalking.apm.plugin.jdbc.JDBCPreparedStatementNullSetterInstanceMethodsInterceptPoint;

public class OraclePreparedStatementNullSetterInstrumentation extends OraclePrepareStatementInstrumentation {
	@Override
	public final InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
		return new InstanceMethodsInterceptPoint[] {
				new JDBCPreparedStatementNullSetterInstanceMethodsInterceptPoint() };
	}

}
