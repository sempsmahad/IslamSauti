package com.example.myapplication.ui;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Summon;

import java.util.List;

class SummonAdapter extends RecyclerView.Adapter<SummonHolder> {
    private final ListActivity mListActivity;
    private       List<Summon> mSummons;

    private OnItemClickListener       mOnItemClickListener;
    private OnMoreButtonClickListener onMoreButtonClickListener;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public void setOnMoreButtonClickListener(final OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }

    public SummonAdapter(ListActivity listActivity, List<Summon> summons) {
        mListActivity = listActivity;
        mSummons      = summons;
    }

    @NonNull
    @Override
    public SummonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mListActivity);
        return new SummonHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SummonHolder holder, int position) {
        Summon summon = mSummons.get(position);
        holder.bind(summon);

        holder.lyt_parent.setOnClickListener((View.OnClickListener) view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, summon, position);
            }
        });

        holder.menu.setOnClickListener(view -> {
            if (onMoreButtonClickListener == null) return;
            onMoreButtonClick(view, summon);

        });
    }

    @Override
    public int getItemCount() {
        return mSummons.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Summon obj, int pos);
    }

    public interface OnMoreButtonClickListener {
        void onItemClick(View view, Summon obj, MenuItem item);
    }

    private void onMoreButtonClick(final View view, final Summon p) {
        PopupMenu popupMenu = new PopupMenu(mListActivity, view);
        popupMenu.setOnMenuItemClickListener(item -> {
            onMoreButtonClickListener.onItemClick(view, p, item);
            return true;
        });
        popupMenu.inflate(R.menu.menu_song_more);
        popupMenu.show();
    }
}
