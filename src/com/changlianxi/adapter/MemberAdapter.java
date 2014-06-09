package com.changlianxi.adapter;

import java.util.List;
import java.util.regex.Pattern;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;

/**
 * 用来显示圈子成员的自定义adapter
 * 
 * @author teeker_bin
 * 
 */
public class MemberAdapter extends BaseAdapter {
    private Context context;
    private boolean isAuth = true;
    private List<CircleMember> circleMembers;
    private FinalBitmap fb;

    public MemberAdapter(Context context, List<CircleMember> circleMembers) {
        this.circleMembers = circleMembers;
        this.context = context;
        fb = FinalBitmap.create(context);
        fb.configLoadingImage(R.drawable.head_bg);
        fb.configLoadfailImage(R.drawable.head_bg);
    }

    @Override
    public int getCount() {
        return circleMembers.size();
    }

    public void setAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }

    public void setData(List<CircleMember> list) {
        this.circleMembers = list;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        CircleMemberState state = circleMembers.get(position).getState();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.user_list_item, null);
            holder.img = (CircularImage) convertView.findViewById(R.id.userimg);
            holder.info = (TextView) convertView.findViewById(R.id.userinfo);
            holder.name = (TextView) convertView.findViewById(R.id.username);
            holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
            holder.news = (TextView) convertView.findViewById(R.id.userdt);
            holder.changeBg = (RelativeLayout) convertView
                    .findViewById(R.id.changebg);
            holder.authState = (TextView) convertView
                    .findViewById(R.id.authState);
            holder.btnWarn = (Button) convertView.findViewById(R.id.btnWarn);
            holder.line = (View) convertView.findViewById(R.id.line);
            holder.lastName = (TextView) convertView
                    .findViewById(R.id.lastName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.authState.setVisibility(View.VISIBLE);
        holder.btnWarn.setVisibility(View.GONE);
        String employer = circleMembers.get(position).getEmployer();
        if (circleMembers.get(position).getPrivacySettings().contains("5")) {
            holder.info.setText(circleMembers.get(position).getLocation());
        } else {
            holder.info
                    .setText("".equals(employer) || "null".equals(employer) ? circleMembers
                            .get(position).getLocation() : employer);
        }
        String name = StringUtils.cutEight(circleMembers.get(position)
                .getName());
        holder.name.setText(name);
        holder.lastName.setText(name.substring(name.length() - 1));
        if (isAuth) {
            holder.news.setText(circleMembers.get(position).getCellphone());
            authState(holder, state);
        } else {
            holder.news.setText(StringUtils.replaceNum(circleMembers.get(
                    position).getCellphone()));
            authState(holder, state);
        }
        showAlpha(position, holder);
        String path = circleMembers.get(position).getAvatar();
        if (path == null || "".equals(path)) {
            holder.img.setImageResource(R.drawable.head_bg);
            holder.img.setVisibility(View.GONE);

        } else {
            holder.img.setVisibility(View.VISIBLE);
             fb.display(holder.img, path);
//            FinalBitmapLoadTool.display(path, holder.img, R.drawable.head_bg);

        }
        holder.btnWarn.setOnClickListener(new BtnWranClick(position));
        return convertView;
    }

    private void showAlpha(int position, ViewHolder holder) {
        // 当前联系人的sortKey
        String currentStr = getAlpha(circleMembers.get(position).getSortkey());
        // 上一个联系人的sortKey
        String previewStr = (position - 1) >= 0 ? getAlpha(circleMembers.get(
                position - 1).getSortkey()) : " ";
        /**
         * 判断显示#、A-Z的TextView隐藏与显示
         */
        if (!previewStr.equals(currentStr)) { // 当前联系人的sortKey与上一个联系人的sortKey不同，说明当前联系人是新组
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr);
            holder.line.setVisibility(View.VISIBLE);
        } else {
            holder.alpha.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
        }
    }

    private void authState(ViewHolder holder, CircleMemberState state) {
        if (state.equals(CircleMemberState.STATUS_VERIFIED)) {
            holder.authState.setText("已认证");
            holder.authState.setBackgroundResource(R.drawable.auth);
        } else if (state.equals(CircleMemberState.STATUS_ENTER_AND_VERIFYING)) {
            holder.authState.setText("认证中");
            holder.authState.setBackgroundResource(R.drawable.btn_aaaaaa);
        } else if (state.equals(CircleMemberState.STATUS_INVITING)) {
            holder.authState.setText("未加入");
            holder.authState.setBackgroundResource(R.drawable.btn_aaaaaa);
            if (isAuth) {
                holder.btnWarn.setVisibility(View.VISIBLE);
            } else {
                holder.btnWarn.setVisibility(View.GONE);
            }
        } else if (state.equals(CircleMemberState.STATUS_KICKOFFING)) {
            holder.authState.setText("已认证");
            holder.authState.setBackgroundResource(R.drawable.auth);
        } else {
            holder.authState.setVisibility(View.GONE);
        }
    }

    /**
     * 提取英文的首字母，非英文字母用#代替
     * 
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }

        if (str.trim().length() == 0) {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase(); // 大写输出
        } else {
            return "#";
        }

    }

    class BtnWranClick implements OnClickListener {
        private int position;

        public BtnWranClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Circle c = new Circle(circleMembers.get(position).getCid());
            c.getCircleName(DBUtils.getDBsa(1));
            String circleName = c.getName();
            String content = Utils.getWarnContent(circleMembers, circleMembers
                    .get(position).getName(), circleName,
                    circleMembers.get(position).getInviteCode());
            Utils.sendSMS(context, content, circleMembers.get(position)
                    .getCellphone());
            ((Activity) context).overridePendingTransition(
                    R.anim.in_from_right, R.anim.out_to_left);
        }
    }

    class ViewHolder {
        TextView authState;
        CircularImage img;
        TextView info;
        TextView news;
        TextView name;
        TextView alpha;
        RelativeLayout changeBg;
        Button btnWarn;
        View line;
        TextView lastName;

    }
}
