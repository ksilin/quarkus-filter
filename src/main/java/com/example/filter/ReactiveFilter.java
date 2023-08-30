package com.example.filter;

import com.example.filter.utils.MessageFilterHelper;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@Startup
@ApplicationScoped
public class ReactiveFilter {

    Logger log = Logger.getLogger(ReactiveFilter.class);

    @ConfigProperty(name = "filter.criteria")
    String filterString;

    @Incoming("unfiltered-incoming")
    @Outgoing("filtered-outgoing")
    public String produceFilterTopology(String msg) {
        if (MessageFilterHelper.isMessageValid(msg, filterString)){
            log.infov("VALID message {0}", msg);
            return msg;
        }
        log.infov("INVALID message {0}", msg);
        return null;
    }

}
