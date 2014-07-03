package com.changlianxi.task;

import java.util.List;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;

public class IinviteCircleMemberTask extends
        BaseAsyncTask<CircleMember, Void, RetError> {
    private List<CircleMember> mebers;
    private CircleMember cMember;

    public IinviteCircleMemberTask(List<CircleMember> mebers) {
        this.mebers = mebers;
    }

    @Override
    protected RetError doInBackground(CircleMember... params) {
        cMember = params[0];
        if (!isNet) {
            return RetError.NETWORK_ERROR;
        }
        RetError err = cMember.inviteMore(mebers);
        return err;
    }

}
