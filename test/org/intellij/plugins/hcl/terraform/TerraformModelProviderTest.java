/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.plugins.hcl.terraform;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.testFramework.LightPlatformTestCase;
import org.intellij.plugins.hcl.terraform.config.model.*;

public class TerraformModelProviderTest extends LightPlatformTestCase {
  public void testModelIsLoaded() throws Exception {
    final TypeModelProvider provider = ServiceManager.getService(TypeModelProvider.class);
    //noinspection unused
    final TypeModel model = provider.get();
  }

  public void testProperlyParsedNetworkInterface() throws Exception {
    final TypeModelProvider provider = ServiceManager.getService(TypeModelProvider.class);
    assertNotNull(provider);
    final TypeModel model = provider.get();
    assertNotNull(model);
    final ResourceType google_compute_instance = model.getResourceType("google_compute_instance");
    assertNotNull(google_compute_instance);
    final PropertyOrBlockType[] properties = google_compute_instance.getProperties();
    final PropertyOrBlockType network_interface = findProperty(properties, "network_interface");
    assertNotNull(network_interface);
    final BlockType network_interfaceBlock = network_interface.getBlock();
    assertNotNull(network_interfaceBlock);
    final PropertyOrBlockType access_config = findProperty(network_interfaceBlock.getProperties(), "access_config");
    assertNotNull(access_config);
    final BlockType access_configBlock = access_config.getBlock();
    assertNotNull(access_configBlock);
    assertNotNull(findProperty(access_configBlock.getProperties(), "assigned_nat_ip"));
    assertNotNull(findProperty(access_configBlock.getProperties(), "nat_ip"));
  }

  // Test for #67
  public void test_aws_cloudfront_distribution_forwarded_values() throws Exception {
    final TypeModelProvider provider = ServiceManager.getService(TypeModelProvider.class);
    assertNotNull(provider);
    final TypeModel model = provider.get();
    assertNotNull(model);

    final ResourceType aws_cloudfront_distribution = model.getResourceType("aws_cloudfront_distribution");
    assertNotNull(aws_cloudfront_distribution);
    final PropertyOrBlockType[] properties = aws_cloudfront_distribution.getProperties();

    final PropertyOrBlockType default_cache_behavior = findProperty(properties, "default_cache_behavior");
    assertNotNull(default_cache_behavior);
    final BlockType default_cache_behavior_block = default_cache_behavior.getBlock();
    assertNotNull(default_cache_behavior_block);

    final PropertyOrBlockType forwarded_values = findProperty(default_cache_behavior_block.getProperties(), "forwarded_values");
    assertNotNull(forwarded_values);
    final BlockType forwarded_values_block = forwarded_values.getBlock();
    assertNotNull(forwarded_values_block);

    assertNotNull(findProperty(forwarded_values_block.getProperties(), "query_string"));

    PropertyOrBlockType cookies = findProperty(forwarded_values_block.getProperties(), "cookies");
    assertNotNull(cookies);
    assertTrue(cookies.getRequired());
  }

  private PropertyOrBlockType findProperty(PropertyOrBlockType[] properties, String name) {
    for (PropertyOrBlockType property : properties) {
      if (name.equals(property.getName())) return property;
    }
    return null;
  }
}
