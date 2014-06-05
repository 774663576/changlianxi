package com.changlianxi.task;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Global;
import com.changlianxi.data.enums.RetError;

public class InitMyDetailsTask extends BaseAsyncTask<Void, Void, RetError> {
    private int cid;
     private String circleIDs = "";
    private String personalIDs = "";

    public InitMyDetailsTask(int cid, String circleIds, String personalIds) {
        this.cid = cid;
        this.circleIDs = circleIds;
        this.personalIDs = personalIds;
    }

    @Override
    protected RetError doInBackground(Void... params) {
        CircleMember c = new CircleMember(cid, 0, Global.getIntUid());
        RetError ret = c.initMydetails(circleIDs, personalIDs);
        return ret;
    }

}
