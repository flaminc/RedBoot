package com.github.flaminc.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.dean.jraw.Dimension;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.JrawModelsHelper;
import net.dean.jraw.models.MoreChildren;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

interface DimensionMixIn {
    @JsonProperty("width")
    public int width();

    @JsonProperty("height")
    public int height();
}

interface SubmissionMixIn {
}

interface CommentNodeMixIn {
    // prevent serialization from causing more client calls
    @JsonIgnore
    public List<Thing> getMoreComments(RedditClient reddit) throws NetworkException;

    // prevent serialization from causing more client calls
    @JsonIgnore
    public MoreChildren getMoreChildren();

    // break graph cycle that will kill stack by infinite recursion
    @JsonIgnore
    public CommentNode getParent();
}

/**
 * Allow wiring beans for 3rd party library JRAW
 */
@Configuration
public class ThirdPartyConfiguration {
    @Bean
    public RedditClient redditClient(UserAgent userAgent) {
        return new RedditClient(userAgent);
    }

    @Bean
    public UserAgent userAgent(ClientUserAgent clientUserAgent) {
        return UserAgent.of(clientUserAgent.getPlatform(), clientUserAgent.getAppId(),
                clientUserAgent.getVersion(), clientUserAgent.getRedditUsername());
    }

    @Bean
    public Module jrawModule() throws ClassNotFoundException {
        return new SimpleModule("JRAW", new Version(1, 0, 0, null, null, null)) {
            @Override public void setupModule(SetupContext context) {
                // Jackson throws exception when accessing getter for RootComment.getParentId and other properties in
                // RootComment class. Use a mixin to ignore these fields
                // The class is protected so must do this in the classes package
                JrawModelsHelper.setupModule(context);
                context.setMixInAnnotations(Dimension.class, DimensionMixIn.class);
                context.setMixInAnnotations(Submission.class, SubmissionMixIn.class);
                context.setMixInAnnotations(CommentNode.class, CommentNodeMixIn.class);
            }
        };
    }
}