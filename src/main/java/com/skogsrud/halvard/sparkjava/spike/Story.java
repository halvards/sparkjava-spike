package com.skogsrud.halvard.sparkjava.spike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Story {
    private String createdAt;
    private String modifiedAt;
    private String id;
    private String type;
    private Map<String, Object> document;
    private boolean sponsored;
    private String shareUrl;
    private String revision;
    private Links links = new Links();

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getDocument() {
        return document;
    }

    public void setDocument(Map<String, Object> document) {
        this.document = document;
    }

    public boolean isSponsored() {
        return sponsored;
    }

    public void setSponsored(boolean sponsored) {
        this.sponsored = sponsored;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public class Links {
        private String self;
        private String channel;
        private List<String> sections = new ArrayList<>();

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public List<String> getSections() {
            return Collections.unmodifiableList(sections);
        }

        public void setSections(List<String> sections) {
            this.sections = new ArrayList<>(sections);
        }
    }
}
