package com.changlianxi.modle;

/**
 * 获取圈子成长中跟我相关的评论列表内容，
 * 
 * @author teeker_bin
 * 
 */
public class NewsComments {
	private int num;// 评论数量
	private String id = "";// 评论ID
	private String gid = ""; // 评论的成长ID
	private String publisher = "";// 评论成长的发布者UID
	private String uid = "";// 评论发布者ID
	private String content = "";// 评论内容
	private String replyid = "";// 评论回复的用户ID
	private String time = "";
	private String name = "";// 评论发布者姓名
	private String avatar = "";// 评论发布者头像

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReplyid() {
		return replyid;
	}

	public void setReplyid(String replyid) {
		this.replyid = replyid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}// 评论时间

}
