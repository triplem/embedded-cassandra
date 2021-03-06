/*
 * Copyright 2018-2020 the original author or authors.
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

package examples.cql;

import java.util.List;

import com.github.nosan.embedded.cassandra.api.cql.CqlDataSet;
import com.github.nosan.embedded.cassandra.api.cql.CqlScript;
import com.github.nosan.embedded.cassandra.commons.io.ClassPathResource;

public class CqlDataSetExample {

	// tag::source[]

	void classpaths() {
		CqlDataSet dataSet = CqlDataSet.ofClasspaths("schema.cql");
		List<String> statements = dataSet.getStatements();
		//...
	}

	void resources() {
		CqlDataSet dataSet = CqlDataSet.ofResources(new ClassPathResource("schema.cql"));
		List<String> statements = dataSet.getStatements();
		//...
	}

	void strings() {
		CqlDataSet dataSet = CqlDataSet.ofStrings(
				"CREATE KEYSPACE test WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':1}");
		List<String> statements = dataSet.getStatements();
		//...
	}

	void scripts() {
		CqlDataSet dataSet = CqlDataSet.ofScripts(CqlScript.ofClasspath("schema.cql"),
				CqlScript.ofClasspath("schema1.cql"));
		List<String> statements = dataSet.getStatements();
		//...
	}

	// end::source[]

}
