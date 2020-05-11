package io.skywalking.apm.plugin.jdbc.oracle.define;

import static net.bytebuddy.matcher.ElementMatchers.named;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;

import io.skywalking.apm.plugin.jdbc.oracle.Constants;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 
 * @author Shuyan Zhang
 *
 */
public class OraclePreparedStatementSetterInstrumentation extends OraclePrepareStatementInstrumentation {
	
	@Override
	public final InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
		return new InstanceMethodsInterceptPoint[] { 
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("setArray")
                                .or(named("setBigDecimal"))
                                .or(named("setBoolean"))
                                .or(named("setByte"))
                                .or(named("setDate"))
                                .or(named("setDouble"))
                                .or(named("setFloat"))
                                .or(named("setInt"))
                                .or(named("setLong"))
                                .or(named("setNString"))
                                .or(named("setObject"))
                                .or(named("setRowId"))
                                .or(named("setShort"))
                                .or(named("setString"))
                                .or(named("setTime"))
                                .or(named("setTimestamp"))
                                .or(named("setURL"));
			}

			@Override
			public String getMethodsInterceptor() {
				return Constants.PREPARED_STATEMENT_SETTER_METHODS_INTERCEPTOR;
			}

			@Override
			public boolean isOverrideArgs() {
				return false;
			}
		}

		};
	}

}
