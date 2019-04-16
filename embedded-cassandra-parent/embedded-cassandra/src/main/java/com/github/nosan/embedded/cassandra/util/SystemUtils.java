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

package com.github.nosan.embedded.cassandra.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.util.Optional;

import com.github.nosan.embedded.cassandra.lang.annotation.Nullable;

/**
 * Utility classes for dealing with system properties. <b>Only for internal purposes.</b>
 *
 * @author Dmytro Nosan
 * @since 2.0.0
 */
public abstract class SystemUtils {

	/**
	 * Returns {@code true} if this is Windows.
	 *
	 * @return {@code true} if this is Windows, otherwise {@code false}
	 */
	public static boolean isWindows() {
		String os = getValue("os.name");
		if (StringUtils.hasText(os)) {
			return os.toLowerCase(Locale.ENGLISH).contains("windows");
		}
		return File.separatorChar == '\\';
	}

	/**
	 * Returns the temporary directory.
	 *
	 * @return a directory (java.io.tmpdir)
	 */
	public static Optional<Path> getTmpDirectory() {
		return Optional.ofNullable(getValue("java.io.tmpdir")).map(Paths::get);
	}

	/**
	 * Returns the user home directory.
	 *
	 * @return a directory (user.home)
	 */
	public static Optional<Path> getUserHomeDirectory() {
		return Optional.ofNullable(getValue("user.home")).map(Paths::get);
	}

	/**
	 * Returns the user directory.
	 *
	 * @return a directory (user.dir)
	 */
	public static Optional<Path> getUserDirectory() {
		return Optional.ofNullable(getValue("user.dir")).map(Paths::get);
	}

	/**
	 * Returns the java home directory.
	 *
	 * @return a directory (java.home)
	 */
	public static Optional<Path> getJavaHomeDirectory() {
		return Optional.ofNullable(getValue("java.home")).map(Paths::get);
	}

	@Nullable
	private static String getValue(String name) {
		String value = getSystemProperty(name);
		if (value == null) {
			value = getEnvironmentProperty(name);
		}
		return value;
	}

	@Nullable
	private static String getSystemProperty(String name) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			return AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty(name));
		}
		return System.getProperty(name);
	}

	@Nullable
	private static String getEnvironmentProperty(String name) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			return AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getenv(name));
		}
		return System.getenv(name);
	}

}