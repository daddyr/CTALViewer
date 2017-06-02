package com.example.android.ctalviewer;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.ctalviewer.Utilities.TrainUtils;

/**
 * Created by Ryan on 2/14/2017.
 */

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder>{


    private Cursor mTrainData;
    private Context mContext;

    final int viewStop = R.layout.stop_list_item;
    final int viewTrain = R.layout.train_list_item;

    private ListItemClickListener mClickHander;

    public interface ListItemClickListener{
        void onListeItemClick(String clickedItem);
    }

    public TrainAdapter(ListItemClickListener handler, Context context){
        mClickHander = handler;
        mContext = context;
    }


    @Override
    public int getItemCount() {
        if(mTrainData == null) return 0;

        return mTrainData.getCount()+1;
    }

    @Override
    public TrainViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediatly = false;

        View view = inflater.inflate(viewType,parent,attachImmediatly);
        view.setTag(viewType);
        return new TrainViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return viewStop;

        return viewTrain;
    }

    @Override
    public void onBindViewHolder(TrainViewHolder holder, int position) {
        if(position == 0){
            mTrainData.moveToFirst();
            String stop = mTrainData.getString(ArrivalsViewer.STOPNAME);

            holder.mStopTextView.setText(stop);
        }else {
            mTrainData.moveToPosition(position-1);

            String dest = mTrainData.getString(ArrivalsViewer.DESTINATION);
            String currentTime = mTrainData.getString(ArrivalsViewer.TIMESTAMP);
            String arrivalTime = mTrainData.getString(ArrivalsViewer.ARRIVALTIME);
            String route = mTrainData.getString(ArrivalsViewer.ROUTE);

            String text = TrainUtils.formatTimeString(arrivalTime, currentTime);

            holder.mTimeTextView.setText(text);
            holder.mLineTextView.setText(TrainUtils.getRouteString(route) + " to:");
            holder.mDestinationTextView.setText(dest);

            int color = TrainUtils.getTrainColor(route);
            holder.mBackground.setBackgroundColor(mContext.getResources().getColor(color));
        }
    }


    void swapCursor(Cursor newCursor){
        mTrainData = newCursor;
        notifyDataSetChanged();
    }

    class TrainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mLineTextView;
        public TextView mDestinationTextView;
        public TextView mTimeTextView;
        public TextView mStopTextView;
        public ImageView mBackground;

        public TrainViewHolder(View itemView){
            super(itemView);

            int id = Integer.valueOf(itemView.getTag().toString());
            if(id == viewTrain) {
                mLineTextView = (TextView) itemView.findViewById(R.id.textViewLine);
                mDestinationTextView = (TextView) itemView.findViewById(R.id.textViewDestination);
                mTimeTextView = (TextView) itemView.findViewById(R.id.textViewTime);
                mBackground = (ImageView) itemView.findViewById(R.id.background);
            }else {
                mStopTextView = (TextView) itemView.findViewById(R.id.stop_name);
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            //ToDo fix onClick;
            //String clickedItem = mTrainTextView.getText().toString();
            //mClickHander.onListeItemClick(clickedItem);
        }
    }
}
