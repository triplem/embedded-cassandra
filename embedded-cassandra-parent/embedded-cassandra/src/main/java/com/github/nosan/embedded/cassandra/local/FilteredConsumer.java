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

package com.github.nosan.embedded.cassandra.local;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * {@link Consumer} implementation that filters the input before delegates it to the underlying {@link Consumer}.
 *
 * @param <T> the type
 * @author Dmytro Nosan
 * @since 1.4.2
 */
class FilteredConsumer<T> implements Consumer<T> {

	private final Consumer<? super T> consumer;

	private final Predicate<? super T> filter;

	FilteredConsumer(Consumer<? super T> consumer, Predicate<? super T> filter) {
		this.consumer = consumer;
		this.filter = filter;
	}

	@Override
	public void accept(T object) {
		if (this.filter.test(object)) {
			this.consumer.accept(object);
		}
	}

}
