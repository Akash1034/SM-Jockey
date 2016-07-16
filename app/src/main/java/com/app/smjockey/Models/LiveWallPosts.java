package com.app.smjockey.Models;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Akash Srivastava on 13-07-2016.
 */
public class LiveWallPosts implements Serializable{


    public Account account;
    public String created_at;
    public String highlight;
    public String id;
    public String inserted_at;
    public Json json;
    public String livewall;
    public String original;
    public String p_id;
    public List<Photo> photos;
    public String source;
    public String text;
    public String type;

    @JsonCreator
    public LiveWallPosts() {
    }



    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInserted_at() {
        return inserted_at;
    }

    public void setInserted_at(String inserted_at) {
        this.inserted_at = inserted_at;
    }

    public Json getJson() {
        return json;
    }

    public void setJson(Json json) {
        this.json = json;
    }

    public String getLivewall() {
        return livewall;
    }

    public void setLivewall(String livewall) {
        this.livewall = livewall;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }


    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public class Account
    {
        private String id;
        private String username;
        private String verified;
        private String profile_image;
        private String name;

        @JsonCreator
        public Account() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getVerified() {
            return verified;
        }

        public void setVerified(String verified) {
            this.verified = verified;
        }

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Json
    {
        private Entities entities;
        private String text;
        private String likes;
        private String retweets;

        @JsonCreator
        public Json() {
        }

        public Entities getEntities() {
            return entities;
        }

        public void setEntities(Entities entities) {
            this.entities = entities;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getLikes() {
            return likes;
        }

        public void setLikes(String likes) {
            this.likes = likes;
        }

        public String getRetweets() {
            return retweets;
        }

        public void setRetweets(String retweets) {
            this.retweets = retweets;
        }

        public class Entities
        {
            private String full_url;
            private String source;
            private String thumb_url;

            public Entities() {
            }

            public String getFull_url() {
                return full_url;
            }

            public void setFull_url(String full_url) {
                this.full_url = full_url;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getThumb_url() {
                return thumb_url;
            }

            public void setThumb_url(String thumb_url) {
                this.thumb_url = thumb_url;
            }
        }

    }


}

class Photo
{
    private String created_at;
    private String full_url;
    private String source;
    private String thumb_url;

    @JsonCreator
    public Photo() {
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFull_url() {
        return full_url;
    }

    public void setFull_url(String full_url) {
        this.full_url = full_url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }
}