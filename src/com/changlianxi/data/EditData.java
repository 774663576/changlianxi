package com.changlianxi.data;

import java.io.Serializable;

import com.changlianxi.data.enums.PersonDetailType;

/**
 * 修改的资料
 * @author teeker_bin
 *
 */
public class EditData implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int id;
    private int user_id;
    private PersonDetailType type = PersonDetailType.UNKNOWN;
    private String operation = "";
    private String detail = "";
    private boolean checked = false;
    private String time;
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public EditData() {
        super();
        // TODO Auto-generated constructor stub
    }

    public EditData(int id, PersonDetailType type, String operation,
            String detail) {
        this.id = id;
        this.type = type;
        this.operation = operation;
        this.detail = detail;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PersonDetailType getType() {
        return type;
    }

    public void setType(PersonDetailType type) {
        this.type = type;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "[id=" + id + "]" + "[type=" + type + "]" + "[operation="
                + operation + "]" + "[detail=" + detail + "]";
    }
}
