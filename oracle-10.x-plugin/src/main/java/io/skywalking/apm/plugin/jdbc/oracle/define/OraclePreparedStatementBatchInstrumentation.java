package io.skywalking.apm.plugin.jdbc.oracle.define;

import static net.bytebuddy.matcher.ElementMatchers.named;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;

import io.skywalking.apm.plugin.jdbc.oracle.Constants;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class OraclePreparedStatementBatchInstrumentation extends OraclePrepareStatementInstrumentation {
	
	@Override
	public final InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
		return new InstanceMethodsInterceptPoint[] { 
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("addBatch")
                                .or(named("executeBatch"));
			}

			@Override
			public String getMethodsInterceptor() {
				return Constants.PREPARED_STATEMENT_BATCH_METHODS_INTERCEPTOR;
			}

			@Override
			public boolean isOverrideArgs() {
				return false;
			}
		}

		};
	}

}
