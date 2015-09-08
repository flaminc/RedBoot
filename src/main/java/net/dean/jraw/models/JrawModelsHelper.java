package net.dean.jraw.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.Module;

import java.util.Date;

/**
 * Fields that aren't valid for a root comment
 */
interface RootCommentMixIn {
    @JsonIgnore
    public String getParentId();

    @JsonIgnore
    public Flair getAuthorFlair();

    @JsonIgnore
    public Integer getScore();

    @JsonIgnore
    public VoteDirection getVote();

    @JsonIgnore
    public boolean isArchived();

    @JsonIgnore
    public DistinguishedStatus getDistinguishedStatus();

    @JsonIgnore
    public Date getCreated();

    @JsonIgnore
    public Date getCreatedUtc();
}

public class JrawModelsHelper {
    public static void setupModule(Module.SetupContext context) {
        context.setMixInAnnotations(CommentNode.RootComment.class, RootCommentMixIn.class);
        // and other set up, if any
    }
}
