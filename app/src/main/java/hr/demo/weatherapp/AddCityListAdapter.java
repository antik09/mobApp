package hr.demo.weatherapp;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hr.demo.weatherapp.models.CityId;
//TODO: adapter liste gradova koji se prikazuje prilikom dodavanje novog grada
public class AddCityListAdapter extends RecyclerView.Adapter<AddCityListAdapter.MyViewHolder> {
    private List<CityId> mDataset;
    private AddNewCityActivity.OnItemClickListener itemClicklistener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MyViewHolder(ConstraintLayout v) {
            super(v);
            textView = v.findViewById(R.id.textView);
        }
    }


    public AddCityListAdapter(List<CityId> myDataset, AddNewCityActivity.OnItemClickListener listener) {
        mDataset = myDataset;
        this.itemClicklistener = listener;
    }

    @Override
    public AddCityListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {

        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_add_city_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.textView.setText((mDataset.get(position)).city);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                itemClicklistener.onItemClick(mDataset.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
