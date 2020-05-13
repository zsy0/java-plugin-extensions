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
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Constants variables
 *
 * @author zhang xin
 */
public final class Constants {
	public static final String STATEMENT_INTERCEPT_CLASS = "io.skywalking.apm.plugin.jdbc.oracle.StatementExecuteMethodsInterceptor";

	public static final String PREPARED_STATEMENT_INTERCEPT_CLASS = "io.skywalking.apm.plugin.jdbc.oracle.PreparedStatementExecuteMethodsInterceptor";

	public static final String PREPARED_STATEMENT_SETTER_METHODS_INTERCEPTOR = "org.apache.skywalking.apm.plugin.jdbc.JDBCPreparedStatementSetterInterceptor";

	public static final String PREPARED_STATEMENT_BATCH_METHODS_INTERCEPTOR = "io.skywalking.apm.plugin.jdbc.oracle.PreparedStatementBatchMethodsInterceptor";
	
	public static final Logger logger = Logger.getLogger(Constants.class); 
	static {
		System.out.println("log4j初始化");
		URL fileURL=Constants.class.getClassLoader().getResource("src/main/resource/log4j.properties"); 
		PropertyConfigurator.configure(fileURL.getFile());
		System.out.println("初始化完毕");
	}
}
