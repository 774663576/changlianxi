package com.changlianxi.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.GrowthCommentActivity;
import com.changlianxi.GrowthCommentActivity.RecordOperation;
import com.changlianxi.R;
import com.changlianxi.ShareActivity;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthAlbumImages;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.OnAvatarClickListener;
import com.changlianxi.popwindow.GrowthLoadingFailPopwindow;
import com.changlianxi.popwindow.GrowthLoadingFailPopwindow.FailGrowth;
import com.changlianxi.showBigPic.GrowthImagePagerActivity;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.UpLoadNewGrowthTask;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.GrowthImgGridView;
import com.changlianxi.view.GrowthImgGridView.GetClickPosition;

public class GrowthAdapter extends BaseAdapter {
    private List<Growth> listData;
    private Context mContext;
    public boolean isScrolling = false;

    public GrowthAdapter(Context context, List<Growth> modle) {
        this.mContext = context;
        this.listData = modle;

    }

    public void setScrolling(boolean isScrolling) {
        this.isScrolling = isScrolling;

    }

    public void clearCache() {
    }

    public void setData(List<Growth> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listData.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CircleMember m = new CircleMember(listData.get(position).getCid(), 0,
                listData.get(position).getPublisher());
        m.getNameAndAvatar(DBUtils.getDBsa(1));
        String path = m.getAvatar();
        String name = m.getName();
        int pid = m.getPid();
        Growth growth = listData.get(position);
        String content = growth.getContent();
        String location = growth.getLocation();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.growth_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.praise = (TextView) convertView.findViewById(R.id.praise);
            holder.comment = (TextView) convertView.findViewById(R.id.comment);
            holder.location = (TextView) convertView
                    .findViewById(R.id.location);
            holder.layComments = (LinearLayout) convertView
                    .findViewById(R.id.layComment);
            holder.layPraise = (LinearLayout) convertView
                    .findViewById(R.id.layPraise);
            holder.gridView = (GrowthImgGridView) convertView
                    .findViewById(R.id.imgGridview);
            holder.gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            holder.avatar = (CircularImage) convertView
                    .findViewById(R.id.avatar);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.lay = (LinearLayout) convertView
                    .findViewById(R.id.layParent);
            holder.layShare = (LinearLayout) convertView
                    .findViewById(R.id.layShare);
            holder.imgPraise = (ImageView) convertView
                    .findViewById(R.id.imgPraise);
            holder.txtUpLoading = (TextView) convertView
                    .findViewById(R.id.txtUpLoading);
            holder.pbLoading = (ProgressBar) convertView
                    .findViewById(R.id.pbLoading);
            holder.imgLoadingFail = (TextView) convertView
                    .findViewById(R.id.growthLoading_fail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int size = growth.getImages().size();
        if (size == 1) {
            holder.gridView.setVisibility(View.GONE);
            String imgPath = listData.get(position).getImages().get(0).getImg();
            if (imgPath.startsWith("http")) {
                imgPath = StringUtils.JoinString(listData.get(position)
                        .getImages().get(0).getImg(), "_500x500");
            } else {
                imgPath = "file://" + imgPath;
            }
            UniversalImageLoadTool.disPlay(imgPath, new RotateImageViewAware(
                    holder.img, imgPath), R.drawable.empty_photo);
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setOnClickListener(new ImgOnClick(growth.getImages()
                    .get(0).getImg(), position));
        } else {
            holder.img.setVisibility(View.GONE);
            holder.gridView.setVisibility(View.VISIBLE);
            holder.gridView.setAdapter(new GrowthImgAdapter(mContext, growth
                    .getImages()));
            holder.gridView.setOnItemClickListener(new GridViewOnItemClick(
                    position));
            holder.gridView.setCallBack(new GridViewClickPosition(position));
        }
        holder.layPraise.setOnClickListener(new PraiseClick(growth,
                holder.praise, holder.imgPraise));
        holder.layComments.setOnClickListener(new BtnClick(position));
        holder.lay.setOnClickListener(new BtnClick(position));
        holder.avatar.setOnClickListener(new OnAvatarClickListener(mContext,
                growth.getCid(), growth.getPublisher(), pid, name, path, 0));
        if (path == null || path.equals("")) {
            holder.avatar.setImageResource(R.drawable.head_bg);
        } else {
            UniversalImageLoadTool.disPlay(path, new RotateImageViewAware(
                    holder.avatar, path), R.drawable.head_bg);

        }
        holder.name.setText(name);
        holder.time.setText(DateUtils.publishedTime(growth.getPublished()));
        if ("".equals(content)) {
            holder.content.setVisibility(View.GONE);
        } else {
            holder.content.setVisibility(View.VISIBLE);
        }
        if ("".equals(location)) {
            holder.location.setText(DateUtils.getGrowthShowTime(growth
                    .getHappened()));
        } else {
            holder.location.setText(DateUtils.getGrowthShowTime(growth
                    .getHappened()) + "于" + location);
        }
        holder.layShare.setOnClickListener(new BtnClick(position));
        holder.content.setText(content);
        int commentCount = growth.getCommentCnt();
        if (commentCount == 0) {
            holder.comment.setText("");
        } else {
            holder.comment.setText("" + commentCount);
        }
        if (growth.isPraised()) {
            holder.imgPraise.setImageResource(R.drawable.icon_praise1);
        } else {
            holder.imgPraise.setImageResource(R.drawable.icon_praise);

        }
        int praiseCount = growth.getPraiseCnt();
        setPraiseCount(holder.praise, praiseCount);
        if (growth.isUpLoading()) {
            holder.txtUpLoading.setVisibility(View.VISIBLE);
            holder.pbLoading.setVisibility(View.VISIBLE);
        } else {
            holder.txtUpLoading.setVisibility(View.GONE);
            holder.pbLoading.setVisibility(View.GONE);
        }
        if (growth.isLoadingFail()) {
            holder.imgLoadingFail.setVisibility(View.VISIBLE);
            holder.txtUpLoading.setVisibility(View.GONE);
            holder.pbLoading.setVisibility(View.GONE);
        } else {
            holder.imgLoadingFail.setVisibility(View.GONE);

        }
        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView time;
        TextView content;
        TextView praise;
        TextView comment;
        GrowthImgGridView gridView;
        CircularImage avatar;
        TextView location;
        ImageView img;
        LinearLayout lay;
        ImageView imgPraise;
        LinearLayout layPraise;
        LinearLayout layComments;
        LinearLayout layShare;
        TextView txtUpLoading;
        ProgressBar pbLoading;
        TextView imgLoadingFail;
    }

    class GridViewOnItemClick implements OnItemClickListener {
        int position;

        public GridViewOnItemClick(int position) {
            this.position = position;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int posit,
                long arg3) {
            List<GrowthAlbumImages> imgUrl = new ArrayList<GrowthAlbumImages>();
            for (int i = 0; i < listData.get(position).getImages().size(); i++) {
                GrowthAlbumImages img = new GrowthAlbumImages();
                img.setPicPath(listData.get(position).getImages().get(i)
                        .getImg());
                img.setPicID(listData.get(position).getImages().get(i)
                        .getImgId());
                imgUrl.add(img);
            }
            Intent intent = new Intent(mContext, GrowthImagePagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                    (Serializable) imgUrl);
            bundle.putSerializable("growth",
                    (Serializable) listData.get(position));
            intent.putExtras(bundle);
            intent.putExtra(Constants.EXTRA_IMAGE_INDEX, posit);
            intent.putExtra("cid", listData.get(position).getCid());
            mContext.startActivity(intent);

        }
    }

    private void btnShare(Growth growth) {
        Intent intent = new Intent(mContext, ShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("shareImages", (Serializable) growth.getImages());
        intent.putExtras(bundle);
        intent.putExtra("content", growth.getContent());
        intent.putExtra("gid", growth.getId());
        intent.putExtra("from", growth.getCid());
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
    }

    class GridViewClickPosition implements GetClickPosition {
        private int poi;

        public GridViewClickPosition(int position) {
            this.poi = position;
        }

        @Override
        public void getPosition(int position) {
            if (position < 0) {
                CircleMember c = new CircleMember(listData.get(position)
                        .getCid(), 0, Global.getIntUid());
                if (!c.isAuth(DBUtils.getDBsa(1))) {
                    Utils.showToast("啊哦，您还不是认证成员，快去找圈中朋友帮您认证:)",
                            Toast.LENGTH_SHORT);
                    return;
                }
                intentGrowthCommentActivity(poi);

            }
        }
    }

    class ImgOnClick implements OnClickListener {
        String path;
        int position;

        public ImgOnClick(String path, int position) {
            this.path = path;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            List<GrowthAlbumImages> imgUrl = new ArrayList<GrowthAlbumImages>();
            for (int i = 0; i < listData.get(position).getImages().size(); i++) {
                GrowthAlbumImages img = new GrowthAlbumImages();
                img.setPicPath(listData.get(position).getImages().get(i)
                        .getImg());
                img.setPicID(listData.get(position).getImages().get(i)
                        .getImgId());
                imgUrl.add(img);
            }
            Utils.imageBrowerGrowth(mContext, 1, imgUrl, listData.get(position));

        }

    }

    class BtnClick implements OnClickListener {
        int position;

        public BtnClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (listData.get(position).isLoadingFail()) {
                GrowthFail(v, position);
                return;
            }
            if (isUpLoading(listData.get(position))) {
                Utils.showToast("成长正在发布，暂时不能操作", Toast.LENGTH_SHORT);
                return;
            }
            CircleMember c = new CircleMember(listData.get(position).getCid(),
                    0, Global.getIntUid());
            if (!c.isAuth(DBUtils.getDBsa(1))) {
                Utils.showToast("啊哦，您还不是认证成员，快去找圈中朋友帮您认证:)", Toast.LENGTH_SHORT);
                return;
            }
            switch (v.getId()) {
                case R.id.layComment:
                    intentGrowthCommentActivity(position);
                    break;
                case R.id.layParent:
                    intentGrowthCommentActivity(position);
                    break;
                case R.id.layShare:
                    btnShare(listData.get(position));
                    break;
                default:
                    break;
            }
        }
    }

    private void GrowthFail(View view, final int position) {
        GrowthLoadingFailPopwindow pop = new GrowthLoadingFailPopwindow(
                mContext, view);
        pop.setCallback(new FailGrowth() {

            @Override
            public void ToSend() {
                listData.get(position).setUpLoading(true);
                listData.get(position).setLoadingFail(false);
                notifyDataSetChanged();
                upLoadGrowth(listData.get(position));
            }

            @Override
            public void DelGrowth() {
                listData.remove(position);
                notifyDataSetChanged();
            }
        });
        pop.show();
    }

    private void upLoadGrowth(final Growth growth) {
        UpLoadNewGrowthTask task = new UpLoadNewGrowthTask();
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (result != RetError.NONE) {
                    growth.setLoadingFail(true);
                    growth.setUpLoading(false);
                    notifyDataSetChanged();
                    return;
                }
                growth.setLoadingFail(false);
                growth.setUpLoading(false);
                notifyDataSetChanged();
            }

            @Override
            public void readDBFinish() {
            }
        });
        task.executeWithCheckNet(growth);
    }

    private boolean isUpLoading(Growth g) {
        return g.isUpLoading();
    }

    private void intentGrowthCommentActivity(int position) {
        // CircleMember c = new CircleMember(listData.get(position).getCid(), 0,
        // Global.getIntUid());
        // if (!c.isAuth(DBUtils.getDBsa(1))) {
        // Utils.showToast("非认证成员暂时看不到其他人发布的成长详细信息，快去找圈中朋友帮您认证:)",
        // Toast.LENGTH_SHORT);
        // return;
        // }
        Growth modle = listData.get(position);
        Intent intent = new Intent(mContext, GrowthCommentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("growth", (Serializable) modle);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
        GrowthCommentActivity.setRecordOperation(new RecordOperation() {
            @Override
            public void delRecord(int pisition) {
                listData.remove(pisition);
                notifyDataSetChanged();
            }

            @Override
            public void setComment(int position, String count) {
                listData.get(position).setCommentCnt(Integer.valueOf(count));
                notifyDataSetChanged();

            }

            @Override
            public void setPraise(int position, int count, boolean isPraised) {
                listData.get(position).setPraiseCnt(count);
                listData.get(position).setPraised(isPraised);
                notifyDataSetChanged();
            }
        });
    }

    private void setPraiseDrable(boolean ispraise, ImageView img) {
        if (ispraise) {
            img.setImageResource(R.drawable.icon_praise1);
        } else {
            img.setImageResource(R.drawable.icon_praise);

        }
    }

    class PraiseClick implements OnClickListener {
        Growth growth;
        TextView text;
        ImageView img;

        public PraiseClick(Growth growth, TextView text, ImageView img) {
            this.growth = growth;
            this.text = text;
            this.img = img;
        }

        @Override
        public void onClick(View v) {
            if (isUpLoading(growth)) {
                Utils.showToast("成长正在发布，暂时不能操作", Toast.LENGTH_SHORT);
                return;
            }
            CircleMember c = new CircleMember(growth.getCid(), 0,
                    Global.getIntUid());
            if (!c.isAuth(DBUtils.getDBsa(1))) {
                Utils.showToast("啊哦，您还不是认证成员，快去找圈中朋友帮您认证:)", Toast.LENGTH_SHORT);
                return;
            }
            PraiseAndCancle(growth, text, img);
        }
    }

    private void setPraiseCount(TextView text, int praiseCount) {
        if (praiseCount == 0) {
            text.setText("  ");
        } else {
            text.setText("" + praiseCount);
        }
    }

    /**
     * 点赞
     */
    @SuppressLint("NewApi")
    private void PraiseAndCancle(final Growth growth, final TextView v,
            final ImageView imgPraise) {
        if (!Utils.isNetworkAvailable()) {
            Utils.showToast("杯具，网络不通，快检查下。", Toast.LENGTH_SHORT);
            return;
        }
        if (growth.isPraising()) {
            return;
        }
        final boolean isPraise = growth.isPraised();
        if (growth.isPraised()) {
            growth.setPraiseCnt(growth.getPraiseCnt() - 1);
        } else {
            growth.setPraiseCnt(growth.getPraiseCnt() + 1);
        }
        int count = growth.getPraiseCnt();
        setPraiseCount(v, count);
        setPraiseDrable(!isPraise, imgPraise);
        BaseAsyncTask<Void, Void, RetError> taks = new BaseAsyncTask<Void, Void, RetError>() {
            @Override
            protected RetError doInBackground(Void... params) {
                growth.setPraising(true);
                RetError ret = growth.uploadMyPraise(isPraise);
                if (ret == RetError.NONE) {
                    growth.write(DBUtils.getDBsa(2));
                }
                return ret;
            }
        };
        taks.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                growth.setPraising(false);
                if (result != RetError.NONE) {
                    return;
                }
                setPraiseDrable(growth.isPraised(), imgPraise);
            }

            @Override
            public void readDBFinish() {
            }
        });
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            taks.execute();
        } else {
            taks.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }

    }
}