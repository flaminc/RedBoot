package com.github.flaminc.controller;

import com.github.flaminc.config.RedditCredentials;
import com.github.flaminc.exceptions.MissingApiCredentials;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@ConfigurationProperties(prefix = "reddit")
public class RedditController {

    @Autowired
    private RedditClient client;

    @Autowired
    private RedditCredentials redditCredentials;

    /**
     * Cache current credentials
     */
    private Credentials credentials;

    @RequestMapping("/userAgent")
    public String userAgent() {
        return client.getUserAgent();
    }

    @RequestMapping("/thing/{id}")
    public Listing<Thing> thing(@PathVariable("id") String id) {
        return client.get(id);
    }

    @RequestMapping("/comment/{id}")
    public Listing<Thing> comment(@PathVariable("id") String id) {

        return client.get("t1_" + id);
    }

    @RequestMapping("/submission/{id}")
    public Submission submission(@PathVariable("id") String id) {
        return client.getSubmission(id);
    }

    @RequestMapping("/subreddit/{name}")
    public Subreddit subreddit(@PathVariable("name") String name) {
        return client.getSubreddit(name);
    }

    /**
     * Endpoint used to start OAuth process with reddit.com
     *
     * @throws IOException
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.GET)
    public void authenticate(HttpServletRequest request, HttpServletResponse response)
            throws IOException, MissingApiCredentials {

        StringBuffer requestURL = request.getRequestURL();
        // remove request uri from url
        requestURL.setLength(requestURL.length() - request.getRequestURI().length());
        String destination = requestURL.append("/redirect").toString();
        // stop here if secret isn't initialized
        if (redditCredentials.getSecret() == null || redditCredentials.getSecret().length() < 20) {
            // secret seems to be around 27 characters
            throw new MissingApiCredentials("Secret was not set");
        }
        credentials = Credentials.webapp(redditCredentials.getId(), redditCredentials.getSecret(), destination);
        String[] permission = {
                //"edit",
                //"flair",
                //"history",
                //"modconfig",
                //"modflair",
                //"modlog",
                //"modposts",
                //"modwiki",
                //"mysubreddits",
                //"privatemessages",
                //"report",
                //"save",
                //"submit",
                //"subscribe",
                //"wikiedit",
                //"wikiread",
                "identity",
                "vote",
                "read"
        };

        String url = client.getOAuthHelper().getAuthorizationUrl(credentials, false, false, permission).toString();
        response.sendRedirect(url);
    }

    /**
     * Endpoint to finalize OAuth token
     */
    @RequestMapping("/redirect")
    public void credentialRedirect(HttpServletRequest request, HttpServletResponse response)
            throws OAuthException, IOException {

        String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        String finalUrl = request.getRequestURL().toString() + queryString;
        client.authenticate(client.getOAuthHelper().onUserChallenge(finalUrl, credentials));

        response.sendRedirect("/authenticated");
    }

}
