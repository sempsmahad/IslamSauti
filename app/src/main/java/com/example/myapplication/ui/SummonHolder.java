package com.example.myapplication.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Summon;

class SummonHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private Summon    mSummon;
    public  ImageView menu;
    public View        lyt_parent;



    private TextView mNameTextView, mDateTextView, mTopicTextView, mTitleTextView;

    public SummonHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.item_list, parent, false));
        itemView.setOnClickListener(this);

        mNameTextView  = itemView.findViewById(R.id.text);
        mTitleTextView = itemView.findViewById(R.id.title);
        menu       = itemView.findViewById(R.id.menu);
        lyt_parent = itemView.findViewById(R.id.lyt_parent);


//          mDateTextView  = itemView.findViewById(R.id.tv_date);
//          mTopicTextView = itemView.findViewById(R.id.tv_topic);
    }

    public void bind(Summon summon) {
        mSummon = summon;
        mNameTextView.setText(mSummon.getName());
        mTitleTextView.setText(mSummon.getTitle());
//          mDateTextView.setText(mSummon.getCreated_at());
//          mTopicTextView.setText(mSummon.getTopic());
    }

    @Override
    public void onClick(View v) {

    }
}
