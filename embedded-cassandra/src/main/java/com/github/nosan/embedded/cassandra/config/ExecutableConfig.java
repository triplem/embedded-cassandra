/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nosan.embedded.cassandra.config;

import java.time.Duration;
import java.util.List;

import com.github.nosan.embedded.cassandra.customizer.FileCustomizer;
import de.flapdoodle.embed.process.config.IExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * An Embedded Cassandra {@link IExecutableProcessConfig executable config}.
 *
 * @author Dmytro Nosan
 * @see ExecutableConfigBuilder
 */
public interface ExecutableConfig extends IExecutableProcessConfig {

	/**
	 * Retrieves an embedded cassandra config.
	 * @return Cassandra config.
	 */
	Config getConfig();

	/**
	 * Retrieves an embedded cassandra startup timeout.
	 * @return Cassandra startup timeout.
	 */
	Duration getTimeout();

	/**
	 * Retrieves an embedded cassandra {@link FileCustomizer}s.
	 * @return Cassandra File customizers.
	 *
	 * @see com.github.nosan.embedded.cassandra.customizer.FileCustomizer
	 */
	List<FileCustomizer> getFileCustomizers();

	/**
	 * Retrieves the cassandra version.
	 * @return cassandra version
	 */
	Version getVersion();

	/**
	 * Retrieves an embedded cassandra JMX Port.
	 * @return Cassandra JMX_PORT.
	 */
	int getJmxPort();

	/**
	 * Retrieves an embedded cassandra JVM Options.
	 * @return Cassandra JVM Options.
	 */
	List<String> getJvmOptions();

	/**
	 * Retrieves the cassandra version.
	 * @return cassandra version
	 */
	@Override
	default IVersion version() {
		return getVersion();
	}

	/**
	 * Retrieves support config.
	 * @return {@link ISupportConfig} support config.
	 * @see SupportConfig
	 */
	@Override
	default ISupportConfig supportConfig() {
		return new SupportConfig();
	}

}