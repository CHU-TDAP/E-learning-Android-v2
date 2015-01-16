package tw.edu.chu.csie.dblab.uelearning.android.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.learning.ActivityManager;

/**
 * 學習引導畫面（顯示推薦學習點的地方）
 */
public class StudyGuideFragment  extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    protected static final int REMAINED_TIME = 0x101;

    private ListView mList_nextPoints;
    private SwipeRefreshLayout mSwipe_nextPoints;
    private TextView mText_remainedTime;
    private ImageView mImage_map;
    private Timer updateUITimer;

    public static StudyGuideFragment newInstance(int sectionNumber) {
        StudyGuideFragment fragment = new StudyGuideFragment();
        return fragment;
    }

    public StudyGuideFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_study_guide, container, false);
        initUI(rootView);

        updateUITimer = new Timer();
        updateUITimer.schedule(new UpdateUITask(), 0, 1 * 1000);

        return rootView;
    }

    protected void initUI(View rootView) {

        mList_nextPoints = (ListView) rootView.findViewById(R.id.list_learning_next_points);
        mList_nextPoints.setOnItemClickListener(this);
        mSwipe_nextPoints = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_next_points);
        mSwipe_nextPoints.setOnRefreshListener(this);
        mText_remainedTime = (TextView) rootView.findViewById(R.id.text_learning_remaining_time);
        mImage_map = (ImageView) rootView.findViewById(R.id.image_learning_next_points);
    }

    // ============================================================================================

    Handler updateUIHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what) {
                case REMAINED_TIME:
                    Date learningTime = ActivityManager.getRemainderLearningTime(getActivity());
                    Calendar learningCal = Calendar.getInstance();
                    learningCal.setTime(learningTime);
                    learningCal.setTimeZone(TimeZone.getTimeZone("UTC"));

                    mText_remainedTime.setText(learningCal.get(Calendar.HOUR_OF_DAY)+":"+learningCal.get(Calendar.MINUTE)+":"+learningCal.get(Calendar.SECOND));
                    break;
            }
        };
    };

    class UpdateUITask extends TimerTask {

        @Override
        public void run() {
            Message message = new Message();
            message.what = StudyGuideFragment.REMAINED_TIME;

            updateUIHandler.sendMessage(message);
        }

    }

    public void stopUpdateUITask() {
        updateUITimer.cancel();
    }

    // ============================================================================================

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 裝置螢幕旋轉時，重新載入界面
        // Get a layout inflater (inflater from getActivity() or getSupportActivity() works as well)
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newView = inflater.inflate(R.layout.fragment_study_guide, null);
        // This just inflates the view but doesn't add it to any thing.
        // You need to add it to the root view of the fragment
        ViewGroup rootView = (ViewGroup) getView();
        // Remove all the existing views from the root view.
        // This is also a good place to recycle any resources you won't need anymore
        rootView.removeAllViews();
        rootView.addView(newView);

        initUI(rootView);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onRefresh() {
        mSwipe_nextPoints.setRefreshing(false);
    }

    @Override
    public void onPause() {
        stopUpdateUITask();
        super.onPause();
    }
}