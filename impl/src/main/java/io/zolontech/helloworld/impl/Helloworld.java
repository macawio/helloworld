package io.zolontech.helloworld.impl;

import java.lang.Override;

public class Helloworld implements com.cfx.service.api.Service, io.zolontech.helloworld.Helloworld {
  @Override
  public void initialize(com.cfx.service.api.config.Configuration config) throws com.cfx.service.api.ServiceException {
  }

  @Override
  public void start(com.cfx.service.api.StartContext startContext) throws com.cfx.service.api.ServiceException {
  }

  @Override
  public void stop(com.cfx.service.api.StopContext stopContext) throws com.cfx.service.api.ServiceException {
  }

  @Override
  public String sayHello() {
    return new String("Hello, World!");    
  }
}
