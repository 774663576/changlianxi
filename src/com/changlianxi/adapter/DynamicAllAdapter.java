package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.CircleDynamic;
import com.changlianxi.data.CircleMember;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.OnAvatarClickListener;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.view.CircularImage;

/**
 * 动态展示
 * 
 * @author teeker_bin
 * 
 */
public class DynamicAllAdapter extends BaseAdapter {
    private Context mCotext;
    private List<CircleDynamic> listModle;

    public DynamicAllAdapter(Context context, List<CircleDynamic> listModle) {
        this.mCotext = context;
        this.listModle = listModle;
    }

    @Override
    public int getCount() {
        return listModle.size();
    }

    public void setData(List<CircleDynamic> listModle) {
        this.listModle = listModle;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取到当前位置所对应的Type
        String avatarUrl = "";
        String content = listModle.get(position).getContent();
        int uid1 = listModle.get(position).getUid1();
        int uid2 = listModle.get(position).getUid2();
        int cid = listModle.get(position).getCid();
        int pid2 = listModle.get(position).getPid2();
        CircleMember m1 = getNameAndAvatar(cid, 0, uid1); // m1 == null
        CircleMember m2 = getNameAndAvatar(cid, pid2, uid2); // m2 == null
        avatarUrl = m1 == null & m2 == null ? "" : m1 == null ? m2.getAvatar()
                : m1.getAvatar();
        String detail = listModle.get(position).getDetail();
        ViewHolderOther holderOther = null;
        if (convertView == null) {
            holderOther = new ViewHolderOther();
            convertView = LayoutInflater.from(mCotext).inflate(
                    R.layout.news_list_item, null);
            holderOther = new ViewHolderOther();
            holderOther.avatar = (CircularImage) convertView
                    .findViewById(R.id.avatar);
            holderOther.content = (TextView) convertView
                    .findViewById(R.id.content);
            holderOther.time = (TextView) convertView.findViewById(R.id.time);
            holderOther.detail = (TextView) convertView
                    .findViewById(R.id.detail);
            convertView.setTag(holderOther);

        } else {
            holderOther = (ViewHolderOther) convertView.getTag();
        }

        holderOther.content
                .setText(Html.fromHtml(replaceUser(content, m1, m2)));
        holderOther.detail.setText(detail);
        holderOther.time.setText(DateUtils.publishedTime3(listModle.get(
                position).getTime()));
        if (avatarUrl == null || avatarUrl.equals("")) {
            holderOther.avatar.setImageResource(R.drawable.head_bg);
        } else {
            UniversalImageLoadTool.disPlay(avatarUrl, 
                    holderOther.avatar, R.drawable.head_bg);
        }
        holderOther.avatar.setOnClickListener(new OnAvatarClickListener(
                mCotext, cid, m1 == null & m2 == null ? 0 : m1 == null ? m2
                        .getUid() : m1.getUid(), m1 == null & m2 == null ? 0
                        : m1 == null ? m2.getPid() : m1.getPid(), m1 == null
                        & m2 == null ? "" : m1 == null ? m2.getName() : m1
                        .getName(), avatarUrl, 0));

        return convertView;

    }

    private CircleMember getNameAndAvatar(int cid, int pid, int uid) {
        if (pid == 0 && uid == 0) {
            return null;
        }

        CircleMember m = new CircleMember(cid, pid, uid);
        if (!m.readNameAndAvatar(DBUtils.getDBsa(1))) {
            m.refreshBasic();
            // m.write(DBUtils.getDBsa());
        }
        return m;
    }

    private String getDetail(String detail) {
        if (detail.equals("")) {
            return detail;
        }
        return "<font color=\"#fd7a00\">(" + detail + ")</font>";

    }

    private String replaceUser(String content, CircleMember cm1,
            CircleMember cm2) {
        if (cm1 != null) {
            String userName1 = "";
            userName1 = "<font color=\"#000000\">"
                    + cm1.getName().replace("<", "<  ").replace(">", "  >")
                    + "</font>";
            content = content.replace("[X]", userName1);
        }
        if (cm2 != null) {
            String userName2 = "";
            userName2 = "<font color=\"#000000\">"
                    + cm2.getName().replace("<", "<  ").replace(">", "  >")
                    + "</font>";
            content = content.replace("[Y]", userName2);
        }
        return StringUtils.ToDBC(content);
    }

    class ViewHolderOther {
        TextView content;
        TextView time;
        CircularImage avatar;
        TextView detail;
    }

    class ViewHolderInvite {
        TextView contentInvite;
        TextView timeInvite;
        CircularImage avatarInvite;
        Button btnAgreeInvite;
        Button btnNotAgreeInvite;

    }

}
