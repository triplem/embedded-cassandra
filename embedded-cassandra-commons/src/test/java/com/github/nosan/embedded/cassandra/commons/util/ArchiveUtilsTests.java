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

package com.github.nosan.embedded.cassandra.commons.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Tests for {@link ArchiveUtils}.
 *
 * @author Dmytro Nosan
 */
class ArchiveUtilsTests {

	@ParameterizedTest
	@MethodSource("archives")
	void extract(String name, String archiveFormat, String compression, @TempDir Path temporaryFolder)
			throws Exception {
		Path archiveFile = Files.createTempFile(temporaryFolder, "", "." + name);
		File file = new File(getClass().getResource("/text.txt").toURI());
		archive(archiveFormat, archiveFile, file);
		compress(compression, archiveFile);
		ArchiveUtils.extract(archiveFile, temporaryFolder);
		assertThat(temporaryFolder.resolve("text.txt").toFile()).hasSameContentAs(file);
	}

	static Stream<Arguments> archives() {
		List<Arguments> parameters = new ArrayList<>();
		parameters.add(arguments("tar.gz", ArchiveStreamFactory.TAR, CompressorStreamFactory.GZIP));
		parameters.add(arguments("tgz", ArchiveStreamFactory.TAR, CompressorStreamFactory.GZIP));
		parameters.add(arguments("tar.bz2", ArchiveStreamFactory.TAR, CompressorStreamFactory.BZIP2));
		parameters.add(arguments("tbz2", ArchiveStreamFactory.TAR, CompressorStreamFactory.BZIP2));
		parameters.add(arguments("jar", ArchiveStreamFactory.JAR, ""));
		parameters.add(arguments("tar", ArchiveStreamFactory.TAR, ""));
		parameters.add(arguments("zip", ArchiveStreamFactory.ZIP, ""));
		parameters.add(arguments("zipx", ArchiveStreamFactory.ZIP, ""));
		return parameters.stream();
	}

	private static void archive(String archiveFormat, Path archive, File file) throws Exception {
		ArchiveStreamFactory af = new ArchiveStreamFactory();
		try (ArchiveOutputStream os = af.createArchiveOutputStream(archiveFormat, Files.newOutputStream(archive))) {
			ArchiveEntry archiveEntry = os.createArchiveEntry(file, file.getName());
			os.putArchiveEntry(archiveEntry);
			try (InputStream is = Files.newInputStream(file.toPath())) {
				os.write(IOUtils.toByteArray(is));
			}
			os.closeArchiveEntry();
		}
	}

	private static void compress(String compression, Path archive) throws Exception {
		if (StringUtils.hasText(compression)) {
			byte[] content;
			try (InputStream is = Files.newInputStream(archive)) {
				content = IOUtils.toByteArray(is);
			}
			CompressorStreamFactory cf = new CompressorStreamFactory();
			try (OutputStream os = cf.createCompressorOutputStream(compression, Files.newOutputStream(archive))) {
				IOUtils.copy(new ByteArrayInputStream(content), os);
			}
		}
	}

}
