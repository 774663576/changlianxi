package com.changlianxi.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.Global;
import com.changlianxi.data.MyCard;
import com.changlianxi.data.PersonChat;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.db.DBUtils;
import com.changlianxi.showBigPic.ImagePagerActivity;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.EmojiParser;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.EmojiEditText;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class MessageAdapter extends BaseAdapter {
    private Context mContext;
    private List<PersonChat> listModle;
    private String selfAvatar = "";
    private String otherAvater = "";
    private String otherName = "";
    private int pid;
    private final int TYPE_1 = 0;
    private final int TYPE_2 = 1;

    public MessageAdapter(Context context, List<PersonChat> chatsList,
            String otherAvatar, String otherName, int pid) {
        this.mContext = context;
        this.listModle = chatsList;
        MyCard card = new MyCard(0, Global.getIntUid());
        card.read(DBUtils.getDBsa(1));
        selfAvatar = card.getAvatar();
        this.pid = pid;
        this.otherAvater = otherAvatar;
        this.otherName = otherName;

    }

    @Override
    public int getCount() {
        return listModle.size();
    }

    public void setData(List<PersonChat> listModle) {
        this.listModle = listModle;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        boolean isSelf = isSelfSend(listModle.get(position).getSender() + "");
        if (isSelf) {
            return TYPE_1;
        } else {
            return TYPE_2;
        }
    }

    /** 
     * 返回所有的layout的数量 
     *  
     * */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderSelf selfHolder = null;
        ViewHolderOther otherHolder = null;
        ChatType type = listModle.get(position).getType();
        String content = listModle.get(position).getContent();
        int cid = listModle.get(position).getCid();
        int uid = listModle.get(position).getPartner();
        int viewType = getItemViewType(position);
        if (convertView == null) {
            switch (viewType) {
                case TYPE_1:
                    selfHolder = new ViewHolderSelf();
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.chat_self_item, null);
                    selfHolder.selfLayout = (RelativeLayout) convertView
                            .findViewById(R.id.selfLayout);
                    selfHolder.selfContent = (EmojiEditText) convertView
                            .findViewById(R.id.selfContent);
                    selfHolder.selfAvatar = (CircularImage) convertView
                            .findViewById(R.id.selfAvatar);
                    selfHolder.selfImg = (ImageView) convertView
                            .findViewById(R.id.selfImg);
                    selfHolder.time = (TextView) convertView
                            .findViewById(R.id.time);
                    convertView.setTag(selfHolder);
                    break;
                case TYPE_2:
                    otherHolder = new ViewHolderOther();
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.chat_other_item, null);
                    otherHolder.otherLayout = (RelativeLayout) convertView
                            .findViewById(R.id.otherLayout);
                    otherHolder.otherContent = (EmojiEditText) convertView
                            .findViewById(R.id.otherContent);
                    otherHolder.otherAvatar = (CircularImage) convertView
                            .findViewById(R.id.otherAvatar);
                    otherHolder.otherName = (TextView) convertView
                            .findViewById(R.id.otherName);
                    otherHolder.otherImg = (ImageView) convertView
                            .findViewById(R.id.otherImg);
                    otherHolder.time = (TextView) convertView
                            .findViewById(R.id.time);
                    convertView.setTag(otherHolder);

                    break;
                default:
                    break;
            }
        } else {
            switch (viewType) {
                case TYPE_1:
                    selfHolder = (ViewHolderSelf) convertView.getTag();
                    break;
                case TYPE_2:
                    otherHolder = (ViewHolderOther) convertView.getTag();
                    break;
                default:
                    break;
            }
        }

        switch (viewType) {
            case TYPE_1:
                if (type == ChatType.TYPE_TEXT) {
                    selfHolder.selfImg.setVisibility(View.GONE);
                    selfHolder.selfContent.setVisibility(View.VISIBLE);
                } else if (type == ChatType.TYPE_IMAGE) {
                    selfHolder.selfImg.setVisibility(View.VISIBLE);
                    selfHolder.selfContent.setVisibility(View.GONE);
                    if (!content.startsWith("http")) {
                        content = "file://" + content;
                    }
                    UniversalImageLoadTool.disPlay(content,
                            new RotateImageViewAware(selfHolder.selfImg,
                                    content), R.drawable.empty_photo);
                }
                selfHolder.selfLayout.setVisibility(View.VISIBLE);
                selfHolder.selfContent.setText(EmojiParser
                        .demojizedText(content + " "));
                if (selfAvatar == null || selfAvatar.equals("")) {
                    selfHolder.selfAvatar.setImageResource(R.drawable.head_bg);
                } else {
                    UniversalImageLoadTool.disPlay(selfAvatar,
                            new RotateImageViewAware(selfHolder.selfAvatar,
                                    selfAvatar), R.drawable.head_bg);

                }
                selfHolder.selfImg.setOnClickListener(new ImageOnClick(content
                        .replace("file://", "")));
                showTimeSelf(position, selfHolder);
                break;
            case TYPE_2:
                if (type == ChatType.TYPE_TEXT) {
                    otherHolder.otherImg.setVisibility(View.GONE);
                    otherHolder.otherContent.setVisibility(View.VISIBLE);
                } else if (type == ChatType.TYPE_IMAGE) {
                    otherHolder.otherImg.setVisibility(View.VISIBLE);
                    otherHolder.otherContent.setVisibility(View.GONE);

                    UniversalImageLoadTool.disPlay(content,
                            new RotateImageViewAware(otherHolder.otherImg,
                                    content), R.drawable.empty_photo);
                }
                otherHolder.otherLayout.setVisibility(View.VISIBLE);
                otherHolder.otherContent.setText(EmojiParser
                        .demojizedText(content + " "));
                otherHolder.otherName.setText(otherName);
                String path = otherAvater;
                if (path == null || path.equals("")) {
                    otherHolder.otherAvatar
                            .setImageResource(R.drawable.head_bg);

                } else {
                    UniversalImageLoadTool.disPlay(path,
                            new RotateImageViewAware(otherHolder.otherAvatar,
                                    path), R.drawable.head_bg);
                }
                otherHolder.otherAvatar.setOnClickListener(new OnAvatarClick(
                        cid, uid, pid, otherName, otherAvater));
                showTime(position, otherHolder);
                otherHolder.otherImg.setOnClickListener(new ImageOnClick(
                        content));

                break;
            default:
                break;
        }
        return convertView;
    }

    class ViewHolder {
        RelativeLayout otherLayout;
        EmojiEditText otherContent;
        TextView otherName;
        CircularImage otherAvatar;
        RelativeLayout selfLayout;
        EmojiEditText selfContent;
        CircularImage selfAvatar;
        ImageView selfImg;
        ImageView otherImg;
        TextView time;
    }

    class ViewHolderSelf {
        RelativeLayout selfLayout;
        EmojiEditText selfContent;
        CircularImage selfAvatar;
        ImageView selfImg;
        TextView time;
    }

    class ViewHolderOther {
        RelativeLayout otherLayout;
        EmojiEditText otherContent;
        TextView otherName;
        CircularImage otherAvatar;
        ImageView otherImg;
        TextView time;
    }

    class OnAvatarClick implements OnClickListener {
        int uid;
        int cid;
        int pid;
        String name = "";
        String avatarImg;
        int intentType = 0;

        public OnAvatarClick(int cid, int uid, int pid, String name,
                String avatarImg) {
            this.cid = cid;
            this.uid = uid;
            this.pid = pid;
            this.name = name;
            this.avatarImg = avatarImg;
        }

        @Override
        public void onClick(View v) {
            // Utils.intentUserDetailActivity(mContext, cid, uid, pid, name,
            // avatarImg);
            // ((Activity) mContext).overridePendingTransition(
            // R.anim.in_from_right, R.anim.out_to_left);

        }
    }

    class LoadImage implements ImageLoadingListener {
        private ImageView img;

        public LoadImage(ImageView img) {
            this.img = img;
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {

        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            img.setImageBitmap(arg2);

        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {

        }

    }

    class ImageOnClick implements OnClickListener {
        String url;

        public ImageOnClick(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            List<String> imgUrl = new ArrayList<String>();
            imgUrl.add(url);
            Intent intent = new Intent(mContext, ImagePagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                    (Serializable) imgUrl);
            intent.putExtras(bundle);
            intent.putExtra(Constants.EXTRA_IMAGE_INDEX, 1);
            mContext.startActivity(intent);
        }

    }

    private void showTime(int position, ViewHolderOther holder) {
        if (position == 0) {
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(DateUtils.publishedTime3(listModle
                    .get(position).getTime()));
            return;
        }
        // 当前联系人的时间
        String endTime = listModle.get(position).getTime();
        // 上一个联系人的时间
        String startTime = listModle.get(position - 1).getTime();
        /**
         * 判断时间间隔
         */
        if (DateUtils.compareDate(startTime, endTime, 1)) {
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(DateUtils.publishedTime3(endTime));
        } else {
            holder.time.setVisibility(View.GONE);
        }
    }

    private void showTimeSelf(int position, ViewHolderSelf holder) {
        if (position == 0) {
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(DateUtils.publishedTime3(listModle
                    .get(position).getTime()));
            return;
        }
        // 当前联系人的时间
        String endTime = listModle.get(position).getTime();
        // 上一个联系人的时间
        String startTime = listModle.get(position - 1).getTime();
        /**
         * 判断时间间隔
         */
        if (DateUtils.compareDate(startTime, endTime, 1)) {
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(DateUtils.publishedTime3(endTime));
        } else {
            holder.time.setVisibility(View.GONE);
        }
    }

    private boolean isSelfSend(String uid) {
        if (uid.equals(Global.getUid())) {
            return true;
        }
        return false;
    }

}
