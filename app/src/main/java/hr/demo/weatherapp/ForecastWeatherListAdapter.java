package hr.demo.weatherapp;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hr.demo.weatherapp.models.FDDayWeather;

public class ForecastWeatherListAdapter extends RecyclerView.Adapter<ForecastWeatherListAdapter.MyViewHolder> {
    private List<FDDayWeather> mDataset;
    SimpleDateFormat simpleDateFormat;
    Context context;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTemperatureMax;
        public TextView textViewTemperatureMin;
        public TextView textViewDescription;
        public TextView textViewDate;
        public ImageView imageViewIcon;
        public MyViewHolder(ConstraintLayout v) {
            super(v);
            textViewTemperatureMax = v.findViewById(R.id.textViewTemperatureMax);
            textViewDescription = v.findViewById(R.id.textViewDescription);
            textViewTemperatureMin = v.findViewById(R.id.textViewTemperatureMin);
            textViewDate = v.findViewById(R.id.textViewDate);
            imageViewIcon = v.findViewById(R.id.imageViewIcon);
        }
    }


    public ForecastWeatherListAdapter(Context context, List<FDDayWeather> myDataset) {
        this.mDataset = myDataset;
        simpleDateFormat=new SimpleDateFormat("dd.MM");
        this.context=context;
    }

    @Override
    public ForecastWeatherListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {

        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_forecast_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        FDDayWeather fdDayWeather=mDataset.get(position);

        holder.textViewDate.setText(simpleDateFormat.format(new Date(fdDayWeather.getDt())));
        holder.textViewTemperatureMin.setText((Math.round(fdDayWeather.getMinTemp())+"°C"));
        holder.textViewTemperatureMax.setText((Math.round(fdDayWeather.getMaxTemp())+"°C"));
        holder.textViewDescription.setText(fdDayWeather.getDescription());
        holder.imageViewIcon.setImageResource(WeatherCodes.getImageResourceIdForWeatherCode(context, fdDayWeather.getCode()));

    }
    public void setNewForecastData(List<FDDayWeather> myDataset){
        this.mDataset=myDataset;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
