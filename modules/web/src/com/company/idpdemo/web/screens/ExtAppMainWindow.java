package com.company.idpdemo.web.screens;

import com.company.idpdemo.service.DemoIdpService;
import com.haulmont.cuba.security.app.IdpService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ExtAppMainWindow extends AppMainWindow {
    @Inject
    private UserSession userSession;
    @Inject
    private WebAuthConfig webAuthConfig;
    @Inject
    private DemoIdpService demoIdpService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        // Try to update session attribute

        demoIdpService.setTenantToIdpSession("localhost/dbpt_tenant1");

        // Check it

        String idpSessionId = userSession.getAttribute(IdpService.IDP_USER_SESSION_ATTRIBUTE);
        if (idpSessionId == null) {
            return;
        }

        // now we have to use HTTP request to get whole session object from IDP
        // see com.haulmont.idp.controllers.IdpServiceController
        String idpBaseURL = webAuthConfig.getIdpBaseURL();
        if (!idpBaseURL.endsWith("/")) {
            idpBaseURL += "/";
        }
        String idpSessionGetUrl = idpBaseURL + "service/get";

        URI sessionGetUri;
        try {
            sessionGetUri = new URIBuilder(idpSessionGetUrl)
                    .addParameter("idpSessionId", idpSessionId)
                    .addParameter("trustedServicePassword", webAuthConfig.getIdpTrustedServicePassword())
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("incorrect URL");
        }

        HttpGet httpGet = new HttpGet(sessionGetUri);

        HttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
        HttpClient client = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();

        String idpSessionJson;
        try {
            HttpResponse httpResponse = client.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                // error handling
            }
            idpSessionJson = new BasicResponseHandler()
                    .handleResponse(httpResponse);
        } catch (IOException e) {
            throw new RuntimeException("Error");
        } finally {
            connectionManager.shutdown();
        }

        // now we can parse idpSessionJson content and read session attributes
        System.out.println(idpSessionJson);
    }
}