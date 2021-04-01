package com.qingboat.as.utils;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class RssUtil {

    public static SyndFeed readRss(String url) throws IOException, FeedException {

        XmlReader reader = new XmlReader(new URL(url));

        SyndFeed feed = new SyndFeedInput().build(reader);
        log.info( "SyndFeed.title     :" +feed.getTitle());
        log.info( "SyndFeed.author    :" +feed.getAuthor());
        log.info( "SyndFeed.feedType  :" +feed.getFeedType());
        log.info( "SyndFeed.copyright :" +feed.getCopyright());
        log.info( "SyndFeed.desc      :" +feed.getDescription());

        System.out.println("***********************************");

        for (SyndEntry entry : feed.getEntries()) {
            System.out.println(entry);
            System.out.println("***********************************");
        }
        return feed;
    }
}
