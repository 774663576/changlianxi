package com.changlianxi.data;

import java.util.Comparator;

import com.changlianxi.data.enums.ChatType;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.StringUtils;

/**
 * Abstract Chat data, can be extended by CircleChat and PersonChat
 * 
 * @author nnjme
 * 
 */
public class AbstractChat extends AbstractData {

    protected int chatId = 0;
    protected ChatType type = ChatType.TYPE_TEXT;
    protected String content = "";
    protected String time = "";

    public AbstractChat(int chatId) {
        this.chatId = chatId;
    }

    public AbstractChat(int chatId, ChatType type, String content, String time) {
        super();
        this.chatId = chatId;
        this.type = type;
        this.content = content;
        this.time = time;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        if (type == ChatType.TYPE_IMAGE) {
            return content;
        }
        return "";
    }

    public String getImage(String size) {
        if (type == ChatType.TYPE_IMAGE) {
            return StringUtils.JoinString(content, size);
        }
        return "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public String getFormatTime() {
        return DateUtils.formatTime(time);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static Comparator<AbstractChat> getComparator(boolean byTimeAsc) {
        if (byTimeAsc) {
            return new Comparator<AbstractChat>() {
                @Override
                public int compare(AbstractChat l, AbstractChat r) {
                    long lTime = DateUtils.convertToDate(l.getTime()), rTime = DateUtils
                            .convertToDate(r.getTime());
                    return lTime > rTime ? 1 : -1;
                }
            };
        } else {
            return new Comparator<AbstractChat>() {
                @Override
                public int compare(AbstractChat l, AbstractChat r) {
                    long lTime = DateUtils.convertToDate(l.getTime()), rTime = DateUtils
                            .convertToDate(r.getTime());
                    return lTime > rTime ? -1 : 1;
                }
            };
        }
    }

}
