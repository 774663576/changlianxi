package com.changlianxi.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changlianxi.R;
import com.changlianxi.data.Global;
import com.changlianxi.data.MyCard;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.showBigPic.AvatarImagePagerActivity;
import com.changlianxi.task.BaseAsyncTask.PostCallBack;
import com.changlianxi.task.MyCardTask;
import com.changlianxi.util.Constants;
import com.changlianxi.util.RotateImageViewAware;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.UniversalImageLoadTool;
import com.changlianxi.util.Utils;
import com.changlianxi.util.WigdtContorl;
import com.changlianxi.view.CircularImage;

public class LeftMenuFragMent extends Fragment implements OnItemClickListener {
    private List<MenuModle> menulist = new ArrayList<MenuModle>();
    private ListView listview;
    private MyAdapter adapter;
    private MyCard myCard = null;
    private CircularImage avatar;
    private ChangeFragMentListener mOnChangeFragMentListener;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setAvatar((String) msg.obj);
                    break;

                default:
                    break;
            }
        };
    };

    /**
    * 菜单FragMent切换
    *
    */
    public interface ChangeFragMentListener {
        void onChangeFragMent(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mOnChangeFragMentListener == null) {
            mOnChangeFragMentListener = (ChangeFragMentListener) activity;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.desktop, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewByID();
    }

    private void findViewByID() {
        listview = (ListView) getView().findViewById(R.id.menulist);
        setListener();
    }

    private void setListener() {
        avatar = (CircularImage) getView().findViewById(R.id.avatar);
        listview.setOnItemClickListener(this);
        avatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                List<String> imgUrl = new ArrayList<String>();
                imgUrl.add(myCard.getAvatar().replace("_160x160", ""));
                Intent intent = new Intent(getActivity(),
                        AvatarImagePagerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                        (Serializable) imgUrl);
                intent.putExtras(bundle);
                intent.putExtra("defaultImg", R.drawable.head_bg);
                intent.putExtra(Constants.EXTRA_IMAGE_INDEX, 1);
                startActivity(intent);
            }
        });
        setValue();
    }

    private void setValue() {
        myCard = new MyCard(0, Global.getIntUid());
        getMenu();
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        getMyCard();
        getPrompt();
    }

    private void getPrompt() {
        Global g = new Global();
        g.read(DBUtils.getDBsa(1));
        if (g.getNewPersonChatNum() > 0) {
            setMenuPrompt(2, true);
        }
    }

    public void getMyCard() {
        MyCardTask task = new MyCardTask();
        task.setTaskCallBack(new PostCallBack<RetError>() {
            @Override
            public void taskFinish(RetError result) {
                setAvatar(myCard.getAvatar());
            }

            @Override
            public void readDBFinish() {
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = myCard.getAvatar();
                mHandler.sendMessage(msg);
            }
        });
        task.executeWithCheckNet(myCard);

    }

    /**
     * 设置头像
     * 
     * @param avatarUrl
     */
    public void setAvatar(String avatarUrl) {
        if ("".equals(avatarUrl)) {
            avatar.setImageResource(R.drawable.head_bg);
            return;
        }
        UniversalImageLoadTool.disPlay(avatarUrl, new RotateImageViewAware(
                avatar, avatarUrl), R.drawable.head_bg);
    }

    private void getMenu() {
        menulist.clear();
        MenuModle modle = new MenuModle();
        modle.setAngle(true);
        modle.setMenu("我的圈子");
        menulist.add(modle);
        modle = new MenuModle();
        modle.setAngle(false);
        modle.setMenu("我的名片");
        modle.setNofiyPrompt(false);
        menulist.add(modle);
        modle = new MenuModle();
        modle.setAngle(false);
        modle.setMenu("私信");
        modle.setNofiyPrompt(SharedUtils.getInt("loginType", 0) == 1);
        menulist.add(modle);
        modle = new MenuModle();
        modle.setAngle(false);
        modle.setMenu("设置");
        menulist.add(modle);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menulist.size();
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
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.menu_item, null);
                holder = new ViewHolder();
                holder.txt = (TextView) convertView.findViewById(R.id.menutxt);
                holder.angle = (ImageView) convertView.findViewById(R.id.angle);
                holder.notifyPrompt = (ImageView) convertView
                        .findViewById(R.id.notifyPrompt);
                holder.layBg = (RelativeLayout) convertView
                        .findViewById(R.id.bg);
                WigdtContorl.setLayoutX(holder.angle,
                        Utils.getSecreenWidth(getActivity()) / 2 - 18);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (menulist.get(position).isAngle()) {
                holder.angle.setVisibility(View.VISIBLE);
                holder.txt.setTextColor(Color.WHITE);
                holder.layBg.setBackgroundColor(getActivity().getResources()
                        .getColor(R.color.left_menu_blue));
            } else {
                holder.angle.setVisibility(View.GONE);
                holder.txt.setTextColor(getActivity().getResources().getColor(
                        R.color.default_font_color));
                holder.layBg.setBackgroundColor(getActivity().getResources()
                        .getColor(R.color.left_menu_black));
            }
            holder.txt.setText(menulist.get(position).getMenu());
            if (menulist.get(position).isNofiyPrompt()) {
                holder.notifyPrompt.setVisibility(View.VISIBLE);
            } else {
                holder.notifyPrompt.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    class ViewHolder {
        TextView txt;
        ImageView angle;
        ImageView notifyPrompt;
        RelativeLayout layBg;
    }

    class MenuModle {
        String menu;
        boolean angle;
        boolean nofiyPrompt;

        public boolean isNofiyPrompt() {
            return nofiyPrompt;
        }

        public void setNofiyPrompt(boolean nofiyPrompt) {
            this.nofiyPrompt = nofiyPrompt;
        }

        public String getMenu() {
            return menu;
        }

        public void setMenu(String menu) {
            this.menu = menu;
        }

        public boolean isAngle() {
            return angle;
        }

        public void setAngle(boolean angle) {
            this.angle = angle;
        }

    }

    /**
     * 菜单FragMent切换
     * 
     * @author teeker_bin
     * 
     */
    public interface onChangeFragMentListener {
        void onChangeFragMent(int arg0);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int posititon,
            long arg3) {
        for (int i = 0; i < menulist.size(); i++) {
            menulist.get(i).setAngle(false);
        }
        menulist.get(posititon).setAngle(true);
        adapter.notifyDataSetChanged();
        mOnChangeFragMentListener.onChangeFragMent(posititon);
    }

    public void setMenuPrompt(int position, boolean flag) {
        menulist.get(position).setNofiyPrompt(flag);
        adapter.notifyDataSetChanged();
    }

    public void setCurrentMenu(int position) {
        for (MenuModle model : menulist) {
            model.setAngle(false);
        }
        menulist.get(position).setAngle(true);
        adapter.notifyDataSetChanged();
    }
}