package com.changlianxi.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.ChatPartner;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.OnAvatarClickListener;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.EmojiParser;
import com.changlianxi.util.FinalBitmapLoadTool;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.view.EmojiEditText;

public class MessageListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ChatPartner> listModle;

    public MessageListAdapter(Context context, List<ChatPartner> modle) {
        this.mContext = context;
        this.listModle = modle;
    }

    @Override
    public int getCount() {
        return listModle.size();
    }

    public void setData(List<ChatPartner> modle) {
        this.listModle = modle;
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
        ViewHolder holder = null;
        int cid = listModle.get(position).getCid();
        int uid = listModle.get(position).getPartner();
        String name = listModle.get(position).getPartnerName();
        ChatType type = listModle.get(position).getType();
        CircleMember c = getNameAndAvatar(cid, uid, mContext);
        String avatar = c.getAvatar();
        if (cid == 0) {
            avatar = listModle.get(position).getuAvatar();
        }
        if ("".equals(name)) {
            name = c.getName();
        }
        int pid = c.getPid();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.messages_list_item, null);
            holder = new ViewHolder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.content = (EmojiEditText) convertView
                    .findViewById(R.id.content);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.count = (TextView) convertView.findViewById(R.id.count);
            holder.layParent = (LinearLayout) convertView
                    .findViewById(R.id.parent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String content = listModle.get(position).getContent();
        if (type == ChatType.TYPE_IMAGE) {
            holder.content.setText("【图片】");
        } else {
            holder.content.setText(EmojiParser.demojizedText(content + " "));
        }
        int count = listModle.get(position).getUnReadCnt();
        if (count > 0) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(count + "");
        } else {
            holder.count.setVisibility(View.GONE);
        }
        holder.name.setText(name);
        holder.time.setText(DateUtils.publishedTime3(listModle.get(position)
                .getTime()));
        if (!avatar.startsWith("http")) {
            holder.avatar.setImageResource(R.drawable.head_bg);
        } else {
            // fb.display(holder.avatar, avatar);
            // FinalBitmapLoadTool.display(avatar, holder.avatar,
            // R.drawable.head_bg);

            UniversalImageLoadTool.disPlay(avatar, new RotateImageViewAware(
                    holder.avatar, avatar), R.drawable.head_bg);
        }
        if (position % 2 == 0) {
            holder.layParent.setBackgroundColor(Color.WHITE);
        } else {
            holder.layParent.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.f6));
        }
        holder.avatar.setOnClickListener(new OnAvatarClickListener(mContext,
                cid, uid, pid, name, avatar, 1));
        return convertView;
    }

    private CircleMember getNameAndAvatar(int cid, int uid, Context mContext) {
        CircleMember m = new CircleMember(cid, 0, uid);
        if (!m.readNameAndAvatar(DBUtils.getDBsa(1))) {

        }
        return m;

    }

    class ViewHolder {
        ImageView avatar;
        TextView name;
        TextView time;
        EmojiEditText content;
        TextView count;
        LinearLayout layParent;
    }
}
