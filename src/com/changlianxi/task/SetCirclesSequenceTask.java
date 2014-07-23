package com.changlianxi.task;

import com.changlianxi.data.CircleList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.util.SharedUtils;

public class SetCirclesSequenceTask extends
        BaseAsyncTask<CircleList, Void, RetError> {
    private String sequence = "";
    private CircleList circleList = null;

    public SetCirclesSequenceTask(String sequence) {
        this.sequence = sequence;
    }

    @Override
    protected RetError doInBackground(CircleList... params) {
        if (isCancelled() || params == null) {
            return null;
        }
        circleList = params[0];
        RetError retError = circleList.setCirclesSequence(sequence);
        if (retError == RetError.NONE) {
            // for (Circle c : circleList.getCircles()) {
            // c.setStatus(com.changlianxi.data.AbstractData.Status.UPDATE);
            // }
            // circleList
            // .setStatus(com.changlianxi.data.AbstractData.Status.UPDATE);
            // circleList.write(DBUtils.getDBsa(2));
            SharedUtils.setString("circleSequence",
                    sequence.substring(0, sequence.length() - 1));
            System.out.println("compare:::::::__"
                    + sequence.substring(0, sequence.length() - 1));

        }

        return retError;
    }

    @Override
    protected void onPostExecute(RetError result) {
        super.onPostExecute(result);
    }

}
