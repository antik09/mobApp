package hr.demo.weatherapp;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import hr.demo.weatherapp.models.CityId;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.MyViewHolder> {
    private List<CityId> mDataset;
    private AddNewCityActivity.OnItemClickListener itemClicklistenerSelect;
    private AddNewCityActivity.OnItemClickListenerPosition itemClicklistenerDelete;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageButton buttonDelete;
        public MyViewHolder(ConstraintLayout v) {
            super(v);
            textView = v.findViewById(R.id.textView);
            buttonDelete = v.findViewById(R.id.buttonDelete);
        }
    }


    public CityListAdapter(List<CityId> myDataset, AddNewCityActivity.OnItemClickListener select,  AddNewCityActivity.OnItemClickListenerPosition delete) {
        this.mDataset = myDataset;
        this.itemClicklistenerSelect = select;
        this.itemClicklistenerDelete = delete;
    }

    @Override
    public CityListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {

        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_city_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.textView.setText((mDataset.get(position)).city);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                itemClicklistenerSelect.onItemClick(mDataset.get(position));
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                itemClicklistenerDelete.onItemClick(position);
            }
        });
    }
    public void setNewCitiesData(List<CityId> myDataset){
        this.mDataset=myDataset;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
