package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.MyCard;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class CreateNewCircleTask extends BaseAsyncTask<Circle, Void, RetError> {
    private Circle circle = null;

    @Override
    protected RetError doInBackground(Circle... params) {
        circle = params[0];
        RetError reError = circle.uploadForAdd();
        if (reError == RetError.NONE) {
            circle.write(DBUtils.getDBsa(2));

            /**
             * 创建完圈子之后 先在成员列表里写入一条自己的数据 ,避免创建完圈子之后 编辑个人资料 数据重复问题
             */
            MyCard mycard = new MyCard(0, Global.getIntUid());
            mycard.read(DBUtils.getDBsa(2));
            CircleMember memberForMe = new CircleMember(circle.getId(),
                    mycard.getPid(), mycard.getUid());
            memberForMe.write(DBUtils.getDBsa(2));
        }

        return reError;
    }

}
