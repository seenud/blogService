package com.cisco.cmad.blogService.dto;

import java.io.Serializable;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;



@Entity("BlogDTO")
public class BlogDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    private ObjectId id;
	private String blogId;
    private String userId;
    private String title;
    private String content;
    private String tags;
    private Map<String,Map<String, String>> comments;
    
    
    public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
    public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
    
	public String getBlogId() {
		return blogId;
	}
	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Map<String,Map<String, String>> getComments() {
		return comments;
	}
	public void setComments(Map<String,Map<String, String>> comments) {
		this.comments = comments;
	}
	
	
	@Override
	public String toString() {
		return "BlogDTO [blogId=" + blogId + ", userId=" + userId + ", title="
				+ title + ", content=" + content + ", comments=" + comments
				+ "]";
	}
    
}
