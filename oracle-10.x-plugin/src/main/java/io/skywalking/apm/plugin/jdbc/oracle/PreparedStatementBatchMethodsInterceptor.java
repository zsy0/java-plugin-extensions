package io.skywalking.apm.plugin.jdbc.oracle;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.plugin.jdbc.PreparedStatementParameterBuilder;
import org.apache.skywalking.apm.plugin.jdbc.define.StatementEnhanceInfos;

public class PreparedStatementBatchMethodsInterceptor implements InstanceMethodsAroundInterceptor {

	@Override
	public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
			Object ret) throws Throwable {

		StatementEnhanceInfos cacheObject = (StatementEnhanceInfos) objInst.getSkyWalkingDynamicField();
		
		if (cacheObject != null && cacheObject.getConnectionInfo() != null) {
			try {
				if(method.getName().equals("addBatch")) {
					cacheObject.addParaBatch();
				}else if(method.getName().equals("executeBatch")) {
					String s = "[timestamp=" + System.currentTimeMillis() + "]" + "[connId="
							+ cacheObject.getConnectionInfo().getConnId() + "]" + "[sql=" + cacheObject.getSql()
							+ "]";
					List<Object> allParameters = cacheObject.getAllParameters();
					List<Integer> allMaxIndexes = cacheObject.getAllMaxIndexes();
					for(int i=0;i<allParameters.size();++i) {
						String para = new PreparedStatementParameterBuilder().setParameters((Object[]) allParameters.get(i))
								.setMaxIndex(allMaxIndexes.get(i)).build();
						s += "[para=" + para + "]";
					}
//					System.out.println(s);
					System.out.println("1:18");
					System.out.println("Constants.logger"+Constants.PREPARED_STATEMENT_INTERCEPT_CLASS);
					System.out.println("Constants.logger"+Constants.logger.toString());
					Constants.logger.info(s);
					System.out.println("Constants.logger");
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override
	public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
			MethodInterceptResult result) throws Throwable {
		
	}

	@Override
	public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Throwable t) {
		
	}

}
