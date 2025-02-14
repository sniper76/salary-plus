package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.PostResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * ActBestPosts
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class ActBestPosts {

  private String link;

  @Valid
  private List<@Valid PostResponse> posts;

  public ActBestPosts link(String link) {
    this.link = link;
    return this;
  }

  /**
   * url to route to when \"더보기\" button tapped
   * @return link
  */
  
  @JsonProperty("link")
  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public ActBestPosts posts(List<@Valid PostResponse> posts) {
    this.posts = posts;
    return this;
  }

  public ActBestPosts addPostsItem(PostResponse postsItem) {
    if (this.posts == null) {
      this.posts = new ArrayList<>();
    }
    this.posts.add(postsItem);
    return this;
  }

  /**
   * Get posts
   * @return posts
  */
  @Valid 
  @JsonProperty("posts")
  public List<@Valid PostResponse> getPosts() {
    return posts;
  }

  public void setPosts(List<@Valid PostResponse> posts) {
    this.posts = posts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ActBestPosts actBestPosts = (ActBestPosts) o;
    return Objects.equals(this.link, actBestPosts.link) &&
        Objects.equals(this.posts, actBestPosts.posts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(link, posts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ActBestPosts {\n");
    sb.append("    link: ").append(toIndentedString(link)).append("\n");
    sb.append("    posts: ").append(toIndentedString(posts)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

