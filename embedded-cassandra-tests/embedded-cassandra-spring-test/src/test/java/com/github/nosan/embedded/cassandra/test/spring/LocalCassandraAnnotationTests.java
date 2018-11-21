/*
 * Copyright 2018-2018 the original author or authors.
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

package com.github.nosan.embedded.cassandra.test.spring;

import java.nio.file.Paths;
import java.time.Duration;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.nosan.embedded.cassandra.Version;
import com.github.nosan.embedded.cassandra.local.LocalCassandraFactory;
import com.github.nosan.embedded.cassandra.local.artifact.RemoteArtifactFactory;
import com.github.nosan.embedded.cassandra.util.ClassUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LocalCassandraContextCustomizer}.
 *
 * @author Dmytro Nosan
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@LocalCassandra(version = "2.2.13", configurationFile = "classpath:/cassandra.yaml",
		logbackFile = "classpath:/logback-test.xml",
		rackFile = "classpath:/rack.properties",
		workingDirectory = "target/cassandra", javaHome = "target/java",
		jvmOptions = {"-Dtest.property=property"},
		topologyFile = "classpath:/topology.properties", startupTimeout = 240000,
		jmxPort = 8000,
		allowRoot = true,
		replace = EmbeddedCassandra.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class LocalCassandraAnnotationTests {

	@Autowired
	private LocalCassandraFactory factory;

	@Autowired
	private RemoteArtifactFactory artifactFactory;


	@Test
	public void shouldRegisterLocalFactoryBean() {
		LocalCassandraFactory factory = this.factory;
		assertThat(factory.getArtifactFactory()).isEqualTo(this.artifactFactory);
		assertThat(factory.getVersion()).isEqualTo(Version.parse("2.2.13"));
		assertThat(factory.getWorkingDirectory()).isEqualTo(Paths.get("target/cassandra"));
		assertThat(factory.getJavaHome()).isEqualTo(Paths.get("target/java"));
		assertThat(factory.getStartupTimeout()).isEqualTo(Duration.ofMinutes(4));
		assertThat(factory.getLogbackFile()).isEqualTo(ClassUtils.getClassLoader().getResource("logback-test.xml"));
		assertThat(factory.getTopologyFile()).isEqualTo(ClassUtils.getClassLoader().getResource("topology.properties"));
		assertThat(factory.getRackFile()).isEqualTo(ClassUtils.getClassLoader().getResource("rack.properties"));
		assertThat(factory.getConfigurationFile()).isEqualTo(ClassUtils.getClassLoader().getResource("cassandra.yaml"));
		assertThat(factory.getJvmOptions()).containsExactly("-Dtest.property=property");
		assertThat(factory.getJmxPort()).isEqualTo(8000);
		assertThat(factory.isAllowRoot()).isTrue();

	}

	@Configuration
	static class TestConfiguration implements BeanDefinitionRegistryPostProcessor {

		@Bean
		public RemoteArtifactFactory remoteArtifactFactory() {
			return new RemoteArtifactFactory();
		}


		@Override
		public void postProcessBeanDefinitionRegistry(@Nonnull BeanDefinitionRegistry registry) throws BeansException {
			registry.removeBeanDefinition(EmbeddedCassandraFactoryBean.BEAN_NAME);
		}

		@Override
		public void postProcessBeanFactory(@Nonnull ConfigurableListableBeanFactory beanFactory) throws BeansException {

		}
	}


}
