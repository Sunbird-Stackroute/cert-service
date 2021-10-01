package utils.module;

import akka.routing.FromConfig;
import akka.routing.RouterConfig;
import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.akka.AkkaGuiceSupport;

public class ActorStartModule extends AbstractModule implements AkkaGuiceSupport {

    Logger logger = LoggerFactory.getLogger(ActorStartModule.class);


    @Override
    protected void configure() {
        logger.info("binding actors for dependency injection");
        final RouterConfig config = new FromConfig();
        for (ACTOR_NAMES actor : ACTOR_NAMES.values()) {
            bindActor(
                    actor.getActorClass(),
                    actor.getActorName(),
                    (props) -> {
                        return props.withRouter(config);
                    });
        }
        logger.info("binding completed");
    }
}
