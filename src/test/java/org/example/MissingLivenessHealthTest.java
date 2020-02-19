package org.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.health.ReadinessHealthCheck;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunAsClient
@RunWith(Arquillian.class)
public class MissingLivenessHealthTest {

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, MissingLivenessHealthTest.class.getSimpleName() + ".war")
                .addClasses(ReadinessHealthCheck.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testHealthEndpoint() {
        RestAssured.get("http://localhost:9990/health").then()
                .contentType(ContentType.JSON)
                .body("status", is("UP"),
                        "checks", hasSize(1),
                        "checks.status", hasItems("UP"),
                        "checks.name", containsInAnyOrder("ready"),
                        "checks.data", hasSize(1),
                        "checks.data[0].key", is("value")
                );
    }

    @Test
    public void testLivenessEndpoint() {
        RestAssured.get("http://localhost:9990/health/live").then()
                .contentType(ContentType.JSON)
                .body("status", is("UP"),
                        "checks", is(empty())
                );
    }

    @Test
    public void testReadinessEndpoint() {
        RestAssured.get("http://localhost:9990/health/ready").then()
                .contentType(ContentType.JSON)
                .body("status", is("UP"),
                        "checks", hasSize(1),
                        "checks.status", hasItems("UP"),
                        "checks.name", containsInAnyOrder("ready"),
                        "checks.data", hasSize(1),
                        "checks.data[0].key", is("value")
                );
    }

}
