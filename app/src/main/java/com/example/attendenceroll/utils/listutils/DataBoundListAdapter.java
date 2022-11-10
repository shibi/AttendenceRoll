package com.example.attendenceroll.utils.listutils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class DataBoundListAdapter<T,VH extends DataBoundViewHolder> extends RecyclerView.Adapter<VH> {

    public List<T> listItems;

    // each time data is set, we update this variable so that if DiffUtil calculation returns
    // after repetitive updates, we can ignore the old calculation
    private int dataVersion = 0;

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int layout = createLayoutView();
        View view = inflater.inflate(layout, parent, false);
        VH viewHolderItem = wrapViewHolder(view);
        return viewHolderItem;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T rowItem = getItems(position);
        if(rowItem!=null){
            onBind(holder, position, rowItem);
        }else {
            Log.e("--------","empty");
        }
    }

    @Override
    public int getItemCount() {
        return (listItems!=null)?listItems.size():0;
    }

    public T getItems(int position){
        if(listItems!=null && listItems.size()> position){
            return listItems.get(position);
        }else {
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    public void replace(List<T> update) {
        dataVersion ++;
        if (listItems == null) {
            if (update == null) {
                return;
            }
            listItems = update;
            notifyDataSetChanged();
        } else if (update == null) {
            int oldSize = listItems.size();
            listItems = null;
            notifyItemRangeRemoved(0, oldSize);
        } else {
            final int startVersion = dataVersion;
            final List<T> oldItems = listItems;
            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                @SuppressLint("WrongThread")
                @Override
                protected DiffUtil.DiffResult  doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return update.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = update.get(newItemPosition);
                            return DataBoundListAdapter.this.areItemsTheSame(oldItem, newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = update.get(newItemPosition);
                            return DataBoundListAdapter.this.areContentsTheSame(oldItem, newItem);
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (startVersion != dataVersion) {
                        // ignore update
                        return;
                    }
                    listItems = update;
                    diffResult.dispatchUpdatesTo(DataBoundListAdapter.this);
                    dispatched();

                }
            }.execute();
        }
    }



    public abstract int createLayoutView();
    public abstract VH wrapViewHolder(View view);
    public abstract void onBind(@NonNull VH holder, int position, T item);
    protected abstract boolean areItemsTheSame(T oldItem, T newItem);
    protected abstract boolean areContentsTheSame(T oldItem, T newItem);
    protected abstract void dispatched();


}
