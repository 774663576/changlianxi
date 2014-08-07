package com.changlianxi.adapter;

import java.util.List;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.CircleMember;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.inteface.DelGroupListener;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.view.CircularImage;

public class EditCircleGroupAdapter extends BaseAdapter {
    private Context mContext;
    private DelGroupListener mDelGroup;
    private List<CircleMember> listMembers;

    public EditCircleGroupAdapter(Context context, DelGroupListener delGroup,
            List<CircleMember> listMembers) {
        this.mContext = context;
        this.mDelGroup = delGroup;
        this.listMembers = listMembers;
    }

    @Override
    public int getCount() {
        return listMembers.size();
    }

    public void setData(List<CircleMember> listMembers) {
        this.listMembers = listMembers;
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
        ViewHolder holer = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.circle_group_member_list_item, null);
            holer = new ViewHolder();
            holer.imgDel = (ImageView) convertView.findViewById(R.id.img_del);
            holer.imgAvatar = (CircularImage) convertView
                    .findViewById(R.id.img_avatar);
            holer.lastName = (TextView) convertView.findViewById(R.id.lastName);
            holer.name = (TextView) convertView.findViewById(R.id.txt_name);
            holer.info = (TextView) convertView.findViewById(R.id.txt_info);
            holer.cellPhone = (TextView) convertView.findViewById(R.id.txt_tel);
            holer.alpha = (TextView) convertView.findViewById(R.id.alpha);
            convertView.setTag(holer);
        } else {
            holer = (ViewHolder) convertView.getTag();
        }
        String avatarUrl = listMembers.get(position).getAvatar();
        if ("".equals(avatarUrl)) {
            holer.imgAvatar.setImageResource(R.drawable.head_bg);
            holer.imgAvatar.setVisibility(View.GONE);

        } else {
            UniversalImageLoadTool.disPlay(avatarUrl, holer.imgAvatar,
                    R.drawable.head_bg);
            holer.imgAvatar.setVisibility(View.VISIBLE);
        }
        holer.name.setText(listMembers.get(position).getName());
        if (!listMembers.get(position).getName().equals("")) {
            holer.lastName
                    .setText(listMembers
                            .get(position)
                            .getName()
                            .substring(
                                    listMembers.get(position).getName()
                                            .length() - 1));
        }
        holer.cellPhone.setText(listMembers.get(position).getCellphone());
        holer.info.setText(listMembers.get(position).getEmployer());
        showAlpha(position, holer);
        holer.imgDel.setOnClickListener(new DelListener(position));
        return convertView;
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

    private void showAlpha(int position, ViewHolder holder) {
        // 当前联系人的sortKey
        String currentStr = getAlpha(listMembers.get(position).getSortkey());
        // 上一个联系人的sortKey
        String previewStr = (position - 1) >= 0 ? getAlpha(listMembers.get(
                position - 1).getSortkey()) : " ";
        /**
         * 判断显示#、A-Z的TextView隐藏与显示
         */
        if (!previewStr.equals(currentStr)) { // 当前联系人的sortKey与上一个联系人的sortKey不同，说明当前联系人是新组
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr);
        } else {
            holder.alpha.setVisibility(View.GONE);
        }
    }

    class ViewHolder {
        ImageView imgDel;
        TextView lastName;
        CircularImage imgAvatar;
        TextView name;
        TextView cellPhone;
        TextView info;
        TextView alpha;
    }

    private void delPrompt(final int position) {
        Dialog dialog = DialogUtil.confirmDialog(mContext, "确定要删除吗", "确定",
                "取消", new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        mDelGroup.delGroup(position);
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    private class DelListener implements OnClickListener {
        private int position;

        public DelListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            delPrompt(position);
        }

    }

}
