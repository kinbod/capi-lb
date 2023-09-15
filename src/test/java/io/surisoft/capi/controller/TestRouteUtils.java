package io.surisoft.capi.controller;

import io.surisoft.capi.schema.Api;
import io.surisoft.capi.schema.HttpProtocol;
import io.surisoft.capi.schema.Mapping;
import io.surisoft.capi.utils.RouteUtils;
import org.apache.camel.model.RouteDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(
      locations = "classpath:test-consul-application.properties"
)
class TestRouteUtils {

    @Autowired
    RouteUtils routeUtils;

    @Test
    void testBuildException() {
        RouteDefinition routeDefinition = new RouteDefinition();
        String routeId = "unit-test:unit-test-context";
        routeUtils.buildOnExceptionDefinition(routeDefinition, false, false, false, routeId);
        Assertions.assertNotNull(routeDefinition);
    }

    @Test
    void testBuildEndpoints() {

        List<String> expectedEndpointList = new ArrayList<>();
        expectedEndpointList.add("https://first.domain:8380/?bridgeEndpoint=true&throwExceptionOnFailure=false");
        expectedEndpointList.add("https://second.domain:8381/?bridgeEndpoint=true&throwExceptionOnFailure=false");

        Api api = new Api();
        api.setName("test");
        api.setHttpProtocol(HttpProtocol.HTTPS);
        Set<Mapping> mappingList = new HashSet<>();

        Mapping mapping1 = new Mapping();
        mapping1.setHostname("first.domain");
        mapping1.setPort(8380);
        mapping1.setRootContext("/");

        Mapping mapping2 = new Mapping();
        mapping2.setHostname("second.domain");
        mapping2.setPort(8381);
        mapping2.setRootContext("/");

        mappingList.add(mapping1);
        mappingList.add(mapping2);
        api.setMappingList(mappingList);

        String[] endpoints = routeUtils.buildEndpoints(api);
        for(String endpoint : endpoints) {
            Assertions.assertTrue(expectedEndpointList.contains(endpoint));
        }
    }

    @Test
    void testBuildFrom() {
        Api api = new Api();
        api.setContext("test");
        String context = routeUtils.buildFrom(api);
        Assertions.assertEquals(context, "/test");
    }

    @Test
    void testGetRouteId() {
        String expectedId = "unit-test:test:get";
        Api api = new Api();
        api.setName("unit-test");
        api.setContext("test");

        String routeId = routeUtils.getRouteId(api, "get");
        Assertions.assertEquals(routeId, expectedId);
    }

    @Test
    void testGetMethodFromRouteId() {
        String routeId = "unit-test:test:get";
        Assertions.assertEquals("get", routeUtils.getMethodFromRouteId(routeId));
    }
}