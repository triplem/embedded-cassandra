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

package com.github.nosan.embedded.cassandra;

import com.datastax.driver.core.Cluster;

/**
 * Strategy to create {@link Cluster}.
 *
 * @author Dmytro Nosan
 * @see DefaultClusterFactory
 */
public interface ClusterFactory {
	/**
	 * Creates a new {@link Cluster}.
	 *
	 * @param config Cassandra's config.
	 * @param version Cassandra's version.
	 * @return a {@link Cluster} to Cassandra.
	 */
	Cluster getCluster(Config config, Version version);
}