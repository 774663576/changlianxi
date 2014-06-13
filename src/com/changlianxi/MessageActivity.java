package com.changlianxi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.adapter.MessageAdapter;
import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.PersonChat;
import com.changlianxi.data.PersonChatList;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.PushMessages;
import com.changlianxi.inteface.SendMessageAndChatCallBack;
import com.changlianxi.popwindow.SelectPicPopwindow;
import com.changlianxi.popwindow.SelectPicPopwindow.CameraPath;
import com.changlianxi.task.BaseAsyncTask;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.PersonChatListTast;
import com.changlianxi.task.SendMessageThread;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.Constants;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.Expressions;
import com.changlianxi.util.MyPushMessageReceiver;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;
import com.changlianxi.view.MyListView;
import com.changlianxi.view.MyListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 私信聊天界面
 * 
 * @author teeker_bin
 * 
 */
public class MessageActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener, PushMessages, SendMessageAndChatCallBack,
        CameraPath {
    private Button btnSend;// 发送按钮
    private EditText editContent;// 内容输入框
    private MyListView listview;
    private ImageView back;
    private TextView name;// 显示接受私信者的姓名
    private int ruid;// 接受私信者id
    private MessageAdapter adapter;
    private int cid;
    private ImageView imgAdd;
    private LinearLayout layAdd;
    private TextView layoutExpression;
    private RelativeLayout expression;
    private TextView layoutImg;
    private TextView layoutText;
    private boolean layAddIsShow = false;
    private ViewPager viewPager;
    private ArrayList<GridView> grids;
    private ImageView[] imageViews;// 圆点
    private int[] expressionImages;
    private String[] expressionImageNames;
    private int[] expressionImages1;
    private String[] expressionImageNames1;
    private int[] expressionImages2;
    private String[] expressionImageNames2;
    private int[] expressionImages3;
    private String[] expressionImageNames3;
    private int[] expressionImages4;
    private String[] expressionImageNames4;
    private int page = 0;// 标记表情当前页
    private Dialog pd;
    private long startTime = 0l;
    private long endTime = 0l;
    private String receiveName;// 私信对方姓名
    private String receiveAvatar = "";
    private int receivePid;
    private SelectPicPopwindow pop;
    private PersonChatList personChatList = null;
    private int uid;
    private PersonChat chat;
    private List<PersonChat> chatsList = new ArrayList<PersonChat>();
    private List<PersonChat> lists = new ArrayList<PersonChat>();
    private LayoutInflater inflater;
    private boolean isfresh = false;
    private String picPath = "";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Utils.showToast((String) msg.obj, Toast.LENGTH_SHORT);
                    break;
                case 1:
                    chatsList.addAll(0, lists);
                    adapter.setData(chatsList);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getIntentData();
        findViewById();
        setListener();
        initExpression();
        initViewPager();
        MyPushMessageReceiver.setPushMessageCallBack(this);
        pd = DialogUtil.getWaitDialog(this, "请稍候");
        pd.show();
        uid = Integer.valueOf(Global.getUid());
        getPersonChatList(true);
    }

    /**设置页面统计
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

    private void getIntentData() {
        ruid = getIntent().getIntExtra("ruid", 0);
        cid = getIntent().getIntExtra("cid", 0);
        CircleMember c = new CircleMember(cid, 0, ruid);
        c.getNameAndAvatar(DBUtils.getDBsa(1));
        receiveName = c.getName();
        if (c.getName() == null || c.getName().equals("")) {
            receiveName = getIntent().getStringExtra("name");
        }
        receiveAvatar = c.getAvatar();
        receivePid = c.getPid();
    }

    private void getPersonChatList(final boolean readDB) {
        lists = new ArrayList<PersonChat>();
        personChatList = new PersonChatList(ruid);
        PersonChatListTast task = new PersonChatListTast(startTime, endTime);
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                if (pd != null) {
                    pd.dismiss();
                }
                listview.onRefreshComplete();
                lists = personChatList.getChats();
                chatsList.addAll(0, lists);
                adapter.setData(chatsList);
                if (!isfresh) {
                    listview.setSelection(chatsList.size());
                }

            }

            @Override
            public void readDBFinish() {
                lists = personChatList.getChats();
                if (lists.size() > 0) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                }
                mHandler.sendEmptyMessage(1);
            }
        });
        task.executeWithCheckNet(personChatList);

    }

    private void initExpression() {
        // 引入表情
        expressionImages = Expressions.expressionImgs;
        expressionImageNames = Expressions.expressionImgNames;
        expressionImages1 = Expressions.expressionImgs1;
        expressionImageNames1 = Expressions.expressionImgNames1;
        expressionImages2 = Expressions.expressionImgs2;
        expressionImageNames2 = Expressions.expressionImgNames2;
        expressionImages3 = Expressions.expressionImgs3;
        expressionImageNames3 = Expressions.expressionImgNames3;
        expressionImages4 = Expressions.expressionImgs4;
        expressionImageNames4 = Expressions.expressionImgNames4;
    }

    /**
     * 初始化控件
     */
    private void findViewById() {
        initImageViews();
        expression = (RelativeLayout) findViewById(R.id.expression);
        layoutExpression = (TextView) findViewById(R.id.layoutExpression);
        layoutImg = (TextView) findViewById(R.id.layoutImg);
        layoutImg.setOnClickListener(this);
        layoutExpression.setOnClickListener(this);
        layoutText = (TextView) findViewById(R.id.layoutText);
        layoutText.setOnClickListener(this);
        layAdd = (LinearLayout) findViewById(R.id.layoutAdd);
        imgAdd = (ImageView) findViewById(R.id.imgAdd);
        btnSend = (Button) findViewById(R.id.btnSend);
        editContent = (EditText) findViewById(R.id.editContent);
        listview = (MyListView) findViewById(R.id.listView);
        back = (ImageView) findViewById(R.id.back);
        name = (TextView) findViewById(R.id.titleTxt);
        name.setText(receiveName);
        adapter = new MessageAdapter(this, chatsList, receiveAvatar,
                receiveName, receivePid);
        listview.setAdapter(adapter);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        listview.setCacheColorHint(0);
        listview.setonRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                isfresh = true;
                if (chatsList.size() == 0) {
                    // endTime = Long.valueOf((DateUtils.phpTime(System
                    // .currentTimeMillis())));
                    startTime = 0;
                } else {
                    endTime = Long.valueOf(DateUtils.phpTime(DateUtils
                            .convertToDate(chatsList.get(0).getTime())));

                }
                getPersonChatList(false);
            }
        });
    }

    /**
     * 初始化圆点
     */
    private void initImageViews() {
        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);// 包裹小圆点的LinearLayout
        imageViews = new ImageView[5];
        for (int i = 0; i < imageViews.length; i++) {
            // 设置 每张图片的句点
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            imageView.setLayoutParams(params);
            imageViews[i] = imageView;
            if (i == 0) {
                // 默认选中第一张图片
                imageViews[i].setBackgroundResource(R.drawable.face_current);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.face);
            }
            group.addView(imageViews[i]);
        }
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        inflater = LayoutInflater.from(this);
        grids = new ArrayList<GridView>();
        int expressionimage[] = null;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    expressionimage = expressionImages;
                    break;
                case 1:
                    expressionimage = expressionImages1;
                    break;
                case 2:
                    expressionimage = expressionImages2;
                    break;
                case 3:
                    expressionimage = expressionImages3;
                    break;

                default:
                    break;
            }

            addViewPager(expressionimage, 21);
        }
        expressionimage = expressionImages4;
        addViewPager(expressionimage, 5);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    private void addViewPager(int expressionimage[], int num) {
        GridView gView = (GridView) inflater.inflate(R.layout.grid1, null);
        gView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        // 生成24个表情 每页显示个数
        for (int j = 0; j < num; j++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("image", expressionimage[j]);
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(MessageActivity.this,
                listItems, R.layout.singleexpression, new String[] { "image" },
                new int[] { R.id.image });
        gView.setAdapter(simpleAdapter);
        gView.setOnItemClickListener(this);
        grids.add(gView);
    }

    /**
     * 设置点击事件
     */
    private void setListener() {
        btnSend.setOnClickListener(this);
        back.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        listview.setSelector(new ColorDrawable(Color.TRANSPARENT));

    }

    // 填充ViewPager的数据适配器
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return grids.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(grids.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(grids.get(position));
            return grids.get(position);
        }
    };

    /**
     * 将信息发送到服务器
     */
    private void sendToServer(String content) {
        PersonChat chat = new PersonChat(cid, ruid, 0, uid, content);
        chat.setTime(DateUtils.getCurrDateStr());
        chat.setStatus(Status.NEW);
        new SendMessageThread(chat).start();

    }

    private void refushAdapter(String content, ChatType type) {
        PersonChat chat = new PersonChat(cid, ruid, 0, uid, content);
        chat.setTime(DateUtils.getCurrDateStr());
        chat.setType(type);
        chatsList.add(chat);
        adapter.notifyDataSetChanged();
        listview.setSelection(chatsList.size());// 每次发送之后将listview滑动到最低端
        // 从而显示最新消息
        editContent.setText("");

    }

    // ** 指引页面改监听器 */
    class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            page = arg0;
            for (int i = 0; i < imageViews.length; i++) {// 设置当前圆点
                if (arg0 == i) {
                    imageViews[arg0]
                            .setBackgroundResource(R.drawable.face_current);
                    continue;
                }
                imageViews[i].setBackgroundResource(R.drawable.face);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                Utils.rightOut(this);

                break;
            case R.id.btnSend:
                String content = editContent.getText().toString();
                if (content.length() == 0) {
                    return;
                }
                // 从而显示最新消息
                refushAdapter(content, ChatType.TYPE_TEXT);
                sendToServer(content);
                break;
            case R.id.imgAdd:
                if (layAddIsShow) {
                    imgAdd.setImageResource(R.drawable.icon_add_chat);
                    layAdd.setVisibility(View.GONE);
                    expression.setVisibility(View.GONE);
                    layAddIsShow = false;
                    return;
                }
                imgAdd.setImageResource(R.drawable.icon_minus);
                layAddIsShow = true;
                Utils.hideSoftInput(this);
                layAdd.setVisibility(View.VISIBLE);
                break;
            case R.id.layoutExpression:
                expression.setVisibility(View.VISIBLE);
                layAdd.setVisibility(View.GONE);
                break;
            case R.id.layoutImg:
                pop = new SelectPicPopwindow(this, v);
                pop.show();
                pop.setCallBack(this);
                layAdd.setVisibility(View.GONE);
                imgAdd.setImageResource(R.drawable.icon_add_chat);
                break;
            case R.id.layoutText:
                layAdd.setVisibility(View.GONE);
                imgAdd.setImageResource(R.drawable.icon_add_chat);
                editContent.requestFocus();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        MyPushMessageReceiver.pushMessage = null;
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        int expressionimage[] = null;
        String expressionname[] = null;
        switch (page) {
            case 0:
                expressionimage = expressionImages;
                expressionname = expressionImageNames;
                break;
            case 1:
                expressionimage = expressionImages1;
                expressionname = expressionImageNames1;
                break;
            case 2:
                expressionimage = expressionImages2;
                expressionname = expressionImageNames2;
                break;
            case 3:
                expressionimage = expressionImages3;
                expressionname = expressionImageNames3;
                break;
            case 4:
                expressionimage = expressionImages4;
                expressionname = expressionImageNames4;
                break;
            default:
                break;
        }
        if (arg2 == expressionimage.length - 1) {
            int selection = editContent.getSelectionStart();
            String text = editContent.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if (":".equals(text2)) {
                    int start = StringUtils.getPositionEmoj(text);
                    int end = selection;
                    editContent.getText().delete(start, end);
                    return;
                }
                editContent.getText().delete(selection - 1, selection);
            }
            return;
        }
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(getResources(),
                expressionimage[arg2]);
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        SpannableString spannableString = new SpannableString(
                expressionname[arg2].substring(1,
                        expressionname[arg2].length() - 1));
        spannableString.setSpan(imageSpan, 0,
                expressionname[arg2].length() - 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 编辑框设置数据
        editContent.append(spannableString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYSDCARD) {
            if (data == null) {
                return;
            }
            picPath = BitmapUtils.getPickPic(this, data);
            refushAdapter(picPath, ChatType.TYPE_IMAGE);
            upLoadPic(picPath);
        }

        else if (requestCode == Constants.REQUEST_CODE_GETIMAGE_BYCAMERA) {
            if (picPath == null || "".equals(picPath)) {
                Utils.showToast("照片获取失败，请从新获取", Toast.LENGTH_SHORT);
                return;
            }
            refushAdapter(picPath, ChatType.TYPE_IMAGE);
            upLoadPic(picPath);
        }

    }

    /**
     * 上传聊天图片
     */
    private void upLoadPic(String path) {
        chat = new PersonChat(cid, ruid, 0, uid, path);
        chat.setStatus(Status.NEW);
        chat.setTime(DateUtils.getCurrDateStr());
        chat.setType(ChatType.TYPE_IMAGE);
        BaseAsyncTask<Void, Void, RetError> taks = new BaseAsyncTask<Void, Void, RetError>() {

            @Override
            protected RetError doInBackground(Void... params) {
                RetError ret = chat.sendImage();
                if (ret == RetError.NONE) {
                    chat.write(DBUtils.getDBsa(2));
                }
                return ret;
            }
        };
        taks.setTaskCallBack(new PostCallBack<RetError>() {

            @Override
            public void taskFinish(RetError result) {
            }

            @Override
            public void readDBFinish() {

            }
        });
        taks.executeWithCheckNet();

    }

    @Override
    public void getPushMessages(String str) {
        String contetn = "";
        String time = "";
        int uid = 0;
        int cid = 0;
        String type = "";
        try {
            JSONObject json = new JSONObject(str);
            contetn = json.getString("c");
            time = json.getString("m");
            uid = json.getInt("uid");
            cid = json.getInt("cid");
            type = json.getString("ct");
            if (uid == Global.getIntUid() || uid != ruid) {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PersonChat chat = new PersonChat(cid, uid, 0, ruid, contetn);
        chat.setTime(time);
        if (type.equals("TYPE_TEXT")) {
            chat.setType(ChatType.TYPE_TEXT);
        } else if (type.equals("TYPE_IMAGE")) {
            chat.setType(ChatType.TYPE_IMAGE);
        }
        chatsList.add(chat);
        adapter.setData(chatsList);
        listview.setSelection(chatsList.size());// 每次发送之后将listview滑动到最低端
    }

    @Override
    public void getRetError(RetError ret) {
        if (ret != RetError.NONE) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = ErrorCodeUtil.convertToChines(ret.name());
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void getCameraPath(String path) {
        picPath = path;
    }

}
