/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.geode.internal.cache;

import static org.apache.geode.distributed.ConfigurationProperties.CACHE_XML_FILE;
import static org.apache.geode.distributed.ConfigurationProperties.LOCATORS;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.distributed.ServerLauncher;
import org.apache.geode.distributed.ServerLauncherIntegrationTest;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.CacheServerLauncher;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.test.dunit.VM;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.DistributedTest;
import org.apache.geode.test.junit.rules.GfshCommandRule;
import org.apache.geode.test.junit.rules.LocatorStarterRule;
import org.apache.geode.test.junit.rules.Server;
import org.apache.geode.test.junit.rules.ServerStarterRule;
import org.apache.geode.util.test.TestUtil;

@Category(DistributedTest.class)
public class StartServerWithXmlDUnitTest {

  private VM server;
  private MemberVM locator;

  @Rule
  public ClusterStartupRule cluster = new ClusterStartupRule();

  @Before
  public void before() throws Exception {
    locator = cluster.startLocatorVM(0);

    Properties props = new Properties();
    String locators = "localhost[" + locator.getPort() + "]";
    props.setProperty(LOCATORS, locators);
    String cacheXmlPath = TestUtil.getResourcePath(getClass(), "CacheServerWithZeroPort.xml");
    props.setProperty(CACHE_XML_FILE, cacheXmlPath);

    server = cluster.getVM(1);

    server.invoke(() -> {
      CacheServerLauncher.setServerBindAddress("localhost");
      CacheServerLauncher.setDisableDefaultServer(false);
      CacheFactory cf = new CacheFactory(props);
      Cache cache = cf.create();
    });
  }

  @Test
  public void startServerWithXMLNotToStartDefaultCacheServer() {
    // Verify that when there is a declarative cache server then we dont launch default server
    server.invoke(() -> {
      assertThat(GemFireCacheImpl.getInstance().getCacheServers().size()).isEqualTo(1);
    });
  }
}
