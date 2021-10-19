package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import utils.module.ACTOR_NAMES;
import utils.module.StartModule;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static play.inject.Bindings.bind;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"jdk.internal.reflect.*","javax.management.*","sun.security.ssl.*", "javax.net.ssl.*" , "javax.crypto.*"})
public abstract class BaseApplicationTest {
    protected Application application;

    public <T> void setup(ACTOR_NAMES actor, Class actorClass) {
        application =
                new GuiceApplicationBuilder()
                        .in(new File("path/to/app"))
                        .in(Mode.TEST)
                        .disable(StartModule.class)
                        //                        .disable(ActorStartModule.class)
                        //
                        // .bindings(bind(actorClass).qualifiedWith(actor.getActorName()).toInstance(subject))
                        .overrides(bind(actor.getActorClass()).to(actorClass))
                        .build();
        Helpers.start(application);
    }

    public <T> void setup(List<ACTOR_NAMES> actors, Class actorClass) {
        GuiceApplicationBuilder applicationBuilder =
                new GuiceApplicationBuilder()
                        .in(new File("path/to/app"))
                        .in(Mode.TEST)
                        .disable(StartModule.class);
        for (ACTOR_NAMES actor : actors) {
            applicationBuilder = applicationBuilder.overrides(bind(actor.getActorClass()).to(actorClass));
        }
        application = applicationBuilder.build();
        Helpers.start(application);
    }

    public Result performTest(String url, String method, Map map) {
        String data = mapToJson(map);
        Http.RequestBuilder req;
        if (StringUtils.isNotBlank(data)) {
            JsonNode json = Json.parse(data);
            req = new Http.RequestBuilder().bodyJson(json).uri(url).method(method);
        } else {
            req = new Http.RequestBuilder().uri(url).method(method);
        }
        Result result = Helpers.route(application, req);
        return result;
    }

    public String mapToJson(Map map) {
        ObjectMapper mapperObj = new ObjectMapper();
        String jsonResp = "";

        if (map != null) {
            try {
                jsonResp = mapperObj.writeValueAsString(map);
            } catch (IOException e) {
            }
        }
        return jsonResp;
    }

    public int getResponseStatus(Result result) {
        return result.status();
    }

}
