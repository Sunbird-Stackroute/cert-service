package controllers.health;

import controllers.BaseApplicationTest;
import controllers.DummyActor;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import utils.module.ACTOR_NAMES;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HealthControllerTest extends BaseApplicationTest {

  @Before
  public void setUp(){
    setup(Arrays.asList(ACTOR_NAMES.HEALTH_ACTOR), DummyActor.class);
  }


  //@Test
  public void testGetHealthSuccess() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put("accept", "yes");
    Result result = performTest("/health", "GET", reqMap);
    assertTrue(getResponseStatus(result) == Response.Status.OK.getStatusCode());
  }
  @Test
  public void testGetHealthFailure() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put("accept", "yes");
    Result result = performTest("/health", "POST", reqMap);
    assertTrue(getResponseStatus(result) == Response.Status.NOT_FOUND.getStatusCode());
  }


  @Test
  public void testGetServiceHealthSuccess() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put("accept", "yes");
    Result result = performTest("/service/health", "GET", reqMap);
    assertTrue(getResponseStatus(result) == Response.Status.OK.getStatusCode());
  }
  @Test
  public void testGetServiceHealthFailure() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put("accept", "yes");
    Result result = performTest("/user-service/health", "POST", reqMap);
    assertTrue(getResponseStatus(result) == Response.Status.NOT_FOUND.getStatusCode());
  }


}