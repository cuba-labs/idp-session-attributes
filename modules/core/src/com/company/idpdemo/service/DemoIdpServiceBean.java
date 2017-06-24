package com.company.idpdemo.service;

import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.app.IdpService;
import com.haulmont.cuba.security.app.IdpSessionStore;
import com.haulmont.cuba.security.global.IdpSession;
import com.haulmont.cuba.security.global.UserSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service(DemoIdpService.NAME)
public class DemoIdpServiceBean implements DemoIdpService {
    @Inject
    private IdpSessionStore idpSessionStore;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public void setTenantToIdpSession(String tenant) {
        UserSession userSession = userSessionSource.getUserSession();
        String idpSessionId = userSession.getAttribute(IdpService.IDP_USER_SESSION_ATTRIBUTE);

        if (idpSessionId != null) {
            IdpSession idpSession = idpSessionStore.getSession(idpSessionId);
            if (idpSession != null) {
                Map<String, Serializable> attributes = idpSession.getAttributes();
                if (attributes == null) {
                    attributes = new HashMap<>();
                    idpSession.setAttributes(attributes);
                }
                attributes.put("tenantDbAddress","localhost/dbpt_tenant1");
            }
        }
    }
}