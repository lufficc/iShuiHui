package com.lufficc.ishuhui.activity.preview;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lufficc.ishuhui.R;

/**
 * Created by lufficc on 2016/11/10.
 */

public class DanmuViewPresenter implements View.OnClickListener {
    private View rootView;
    private EditText editText;
    private Button bt_send;
    private DanmuViewListener listener;

    public DanmuViewListener getListener() {
        return listener;
    }

    public void setListener(DanmuViewListener listener) {
        this.listener = listener;
    }

    public DanmuViewPresenter(View rootView) {
        this.rootView = rootView;
        editText = (EditText) rootView.findViewById(R.id.et_danmu);
        bt_send = (Button) rootView.findViewById(R.id.bt_send);
        bt_send.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                if (listener != null && !TextUtils.isEmpty(editText.getText().toString())) {
                    listener.onSend(editText.getText().toString());
                    editText.setText("");
                }
                break;
        }
    }
    public interface DanmuViewListener {
        void onSend(String msg);
    }
}
