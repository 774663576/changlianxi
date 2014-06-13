package com.changlianxi.inteface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.changlianxi.NoExistPersonInfoActivity;
import com.changlianxi.R;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.Utils;

public class OnAvatarClickListener implements OnClickListener {
    private int uid;
    private int cid;
    private int pid;
    private String name = "";
    private String avatarImg;
    private Context context;
    private int intentType = 0;

    public OnAvatarClickListener(Context context, int cid, int uid, int pid,
            String name, String avatarImg, int intentType) {
        this.cid = cid;
        this.uid = uid;
        this.pid = pid;
        this.name = name;
        this.avatarImg = avatarImg;
        this.context = context;
        this.intentType = intentType;
    }

    @Override
    public void onClick(View v) {
        CircleMember member = new CircleMember(cid, pid, uid);
        member.read(DBUtils.getDBsa(1));
        boolean isMemberEmpty = (member.getName() == ""); // TODO new way to judge member is empty?
        CircleMember self = new CircleMember(cid, 0, Global.getIntUid());
        if (!isMemberEmpty && !self.isAuth(DBUtils.getDBsa(1))) {
            Utils.showToast("非认证成员暂时看不到其他人的详细信息，快去找朋友帮您认证吧", Toast.LENGTH_SHORT);
            return;
        }
        if (member.getState().equals(CircleMemberState.STATUS_QUIT)
                || member.getState().equals(CircleMemberState.STATUS_KICKOUT)
                || member.getState().equals(CircleMemberState.STATUS_INVALID)
                || member.getState().equals(CircleMemberState.STATUS_REFUSED)) {
            Intent it = new Intent();
            it.setClass(context, NoExistPersonInfoActivity.class);
            it.putExtra("name", name);
            it.putExtra("avatar", avatarImg);
            context.startActivity(it);
        } else {
            Utils.intentUserDetailActivity(context, cid, uid, pid, name,
                    avatarImg);
        }
        switch (intentType) {
            case 0:
                ((Activity) context).overridePendingTransition(
                        R.anim.in_from_right, R.anim.out_to_left);
                break;
            case 1:
                ((Activity) context).overridePendingTransition(
                        R.anim.in_from_right, R.anim.out_to_left);
                break;
            default:
                break;
        }
    }
}
