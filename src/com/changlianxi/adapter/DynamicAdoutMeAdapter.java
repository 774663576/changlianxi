package com.changlianxi.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.R;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.CircleDynamic;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.DynamicType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.OnAvatarClickListener;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.FinalBitmapLoadTool;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;

/**
 * 动态展示
 * 
 * @author teeker_bin
 * 
 */
public class DynamicAdoutMeAdapter extends BaseAdapter {
    private Context mCotext;
    private List<CircleDynamic> listModle;
    // private FinalBitmap fb;
    private final int TYPE_1 = 0;
    private final int TYPE_2 = 1;

    public DynamicAdoutMeAdapter(Context context, List<CircleDynamic> listModle) {
        this.mCotext = context;
        this.listModle = listModle;
        // fb = CLXApplication.getFb();
        // fb.configLoadfailImage(R.drawable.head_bg);
        // fb.configLoadingImage(R.drawable.head_bg);
    }

    @Override
    public int getCount() {
        return listModle.size();
    }

    private void notifyData() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        boolean needApproved = listModle.get(position).isNeedApproved();
        if (needApproved) {
            return TYPE_1;
        }
        return TYPE_2;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
        int type = getItemViewType(position);
        String avatarUrl = "";
        int pid = 0;
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
        ViewHolderInvite holderInvite = null;
        ViewHolder holder = null;
        if (convertView == null) {
            switch (type) {
                case TYPE_1:
                    convertView = LayoutInflater.from(mCotext).inflate(
                            R.layout.news_invitate1, null);
                    holderInvite = new ViewHolderInvite();
                    holderInvite.avatarInvite = (CircularImage) convertView
                            .findViewById(R.id.avatarInvite);
                    holderInvite.contentInvite = (TextView) convertView
                            .findViewById(R.id.contentInvite);
                    holderInvite.timeInvite = (TextView) convertView
                            .findViewById(R.id.timeInvite);
                    holderInvite.btnAgreeInvite = (Button) convertView
                            .findViewById(R.id.btnAgree);
                    holderInvite.btnNotAgreeInvite = (Button) convertView
                            .findViewById(R.id.btnNotAgree);
                    convertView.setTag(holderInvite);
                    break;
                case TYPE_2:
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mCotext).inflate(
                            R.layout.news_list_item, null);
                    holder.avatar = (CircularImage) convertView
                            .findViewById(R.id.avatar);
                    holder.content = (TextView) convertView
                            .findViewById(R.id.content);
                    holder.time = (TextView) convertView
                            .findViewById(R.id.time);
                    convertView.setTag(holder);
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case TYPE_1:
                    holderInvite = (ViewHolderInvite) convertView.getTag();
                    break;
                case TYPE_2:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                default:
                    break;
            }
        }
        switch (type) {
            case TYPE_1:
                holderInvite.contentInvite.setText(Html.fromHtml(replaceUser(
                        content, m1, m2) + getDetail(detail)));
                holderInvite.timeInvite.setText(DateUtils
                        .publishedTime3(listModle.get(position).getTime()));
                if (avatarUrl == null || avatarUrl.equals("")) {
                    holderInvite.avatarInvite
                            .setImageResource(R.drawable.head_bg);
                } else {
                    // fb.display(holderInvite.avatarInvite, avatarUrl);
                    FinalBitmapLoadTool.display(avatarUrl,
                            holderInvite.avatarInvite, R.drawable.head_bg);
                }
                holderInvite.btnAgreeInvite.setOnClickListener(new BtnClick(
                        listModle.get(position), position, m1, m2));
                holderInvite.btnNotAgreeInvite.setOnClickListener(new BtnClick(
                        listModle.get(position), position, m1, m2));
                holderInvite.avatarInvite
                        .setOnClickListener(new OnAvatarClickListener(mCotext,
                                cid, m1 == null & m2 == null ? 0
                                        : m1 == null ? m2.getUid() : m1
                                                .getUid(), m1 == null
                                        & m2 == null ? 0 : m1 == null ? m2
                                        .getPid() : m1.getPid(), m1 == null
                                        & m2 == null ? "" : m1 == null ? m2
                                        .getName() : m1.getName(), avatarUrl, 0));
                break;
            case TYPE_2:
                detail = getDetail(detail);
                holder.content.setText(Html.fromHtml(replaceUser(content, m1,
                        m2) + detail));
                holder.time.setText(DateUtils.publishedTime3(listModle.get(
                        position).getTime()));
                if (avatarUrl.equals("") || avatarUrl == null) {
                    holder.avatar.setImageResource(R.drawable.head_bg);
                } else {
                    // fb.display(holder.avatar, avatarUrl);
                    FinalBitmapLoadTool.display(avatarUrl, holder.avatar,
                            R.drawable.head_bg);
                }
                holder.avatar.setOnClickListener(new OnAvatarClickListener(
                        mCotext, cid, m1 == null & m2 == null ? 0
                                : m1 == null ? m2.getUid() : m1.getUid(),
                        m1 == null & m2 == null ? 0 : m1 == null ? m2.getPid()
                                : m1.getPid(), m1 == null & m2 == null ? ""
                                : m1 == null ? m2.getName() : m1.getName(),
                        avatarUrl, 0));
                break;
            default:
                break;
        }

        return convertView;
    }

    private CircleMember getNameAndAvatar(int cid, int pid, int uid) {
        if (pid == 0 && uid == 0) {
            return null;
        }

        CircleMember m = new CircleMember(cid, pid, uid);
        if (!m.readNameAndAvatar(DBUtils.getDBsa(1))) {
            m.refreshBasic();
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

    class ViewHolderInvite {
        TextView contentInvite;
        TextView timeInvite;
        CircularImage avatarInvite;
        Button btnAgreeInvite;
        Button btnNotAgreeInvite;

    }

    class ViewHolder {
        TextView content;
        TextView time;
        CircularImage avatar;
    }

    class BtnClick implements OnClickListener {
        int position;
        CircleDynamic cDynamic = null;
        CircleMember m1;
        CircleMember m2;

        public BtnClick(CircleDynamic cDynamic, int position, CircleMember m1,
                CircleMember m2) {
            this.position = position;
            this.cDynamic = cDynamic;
            this.m1 = m1;
            this.m2 = m2;
        }

        @Override
        public void onClick(View v) {
            CircleMember c = new CircleMember(listModle.get(position).getCid(),
                    0, Global.getIntUid());
            if (!c.isAuth(DBUtils.getDBsa(1))) {
                Utils.showToast("您不是认证成员，不能进行此操作", Toast.LENGTH_SHORT);
                return;
            }
            if (cDynamic.getPid2() == 0) {
                cDynamic.setPid2(m1 == null ? m2.getPid() : m1.getPid());
            }
            switch (v.getId()) {
                case R.id.btnAgree:
                    if (cDynamic.getType().equals(DynamicType.TYPE_KICKOUT)) {
                        kickOutApprove(cDynamic, true, position);
                    } else if (cDynamic.getType().equals(
                            DynamicType.TYPE_ENTERING)) {
                        EnterApprove(cDynamic, true, position);

                    }
                    break;
                case R.id.btnNotAgree:
                    if (cDynamic.getType().equals(DynamicType.TYPE_KICKOUT)) {
                        kickOutApprove(cDynamic, false, position);
                    } else if (cDynamic.getType().equals(
                            DynamicType.TYPE_ENTERING)) {
                        EnterApprove(cDynamic, false, position);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void kickOutApprove(final CircleDynamic cDynamic,
            final boolean attitude, final int position) {
        final Dialog dialog = DialogUtil.getWaitDialog(mCotext, "请稍候");
        dialog.show();
        BaseAsyncTask<Void, Void, RetError> task = new BaseAsyncTask<Void, Void, RetError>() {
            @Override
            protected RetError doInBackground(Void... params) {
                RetError ret = cDynamic.kickoutApprove(attitude);
                return ret;
            }
        };
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (result == RetError.NONE) {
                    Utils.showToast("操作成功", Toast.LENGTH_SHORT);
                    listModle.get(position).setNeedApproved(false);
                    cDynamic.setNeedApproved(false);
                    cDynamic.write(DBUtils.getDBsa(2));
                    notifyData();
                }

            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet();
    }

    private void EnterApprove(final CircleDynamic cDynamic,
            final boolean attitude, final int position) {
        final Dialog dialog = DialogUtil.getWaitDialog(mCotext, "请稍候");
        dialog.show();
        BaseAsyncTask<Void, Void, RetError> task = new BaseAsyncTask<Void, Void, RetError>() {
            @Override
            protected RetError doInBackground(Void... params) {
                RetError ret = cDynamic.enterApprove(attitude);
                return ret;
            }
        };
        task.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (result == RetError.NONE) {
                    Utils.showToast("操作成功", Toast.LENGTH_SHORT);
                    listModle.get(position).setNeedApproved(false);
                    notifyData();
                    cDynamic.setNeedApproved(false);
                    cDynamic.write(DBUtils.getDBsa(2));
                }

            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet();
    }

}
