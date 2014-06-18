package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

/**
 * 接受圈子邀请
 * @author teeker_bin
 *
 */
public class AcceptCircleInvitationTask extends
        BaseAsyncTask<CircleMember, Void, RetError> {
    private CircleMember member;

    @Override
    protected RetError doInBackground(CircleMember... params) {
        member = params[0];
        member.read(DBUtils.getDBsa(1));
        RetError ret = member.acceptInvitation();
        if (ret == RetError.NONE) {
            member.readDetails(DBUtils.getDBsa(1));
            member.write(DBUtils.getDBsa(2));
            Circle c = new Circle(member.getCid());
            c.read(DBUtils.getDBsa(1));
            c.setNew(false);
            c.setStatus(com.changlianxi.data.AbstractData.Status.UPDATE);
            c.write(DBUtils.getDBsa(2));
        }
        return ret;
    }

}
