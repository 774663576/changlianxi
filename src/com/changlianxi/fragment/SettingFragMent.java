package com.changlianxi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changlianxi.AboutActivity;
import com.changlianxi.AdviceFeedBackActivity;
import com.changlianxi.ChangePassswordActivity;
import com.changlianxi.LoginActivity;
import com.changlianxi.NoticesActivity;
import com.changlianxi.ProblemActivity;
import com.changlianxi.R;
import com.changlianxi.VerifyActivity;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.Global;
import com.changlianxi.data.MyCard;
import com.changlianxi.db.DBUtils;
import com.changlianxi.db.DataBaseHelper;
import com.changlianxi.inteface.ConfirmDialog;
import com.changlianxi.slidingmenu.lib.app.SlidingActivity;
import com.changlianxi.task.PostAsyncTask;
import com.changlianxi.task.PostAsyncTask.PostCallBack;
import com.changlianxi.task.UpDateNewVersionTask;
import com.changlianxi.task.UpDateNewVersionTask.UpDateVersion;
import com.changlianxi.util.BaiDuPushUtils;
import com.changlianxi.util.DialogUtil;
import com.changlianxi.util.ErrorCodeUtil;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.Utils;

@SuppressLint("NewApi")
public class SettingFragMent extends Fragment implements OnClickListener,
        OnItemClickListener, PostCallBack {
    private ListView setListView;
    private List<String> listMenus = new ArrayList<String>();
    private MyAdapter adapter;
    private ImageView menu;
    private TextView title;
    private Button exitLogin;
    private MyCard card;
    private Dialog dialog;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initView();
                    break;
                default:
                    break;
            }

        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.sendEmptyMessageDelayed(0, 0);
    }

    private void initView() {
        menu = (ImageView) getView().findViewById(R.id.back);
        menu.setImageResource(R.drawable.icon_list);
        title = (TextView) getView().findViewById(R.id.titleTxt);
        setListView = (ListView) getView().findViewById(R.id.setListView);
        View bottom = LayoutInflater.from(getActivity()).inflate(
                R.layout.set_layout_item_bottom, null);
        exitLogin = (Button) bottom.findViewById(R.id.exitLogin);
        setListView.addFooterView(bottom);
        setListtener();
    }

    private void setListtener() {
        menu.setOnClickListener(this);
        exitLogin.setOnClickListener(this);
        setListView.setOnItemClickListener(this);
        setValue();
    }

    private void setValue() {
        getListMenu();
        title.setText("设置");
        adapter = new MyAdapter();
        setListView.setAdapter(adapter);
    }

    private void getListMenu() {
        card = new MyCard(0, Global.getIntUid());
        card.readMyCard(DBUtils.getDBsa(1));
        listMenus.add("绑定手机号");
        if (!card.getRegister().equals("third_party")) {
            listMenus.add("修改密码");
        }
        listMenus.add("意见反馈");
        listMenus.add("版本更新");
        listMenus.add("关于常联系");
        listMenus.add("常见问题");
        listMenus.add("软件许可及服务协议");

    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listMenus.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.setting_laout_item, null);
                holder.bg = (RelativeLayout) convertView.findViewById(R.id.bg);
                holder.menuText = (TextView) convertView
                        .findViewById(R.id.menuText);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.txt = (TextView) convertView.findViewById(R.id.txt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.menuText.setText(listMenus.get(position));
            if (position % 2 == 0) {
                holder.bg.setBackgroundColor(Color.WHITE);

            } else {
                holder.bg.setBackgroundColor(getActivity().getResources()
                        .getColor(R.color.f6));
            }
            if (position == 0) {
                if (!"".equals(card.getCellphone())) {
                    holder.txt.setText("已绑定" + card.getCellphone());
                    holder.txt.setVisibility(View.VISIBLE);
                    holder.img.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

    }

    class ViewHolder {
        RelativeLayout bg;
        TextView menuText;
        TextView txt;
        ImageView img;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ((SlidingActivity) getActivity()).getSlidingMenu().toggle();
                break;
            case R.id.exitLogin:
                editDialog();
                break;
            default:
                break;
        }
    }

    private void editDialog() {
        Dialog dialog = DialogUtil.confirmDialog(getActivity(),
                "退出后，您将不能及时收到老朋友们发的消息，可能错过很多精彩内容。您确定么？", "确定", "取消",
                new ConfirmDialog() {

                    @Override
                    public void onOKClick() {
                        exit();
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.show();
    }

    /**
     * 退出
     */
    private void exit() {
        if (!Utils.isNetworkAvailable()) {
            Utils.showToast("杯具，网络不通，快检查下。", Toast.LENGTH_SHORT);
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", SharedUtils.getString("uid", ""));
        map.put("token", SharedUtils.getString("token", ""));
        PostAsyncTask task = new PostAsyncTask(getActivity(), map,
                "/users/ilogout");
        task.setTaskCallBack(this);
        task.execute();
        dialog = DialogUtil.getWaitDialog(getActivity(), "正在退出");
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        if (position == 0) {
            if ("".equals(card.getCellphone())) {
                Intent it = new Intent();
                it.setClass(getActivity(), VerifyActivity.class);
                it.putExtra("type", "laterbind");
                startActivity(it);
                return;
            }
            return;
        }
        if ("third_party".equals(card.getRegister())) {
            position += 1;
        }
        Intent intent = new Intent();
        switch (position) {
            case 1:
                intent.setClass(getActivity(), ChangePassswordActivity.class);
                getActivity().startActivity(intent);
                Utils.leftOutRightIn(getActivity());
                break;
            case 2:
                intent.setClass(getActivity(), AdviceFeedBackActivity.class);
                getActivity().startActivity(intent);
                Utils.leftOutRightIn(getActivity());
                break;
            case 3:
                getNewVersion();
                break;
            case 4:
                intent.setClass(getActivity(), AboutActivity.class);
                getActivity().startActivity(intent);
                Utils.leftOutRightIn(getActivity());
                break;
            case 5:
                intent.setClass(getActivity(), ProblemActivity.class);
                getActivity().startActivity(intent);
                Utils.leftOutRightIn(getActivity());
                break;
            case 6:
                intent.setClass(getActivity(), NoticesActivity.class);
                getActivity().startActivity(intent);
                Utils.leftOutRightIn(getActivity());
                break;
            default:
                break;
        }
    }

    private void getNewVersion() {
        dialog = DialogUtil.getWaitDialog(getActivity(), "检查新版本");
        dialog.show();
        UpDateNewVersionTask task = new UpDateNewVersionTask(getActivity(),
                true);
        task.setCallBack(new UpDateVersion() {
            @Override
            public void getNewVersion(String rt, String versionCode, String link) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (!"1".equals(rt)) {
                    return;
                }
                DialogUtil.newVersion(getActivity(), versionCode, link);
            }
        });
        task.execute();
    }

    @Override
    public void taskFinish(String result) {
        if (result == null) {
            return;
        }
        try {
            JSONObject json = new JSONObject(result);
            int rt = json.getInt("rt");
            if (rt == 1) {
                finish();
            } else {
                String err = json.getString("err");
                if (err.equals("TOKEN_INVALID")) {
                    finish();
                    return;
                }
                String errorString = ErrorCodeUtil.convertToChines(err);
                Utils.showToast(errorString, Toast.LENGTH_SHORT);
                dialog.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finish() {
        SharedUtils.setBoolean("isLogin", false);
        SharedUtils.setString("uid", "");
        SharedUtils.setString("token", "");
        BaiDuPushUtils.setBind(getActivity(), false);
        DataBaseHelper.setIinstanceNull();
        DBUtils.dbase = null;
        DBUtils.close();
        CLXApplication.exit(false);
        Intent intent = new Intent();
        intent.setClass(getActivity(), LoginActivity.class);
        getActivity().startActivity(intent);
    }

}
