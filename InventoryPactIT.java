package io.openliberty.guides.inventory;

import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

public class InventoryPactIT {
  @Rule
  public PactProviderRule mockProvider = new PactProviderRule("System", this);

  @Pact(consumer = "Inventory")
  public RequestResponsePact createPactServer(PactDslWithProvider builder) {
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");

    return builder
      .given("wlp.server.name is defaultServer")
      .uponReceiving("a request for server name")
      .path("/system/properties/key/wlp.server.name")
      .method("GET")
      .willRespondWith()
      .headers(headers)
      .status(200)
      .body(new PactDslJsonArray().object()
        .stringValue("wlp.server.name", "defaultServer"))
      .toPact();
  }

  @Pact(consumer = "Inventory")
  public RequestResponsePact createPactEdition(PactDslWithProvider builder) {
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");

    return builder
      .given("Default directory is true")
      .uponReceiving("a request to check for the default directory")
      .path("/system/properties/key/wlp.user.dir.isDefault")
      .method("GET")
      .willRespondWith()
      .headers(headers)
      .status(200)
      .body(new PactDslJsonArray().object()
        .stringValue("wlp.user.dir.isDefault", "true"))
      .toPact();
  }

  @Pact(consumer = "Inventory")
  public RequestResponsePact createPactVersion(PactDslWithProvider builder) {
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");

    return builder
      .given("version is 1.1")
      .uponReceiving("a request for the version")
      .path("/system/properties/version")
      .method("GET")
      .willRespondWith()
      .headers(headers)
      .status(200)
      .body(new PactDslJsonBody()
        .decimalType("system.properties.version", 1.1))
      .toPact();
  }

  @Pact(consumer = "Inventory")
  public RequestResponsePact createPactInvalid(PactDslWithProvider builder) {

    return builder
      .given("invalid property")
      .uponReceiving("a request with an invalid property")
      .path("/system/properties/invalidProperty")
      .method("GET")
      .willRespondWith()
      .status(404)
      .toPact();
  }

  @Test
  @PactVerification(value = "System", fragment = "createPactServer")
  public void runServerTest() {
    String serverName = new Inventory(mockProvider.getUrl()).getServerName();
    assertEquals("Expected server name does not match",
      "[{\"wlp.server.name\":\"defaultServer\"}]", serverName);
  }

  @Test
  @PactVerification(value = "System", fragment = "createPactEdition")
  public void runEditionTest() {
    String edition = new Inventory(mockProvider.getUrl()).getEdition();
    assertEquals("Expected edition does not match",
      "[{\"wlp.user.dir.isDefault\":\"true\"}]", edition);
  }

  @Test
  @PactVerification(value = "System", fragment = "createPactVersion")
  public void runVersionTest() {
    String version = new Inventory(mockProvider.getUrl()).getVersion();
    assertEquals("Expected version does not match",
      "{\"system.properties.version\":1.1}", version);
  }

  @Test
  @PactVerification(value = "System", fragment = "createPactInvalid")
  public void runInvalidTest() {
    String invalid = new Inventory(mockProvider.getUrl()).getInvalidProperty();
    assertEquals("Expected invalid property response does not match",
      "", invalid);
  }
}
