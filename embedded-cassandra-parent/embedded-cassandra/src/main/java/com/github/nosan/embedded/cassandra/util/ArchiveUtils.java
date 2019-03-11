/*
 * Copyright 2018-2019 the original author or authors.
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

package com.github.nosan.embedded.cassandra.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nosan.embedded.cassandra.lang.Nullable;

/**
 * Utility methods for dealing with archives.
 *
 * @author Dmytro Nosan
 * @since 1.0.0
 */
@API(since = "1.0.0", status = API.Status.INTERNAL)
public abstract class ArchiveUtils {

	private static final Map<String, ArchiveFactory> ARCHIVES;

	static {
		Map<String, ArchiveFactory> candidates = new LinkedHashMap<>();
		candidates.put(".tar.gz", ArchiveFactory.create(ArchiveStreamFactory.TAR, CompressorStreamFactory.GZIP));
		candidates.put(".tar.bz2", ArchiveFactory.create(ArchiveStreamFactory.TAR, CompressorStreamFactory.BZIP2));
		candidates.put(".tar.xz", ArchiveFactory.create(ArchiveStreamFactory.TAR, CompressorStreamFactory.XZ));
		candidates.put(".tbz2", ArchiveFactory.create(ArchiveStreamFactory.TAR, CompressorStreamFactory.BZIP2));
		candidates.put(".tgz", ArchiveFactory.create(ArchiveStreamFactory.TAR, CompressorStreamFactory.GZIP));
		candidates.put(".txz", ArchiveFactory.create(ArchiveStreamFactory.TAR, CompressorStreamFactory.XZ));
		candidates.put(".a", ArchiveFactory.create(ArchiveStreamFactory.AR));
		candidates.put(".ar", ArchiveFactory.create(ArchiveStreamFactory.AR));
		candidates.put(".cpio", ArchiveFactory.create(ArchiveStreamFactory.CPIO));
		candidates.put(".jar", ArchiveFactory.create(ArchiveStreamFactory.JAR));
		candidates.put(".tar", ArchiveFactory.create(ArchiveStreamFactory.TAR));
		candidates.put(".zip", ArchiveFactory.create(ArchiveStreamFactory.ZIP));
		candidates.put(".zipx", ArchiveFactory.create(ArchiveStreamFactory.ZIP));
		ARCHIVES = Collections.unmodifiableMap(candidates);
	}

	/**
	 * Extracts the source archive file into the given destination directory.
	 * The destination is expected to be a writable directory.
	 *
	 * @param archiveFile the archive file to extract
	 * @param destination the directory to which to extract the files
	 * @throws IOException in the case of I/O errors
	 */
	public static void extract(Path archiveFile, Path destination) throws IOException {
		Objects.requireNonNull(archiveFile, "Archive must not be null");
		Objects.requireNonNull(destination, "Destination must not be null");
		ArchiveFactory archiveFactory = createArchiveFactory(archiveFile);
		try (InputStream stream = Files.newInputStream(archiveFile);
				ArchiveInputStream archiveStream = archiveFactory.create(stream)) {
			Files.createDirectories(destination);
			ArchiveEntry entry;
			while ((entry = archiveStream.getNextEntry()) != null) {
				Path dest = destination.resolve(entry.getName());
				if (entry.isDirectory()) {
					Files.createDirectories(dest);
				}
				else {
					Path tempFile = Files.createTempFile(null, null);
					Files.copy(archiveStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
					Files.move(tempFile, dest, StandardCopyOption.REPLACE_EXISTING);
				}
				FileModeUtils.set(entry, dest);
			}
		}
		catch (ArchiveException | CompressorException ex) {
			throw new IOException(String.format("Could not create a stream for archive (%s)", archiveFile), ex);
		}
	}

	private static ArchiveFactory createArchiveFactory(Path source) {
		for (Map.Entry<String, ArchiveFactory> candidate : ARCHIVES.entrySet()) {
			if (String.valueOf(source.getFileName()).endsWith(candidate.getKey())) {
				return candidate.getValue();
			}
		}
		throw new IllegalArgumentException(String.format("Archive (%s) is not supported", source));
	}

	/**
	 * Factory for creating {@link ArchiveInputStream} instances by a given archive.
	 */
	private interface ArchiveFactory {

		/**
		 * Creates a factory for the given archive format.
		 *
		 * @param archiveFormat the archiveFormat format e.g. "tar" or "zip"
		 * @return a new Factory instance that also handles compression
		 */
		static ArchiveFactory create(String archiveFormat) {
			return create(archiveFormat, null);
		}

		/**
		 * Creates a factory for the given archive format that uses compression.
		 *
		 * @param archiveFormat the archiveFormat format e.g. "tar" or "zip"
		 * @param compressionFormat the compression algorithm name e.g. "gz"
		 * @return a new Factory instance that also handles compression
		 */
		static ArchiveFactory create(String archiveFormat, @Nullable String compressionFormat) {
			return stream -> {
				CompressorStreamFactory cf = new CompressorStreamFactory();
				ArchiveStreamFactory af = new ArchiveStreamFactory();
				if (StringUtils.hasText(compressionFormat)) {
					return af.createArchiveInputStream(archiveFormat,
							cf.createCompressorInputStream(compressionFormat, stream));
				}
				return af.createArchiveInputStream(archiveFormat, stream);
			};
		}

		/**
		 * Reads the given archive file as an {@link ArchiveInputStream} which is used to access individual
		 * {@link ArchiveEntry} objects within the archive without extracting the archive onto the file system.
		 *
		 * @param inputStream the file to stream
		 * @return a new archive stream for the given archive
		 * @throws IOException IOException in the case of I/O errors
		 * @throws CompressorException if the compressor name is not known or not available, or if there's an
		 * IOException or MemoryLimitException thrown during initialization
		 * @throws ArchiveException if the archive name is not known
		 */
		ArchiveInputStream create(InputStream inputStream) throws IOException, CompressorException, ArchiveException;

	}

	/**
	 * Utility class to set a file mode.
	 */
	private abstract static class FileModeUtils {

		private static final Logger log = LoggerFactory.getLogger(FileModeUtils.class);

		private static final int MASK = 511;

		private static final Map<Integer, PosixFilePermission> permissions;

		static {
			Map<Integer, PosixFilePermission> values = new LinkedHashMap<>();
			values.put(256, PosixFilePermission.OWNER_READ);
			values.put(128, PosixFilePermission.OWNER_WRITE);
			values.put(64, PosixFilePermission.OWNER_EXECUTE);
			values.put(32, PosixFilePermission.GROUP_READ);
			values.put(16, PosixFilePermission.GROUP_WRITE);
			values.put(8, PosixFilePermission.GROUP_EXECUTE);
			values.put(4, PosixFilePermission.OTHERS_READ);
			values.put(2, PosixFilePermission.OTHERS_WRITE);
			values.put(1, PosixFilePermission.OTHERS_EXECUTE);
			permissions = Collections.unmodifiableMap(values);
		}

		/**
		 * Sets the file mode onto the given file.
		 *
		 * @param entry the archive entry that holds the mode
		 * @param file the file to apply the mode onto
		 */
		static void set(ArchiveEntry entry, Path file) {
			if (!isWindows()) {
				long mode = getMode(entry) & MASK;
				if (mode > 0) {
					Set<PosixFilePermission> permissions = getPermissions(mode);
					try {
						Files.setPosixFilePermissions(file, permissions);
					}
					catch (Throwable ex) {
						if (log.isDebugEnabled()) {
							log.error(String.format("Could not set permission(s) (%s) to (%s)", permissions, file), ex);
						}
					}
				}
			}
		}

		private static boolean isWindows() {
			return File.separatorChar == '\\';
		}

		private static Set<PosixFilePermission> getPermissions(long mode) {
			return permissions.entrySet()
					.stream()
					.filter(entry -> (mode & entry.getKey()) > 0)
					.map(Map.Entry::getValue)
					.collect(Collectors.toSet());
		}

		private static long getMode(ArchiveEntry entry) {
			if (entry instanceof TarArchiveEntry) {
				return ((TarArchiveEntry) entry).getMode();
			}
			else if (entry instanceof ZipArchiveEntry) {
				return ((ZipArchiveEntry) entry).getUnixMode();
			}
			else if (entry instanceof CpioArchiveEntry) {
				return ((CpioArchiveEntry) entry).getMode();
			}
			else if (entry instanceof ArArchiveEntry) {
				return ((ArArchiveEntry) entry).getMode();
			}
			return 0;
		}

	}

}

