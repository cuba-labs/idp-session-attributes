package com.company.idpdemo.service;


public interface DemoIdpService {
    String NAME = "idpdemo_DemoIdpService";

    void setTenantToIdpSession(String tenant);
}