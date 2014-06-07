package com.changlianxi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.adapter.GrowthImgAdapter;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthAlbumImages;
import com.changlianxi.data.GrowthComment;
import com.changlianxi.data.GrowthCommentList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.GrowthCommentsTask;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.FinalBitmapLoadTool;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.CircularImage;
import com.changlianxi.view.HorizontalListView;
import com.changlianxi.view.PullDownView;
import com.changlianxi.view.PullDownView.OnPullDownListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 成长详情及评论界面
 * 
 * @author teeker_bin
 * 
 */
public class GrowthCommentActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener, OnPullDownListener {
    private Growth growth;
    private LayoutInflater flater;
    private ListView listview;
    private int cid;// 圈子id
    private int uid;// 用户id
    private int pid;
    private int gid;// 成长记录id
    private MyAdapter adapter;// 自定义adapter
    private TextView name;// 显示發佈人姓名
    private TextView content;// 显示记录内容
    private TextView time;// 显示记录发布时间
    private CircularImage img;// 发布人头像
    private TextView comment;// 评论数量
    private TextView addressTextView;// 成长发布地址
    private String address = "";
    private GridView gridView;// 用来展示记录中的图片
    private ImageView back;
    private Button btnPublis;// 回复按钮
    private EditText edtContent;// 评论内容
    private Button del;// 删除按钮
    private int pisition = 0;
    private static RecordOperation callBack;
    private ScrollView scorll;
    private ImageView oneImg;
    private GrowthCommentList commentList;
    private List<GrowthComment> comments;
    private String avatarImg = "";
    private Dialog dialog;
    private HorizontalListView hlistView;
    private HorizListAdapter mListAdapter;
    private LinearLayout pra_layoutLayout;
    private TextView title;
    private String from = "";
    private List<PraiseListModle> praiseLists = new ArrayList<PraiseListModle>();
    private PullDownView mPullDownView;
    private boolean isMore = false;
    private ImageView imgPraise;
    private LinearLayout layPraise;
    private LinearLayout layShare;
    private TextView praise;// 点赞的数量

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.growth_comments);
        Bundle bundle = getIntent().getExtras();
        growth = (Growth) bundle.getSerializable("growth");
        from = getIntent().getStringExtra("from");
        pisition = getIntent().getIntExtra("position", 0);
        cid = growth.getCid();
        gid = growth.getId();
        uid = growth.getPublisher();
        address = growth.getLocation();
        initView();
        CircleMember m = getNameAndAvatar(cid, uid);
        pid = m.getPid();
        avatarImg = m.getAvatar();
        name.setText(m.getName());
        if ("".equals(avatarImg)) {
            img.setImageResource(R.drawable.head_bg);
        } else {
            FinalBitmapLoadTool.display(avatarImg, img, R.drawable.head_bg);

        }
        commentList = growth.getCommentList();
        filldata(0);

    }

    private CircleMember getNameAndAvatar(int cid, int uid) {
        CircleMember m = new CircleMember(cid, 0, uid);
        m.getNameAndAvatar(DBUtils.getDBsa(1));
        return m;

    }

    private void filldata(long startTime) {
        GrowthCommentsTask commentsTask = new GrowthCommentsTask(commentList,
                startTime);
        commentsTask.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                mPullDownView.notifyDidMore();
                comments = commentList.getComments();
                if (adapter == null) {
                    adapter = new MyAdapter();
                    listview.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                Utils.setListViewHeightBasedOnChildren(listview);
                if (!isMore) {
                    getPraiseListAdapter();
                    setGrowthCommentCount(commentList.getTotal());
                }
                if (commentList.getServerCount() > 19) {
                    mPullDownView.setFooterVisible(true);
                } else {
                    mPullDownView.setFooterVisible(false);

                }

            }

            @Override
            public void readDBFinish() {

            }
        });
        commentsTask.execute();
    }

    private void getPraiseListAdapter() {
        List<Integer> ids = commentList.getList();
        CircleMember m;
        PraiseListModle mode;
        for (int i = 0; i < ids.size(); i++) {
            int uid = ids.get(i);
            mode = new PraiseListModle();
            m = getNameAndAvatar(cid, uid);
            mode.setAvatar(m.getAvatar());
            mode.setUid(uid);
            mode.setPid(m.getPid());
            mode.setName(mode.name);
            praiseLists.add(mode);
        }
        if (praiseLists.size() == 0) {
            pra_layoutLayout.setVisibility(View.GONE);
        } else {
            pra_layoutLayout.setVisibility(View.VISIBLE);
            mListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置页面统计
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    /**
     * . 初始化控件
     */
    private void initView() {
        initPraiseView();
        flater = LayoutInflater.from(this);
        del = (Button) findViewById(R.id.del);
        addressTextView = (TextView) findViewById(R.id.text_address);
        btnPublis = (Button) findViewById(R.id.btPublish);
        edtContent = (EditText) findViewById(R.id.editContent);
        back = (ImageView) findViewById(R.id.back);
        oneImg = (ImageView) findViewById(R.id.oneImg);
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        hlistView = (HorizontalListView) findViewById(R.id.listview);
        content = (TextView) findViewById(R.id.content);
        comment = (TextView) findViewById(R.id.comment);
        name = (TextView) findViewById(R.id.name);
        time = (TextView) findViewById(R.id.time);
        img = (CircularImage) findViewById(R.id.img);
        time.setText(DateUtils.publishedTime(growth.getPublished()));
        content.setText(StringUtils.ToDBC(growth.getContent()));
        scorll = (ScrollView) findViewById(R.id.scroll);
        layShare = (LinearLayout) findViewById(R.id.layShare);
        pra_layoutLayout = (LinearLayout) findViewById(R.id.praise_layout);
        title = (TextView) findViewById(R.id.titleTxt);
        mPullDownView = (PullDownView) findViewById(R.id.pullListView);
        mPullDownView.notifyDidMore();
        mPullDownView.setHideHeader();
        mPullDownView.setFooterViewFont(15, "获取更多评论");
        listview = mPullDownView.getListView();
        listview.setCacheColorHint(0);
        listview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listview.setDivider(getResources().getDrawable(R.color.d2));
        listview.setDividerHeight(1);
        listview.setVerticalScrollBarEnabled(false);
        mListAdapter = new HorizListAdapter();
        hlistView.setAdapter(mListAdapter);
        setViewListener();
        setViewValue();
    }

    private void initPraiseView() {
        praise = (TextView) findViewById(R.id.praise);
        layPraise = (LinearLayout) findViewById(R.id.layPraise);
        imgPraise = (ImageView) findViewById(R.id.imgPraise);
    }

    private void setViewListener() {
        layPraise.setOnClickListener(this);
        del.setOnClickListener(this);
        btnPublis.setOnClickListener(this);
        back.setOnClickListener(this);
        hlistView.setOnItemClickListener(this);
        gridView.setOnItemClickListener(this);
        oneImg.setOnClickListener(this);
        img.setOnClickListener(this);
        mPullDownView.setOnPullDownListener(this);
        layShare.setOnClickListener(this);
        mPullDownView.setFooterVisible(false);

    }

    private void setViewValue() {
        initImg(growth);
        if (!"".equals(address)) {
            addressTextView.setText(DateUtils.getGrowthShowTime(growth
                    .getHappened()) + "于" + address);
        } else {
            addressTextView.setText(DateUtils.getGrowthShowTime(growth
                    .getHappened()));
        }
        int praiseCount = growth.getPraiseCnt();
        setPraiseCount(praiseCount);
        setGrowthCommentCount(growth.getCommentCnt());
        setPraiseDrable(growth.isPraised(), imgPraise);
        if (!isPermission(uid + "") || "bigImage".equals(from)) {
            del.setVisibility(View.INVISIBLE);
        }
        title.setText("成长详情");

    }

    public void setPraiseCount(int prarseCount) {
        if (prarseCount == 0) {
            praise.setText("");
        } else {
            praise.setText("" + prarseCount);
        }
    }

    private void setPraiseDrable(boolean ispraise, ImageView img) {
        if (ispraise) {
            img.setImageResource(R.drawable.icon_praise1);
        } else {
            img.setImageResource(R.drawable.icon_praise);

        }
    }

    private void setGrowthCommentCount(int commentCount) {
        if (commentCount == 0) {
            comment.setText("");
        } else {
            comment.setText("" + commentCount);
        }
    }

    private void initImg(Growth growth) {
        int size = growth.getImages().size();
        if (growth.getContent().equals("")) {
            content.setVisibility(View.GONE);
        } else {
            content.setVisibility(View.VISIBLE);
        }
        if (size == 1) {
            String imgPath = growth.getImages().get(0).getImg();
            if (imgPath.startsWith("http")) {
                imgPath = StringUtils.JoinString(imgPath, "_500x500");
            } else {
                imgPath = "file://" + imgPath;
            }

            oneImg.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            // fb.display(oneImg, imgPath);
            FinalBitmapLoadTool.display(imgPath, oneImg, R.drawable.head_bg);

        } else {
            oneImg.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            gridView.setAdapter(new GrowthImgAdapter(this, growth.getImages()));
        }
    }

    /**
     * 判断是否有权限进行编辑
     * 
     * @param uid
     * @return
     */
    private boolean isPermission(String uid) {
        if (uid.equals(SharedUtils.getString("uid", ""))) {
            return true;
        }
        return false;
    }

    /**
     * 删除记录
     * 
     */
    private void DelCommentsTask() {
        dialog = DialogUtil.getWaitDialog(this, "请稍候");
        dialog.show();
        BaseAsyncTask<Void, Void, RetError> task = new BaseAsyncTask<Void, Void, RetError>() {

            @Override
            protected RetError doInBackground(Void... params) {
                RetError ret = growth.uploadForDel();
                if (ret == RetError.NONE) {
                    growth.write(DBUtils.getDBsa(2));
                }
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
                    finish();
                    Utils.rightOut(GrowthCommentActivity.this);
                    if (callBack != null) {
                        callBack.delRecord(pisition);
                    }
                }
            }

            @Override
            public void readDBFinish() {

            }
        });
        task.executeWithCheckNet();

    }

    /**
     * 自定义adapter用来显示评论内容
     * 
     * @author teeker_bin
     * 
     */
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return comments.size();
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
            int uid = comments.get(position).getUid();
            CircleMember m = getNameAndAvatar(cid, uid);
            String name = m.getName();
            String avatar = m.getAvatar();
            int pid = m.getPid();
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = flater.inflate(R.layout.growth_comments_item,
                        null);
                holder.content = (TextView) convertView
                        .findViewById(R.id.content);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.img = (CircularImage) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(name);
            String path = avatar;
            if (path == null || path.equals("")) {
                holder.img.setImageResource(R.drawable.head_bg);
            } else {
                // fb.display(holder.img, path);
                FinalBitmapLoadTool.display(path, holder.img,
                        R.drawable.head_bg);

            }
            holder.img.setOnClickListener(new OnAvatarClick(cid, uid, pid,
                    name, avatar));
            holder.content.setText(comments.get(position).getContent());
            holder.time.setText(DateUtils.publishedTime2(comments.get(position)
                    .getTime()));
            return convertView;
        }
    }

    class ViewHolder {
        CircularImage img;
        TextView name;
        TextView time;
        TextView content;
        CircularImage img_headIcon;
    }

    class OnAvatarClick implements OnClickListener {
        int uid;
        int cid;
        int pid;
        String name = "";
        String avatarImg = "";
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
            // Utils.intentUserDetailActivity(GrowthCommentActivity.this, cid,
            // uid, pid, name, avatarImg);
            // Utils.leftOutRightIn(GrowthCommentActivity.this);
        }
    }

    /**
     * 点击事件的处理
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                Utils.rightOut(this);
                break;
            case R.id.btPublish:

                String str = edtContent.getText().toString();
                if (str.length() == 0) {
                    Utils.showToast("请输入评论内容", Toast.LENGTH_SHORT);
                    return;
                }
                publishedComment(str);
                break;
            case R.id.del:
                Dialog dialog = DialogUtil.confirmDialog(this,
                        "确定删除？精彩分享不要轻易放弃哦", "确定", "取消", new ConfirmDialog() {

                            @Override
                            public void onOKClick() {
                                DelCommentsTask();
                            }

                            @Override
                            public void onCancleClick() {
                            }
                        });
                dialog.show();
                break;
            case R.id.layPraise:
                PraiseAndCancle();
                break;
            case R.id.oneImg:
                List<GrowthAlbumImages> imgUrl = new ArrayList<GrowthAlbumImages>();
                for (int i = 0; i < growth.getImages().size(); i++) {
                    GrowthAlbumImages img = new GrowthAlbumImages();
                    img.setPicPath(growth.getImages().get(i).getImg());
                    img.setPicID(growth.getImages().get(i).getImgId());
                    imgUrl.add(img);
                }
                Utils.imageBrowerGrowth(this, 1, imgUrl, growth);
                break;
            case R.id.img:
                // Utils.intentUserDetailActivity(this, cid, uid, pid,
                // publisherName, avatarImg);
                Utils.leftOutRightIn(this);
                break;
            case R.id.layShare:
                btnShare();
                break;
            default:
                break;
        }
    }

    private void btnShare() {
        Intent intent = new Intent(this, ShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("shareImages", (Serializable) growth.getImages());
        intent.putExtras(bundle);
        intent.putExtra("content", growth.getContent());
        intent.putExtra("gid", growth.getId());
        intent.putExtra("from", growth.getCid());
        startActivity(intent);
        Utils.leftOutRightIn(this);
    }

    private void publishedComment(String str) {
        final Dialog pd = DialogUtil.getWaitDialog(this, "请稍候");
        pd.show();
        final GrowthComment growthComment = new GrowthComment(cid, gid, 0, uid,
                str);
        BaseAsyncTask<Void, Void, RetError> asyncTask = new BaseAsyncTask<Void, Void, RetError>() {

            @Override
            protected RetError doInBackground(Void... params) {
                RetError ret = growth.uploadMyComment(growthComment);
                if (ret == RetError.NONE) {
                    growth.write(DBUtils.getDBsa(2));
                }
                return ret;
            }

        };
        asyncTask.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
                if (pd != null) {
                    pd.dismiss();
                }
                if (result != RetError.NONE) {
                    return;
                }
                setGrowthCommentCount(growth.getCommentCnt());
                edtContent.setText("");
                if (callBack != null) {
                    callBack.setComment(pisition, growth.getCommentCnt() + "");
                }
                Utils.setListViewHeightBasedOnChildren(listview);
                scorll.scrollTo(0, 0);
            }

            @Override
            public void readDBFinish() {

            }
        });
        asyncTask.executeWithCheckNet();
    }

    private void addPraiseListForMe() {
        PraiseListModle mode = new PraiseListModle();
        CircleMember m = getNameAndAvatar(cid, Global.getIntUid());
        mode.setAvatar(m.getAvatar());
        mode.setUid(m.getUid());
        mode.setPid(m.getPid());
        mode.setName(mode.name);
        praiseLists.add(mode);
        if (pra_layoutLayout.getVisibility() != View.VISIBLE) {
            pra_layoutLayout.setVisibility(View.VISIBLE);
        }
        if (mListAdapter == null) {
            mListAdapter = new HorizListAdapter();
            hlistView.setAdapter(mListAdapter);
        } else {
            mListAdapter.notifyDataSetChanged();
        }

    }

    private void delPraiseListForMe() {
        int puid = 0;
        for (int i = praiseLists.size() - 1; i >= 0; i--) {
            puid = praiseLists.get(i).getUid();
            if (Global.getIntUid() == puid) {
                praiseLists.remove(i);
                break;
            }
        }
        if (praiseLists.size() == 0) {
            pra_layoutLayout.setVisibility(View.GONE);
        } else {
            mListAdapter.notifyDataSetChanged();

        }
    }

    /**
     * 点赞
     */
    private void PraiseAndCancle() {
        if (growth.isPraising()) {
            return;
        }
        if (growth.isPraised()) {
            growth.setPraiseCnt(growth.getPraiseCnt() - 1);
            delPraiseListForMe();
        } else {
            growth.setPraiseCnt(growth.getPraiseCnt() + 1);
            addPraiseListForMe();
        }
        setPraiseDrable(!growth.isPraised(), imgPraise);
        int praiseCount = growth.getPraiseCnt();
        setPraiseCount(praiseCount);

        BaseAsyncTask<Void, Void, RetError> taks = new BaseAsyncTask<Void, Void, RetError>() {

            @Override
            protected RetError doInBackground(Void... params) {
                growth.setPraising(true);
                RetError ret = growth.uploadMyPraise(growth.isPraised());
                if (ret == RetError.NONE) {
                    growth.write(DBUtils.getDBsa(1));
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
                int praiseCount = growth.getPraiseCnt();
                setPraiseCount(praiseCount);
                if (callBack != null) {
                    callBack.setPraise(pisition, growth.getPraiseCnt(),
                            growth.isPraised());
                }
            }

            @Override
            public void readDBFinish() {

            }
        });
        taks.executeWithCheckNet();
    }

    public static void setRecordOperation(RecordOperation callBa) {
        callBack = callBa;
    }

    /**
     * 对成长记录的操作接口
     * 
     * @author teeker_bin
     * 
     */
    public interface RecordOperation {
        /**
         * 删除记录
         * 
         * @param pisition
         */
        public void delRecord(int pisition);

        /**
         * 评论
         * 
         * @param position
         * @return
         */
        public void setComment(int position, String count);

        public void setPraise(int position, int count, boolean isPraised);
    }

    @Override
    public void exit() {
        super.exit();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (arg0.getId() == gridView.getId()) {
            List<GrowthAlbumImages> imgUrl = new ArrayList<GrowthAlbumImages>();
            for (int i = 0; i < growth.getImages().size(); i++) {
                GrowthAlbumImages img = new GrowthAlbumImages();
                img.setPicPath(growth.getImages().get(i).getImg());
                img.setPicID(growth.getImages().get(i).getImgId());
                imgUrl.add(img);
            }
            Utils.imageBrowerGrowth(this, arg2, imgUrl, growth);
        } else {
            // Utils.intentUserDetailActivity(this, cid, praiseLists.get(arg2)
            // .getUid(), praiseLists.get(arg2).getPid(),
            // praiseLists.get(arg2).getName(),
            // praiseLists.get(arg2).avatar);
            // Utils.leftOutRightIn(this);
        }

    }

    // 点赞 头像adapter
    private class HorizListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return praiseLists.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(GrowthCommentActivity.this)
                        .inflate(R.layout.praise_list_item, null);
                holder.img_headIcon = (CircularImage) convertView
                        .findViewById(R.id.img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // fb.display(holder.img_headIcon, praiseLists.get(position)
            // .getAvatar());
            FinalBitmapLoadTool.display(praiseLists.get(position).getAvatar(),
                    holder.img_headIcon, R.drawable.head_bg);

            return convertView;
        }
    }

    class PraiseListModle {
        int uid;
        int pid;
        String name = "";
        String avatar = "";

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (data == null) {
                return;
            }
            Growth gth = (Growth) data.getExtras().getSerializable("growth");
            growth = gth;
            content.setText(StringUtils.ToDBC(gth.getContent()));
            initImg(gth);
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onMore() {
        isMore = true;
        long startTime = DateUtils.convertToDate(comments.get(
                comments.size() - 1).getTime()) / 1000;
        filldata(startTime - 1);
    }
}
