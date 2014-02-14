package hello;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import hello.client.ClientApplication;

import java.io.File;
import java.util.List;

import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.yarn.boot.test.junit.AbstractBootYarnClusterTests;
import org.springframework.yarn.boot.test.junit.AbstractBootYarnClusterTests.EmptyConfig;
import org.springframework.yarn.test.context.MiniYarnCluster;
import org.springframework.yarn.test.context.YarnDelegatingSmartContextLoader;
import org.springframework.yarn.test.junit.ApplicationInfo;
import org.springframework.yarn.test.support.ContainerLogUtils;

@ContextConfiguration(loader = YarnDelegatingSmartContextLoader.class, classes = EmptyConfig.class)
@MiniYarnCluster
public class AppTests extends AbstractBootYarnClusterTests {

	@Test
	public void testAppSubmission() throws Exception {

		String[] args = new String[] {
				"--spring.yarn.client.files[0]=file:build/libs/gs-yarn-testing-appmaster-0.1.0.jar",
				"--spring.yarn.client.files[1]=file:build/libs/gs-yarn-testing-container-0.1.0.jar" };

		ApplicationInfo info = submitApplicationAndWait(ClientApplication.class, args);
		assertThat(info.getYarnApplicationState(), is(YarnApplicationState.FINISHED));

		List<Resource> resources = ContainerLogUtils.queryContainerLogs(getYarnCluster(), info.getApplicationId());
		assertThat(resources, notNullValue());
		assertThat(resources.size(), is(4));

		for (Resource res : resources) {
			File file = res.getFile();
			String content = ContainerLogUtils.getFileContent(file);
			if (file.getName().endsWith("stdout")) {
				assertThat(file.length(), greaterThan(0l));
				if (file.getName().equals("Container.stdout")) {
					assertThat(content, containsString("Hello from HelloPojo"));
				}
			} else if (file.getName().endsWith("stderr")) {
				assertThat("stderr file is not empty: " + content, file.length(), is(0l));
			}
		}
	}

}
