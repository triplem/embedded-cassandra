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

package com.github.nosan.embedded.cassandra.spring.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.io.Resource;

import com.github.nosan.embedded.cassandra.api.Cassandra;

/**
 * Annotation that allows the {@link Cassandra} to be started and stopped.
 * <p>By default, {@link Cassandra} runs on the random ports.
 *
 * <p>Example:
 * <pre>
 * &#64;EmbeddedCassandra(scripts = "schema.cql")
 * &#64;ExtendWith(SpringExtension.class)
 * class CassandraTests {
 *
 *      &#64;Test
 *      void test() {
 *      }
 * }
 * </pre>
 *
 * @author Dmytro Nosan
 * @since 3.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface EmbeddedCassandra {

	/**
	 * The paths to the CQL scripts to execute. Each path will be interpreted as a Spring {@link Resource}. A plain path
	 * &mdash; for example, {@code "schema.cql"} &mdash;  will be treated as an <em>absolute</em> classpath resource. A
	 * path which references a URL (e.g., a path prefixed with {@code http:}, etc.) will be loaded using the specified
	 * resource protocol.
	 *
	 * @return CQL Scripts
	 */
	String[] scripts() default {};

	/**
	 * The encoding for the supplied CQL scripts.
	 *
	 * @return CQL scripts encoding.
	 */
	String encoding() default "UTF-8";

}
