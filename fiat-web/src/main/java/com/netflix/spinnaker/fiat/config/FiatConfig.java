package com.netflix.spinnaker.fiat.config;

import com.google.common.collect.ImmutableList;
import com.netflix.spectator.api.Registry;
import com.netflix.spinnaker.fiat.model.resources.Role;
import com.netflix.spinnaker.fiat.roles.UserRolesProvider;
import com.netflix.spinnaker.kork.web.interceptors.MetricsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Import(RetrofitConfig.class)
@EnableConfigurationProperties(FiatServerConfigurationProperties.class)
public class FiatConfig extends WebMvcConfigurerAdapter {

  @Autowired
  private Registry registry;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    List<String> pathVarsToTag = ImmutableList.of("accountName", "applicationName", "resourceName");
    List<String> exclude = ImmutableList.of("BasicErrorController");
    MetricsInterceptor interceptor = new MetricsInterceptor(this.registry,
                                                            "controller.invocations",
                                                            pathVarsToTag,
                                                            exclude);
    registry.addInterceptor(interceptor);
  }

  @Bean
  @ConditionalOnMissingBean(UserRolesProvider.class)
  UserRolesProvider defaultUserRolesProvider() {
    return new UserRolesProvider() {
      @Override
      public Map<String, Collection<Role>> multiLoadRoles(Collection<String> userIds) {
        return new HashMap<>();
      }

      @Override
      public List<Role> loadRoles(String userId) {
        return new ArrayList<>();
      }
    };
  }
}
