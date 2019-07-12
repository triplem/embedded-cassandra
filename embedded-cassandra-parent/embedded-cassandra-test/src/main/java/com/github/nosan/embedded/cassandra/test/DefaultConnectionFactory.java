/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nosan.embedded.cassandra.test;

import java.util.Objects;

import com.github.nosan.embedded.cassandra.Settings;
import com.github.nosan.embedded.cassandra.util.ClassUtils;

/**
 * {@link ConnectionFactory} that creates a {@code Connection} based on the classpath.
 *
 * @author Dmytro Nosan
 * @since 2.0.4
 */
public class DefaultConnectionFactory implements ConnectionFactory {

	private static final String CQL_SESSION_CLASS = "com.datastax.oss.driver.api.core.CqlSession";

	private static final String CLUSTER_CLASS = "com.datastax.driver.core.Cluster";

	@Override
	public Connection create(Settings settings) {
		Objects.requireNonNull(settings, "Settings must not be null");
		ClassLoader classLoader = getClass().getClassLoader();
		if (ClassUtils.isPresent(CQL_SESSION_CLASS, classLoader)) {
			return new CqlSessionConnectionFactory().create(settings);
		}
		if (ClassUtils.isPresent(CLUSTER_CLASS, classLoader)) {
			return new ClusterConnectionFactory().create(settings);
		}
		throw new IllegalStateException(
				String.format("Can not create a Connection. Both '%s' and '%s' classes "
						+ "are not present in the classpath.", CQL_SESSION_CLASS, CLUSTER_CLASS));
	}

}
