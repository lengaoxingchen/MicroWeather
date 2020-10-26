package cn.testrunner.city_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.testrunner.R;

import java.util.List;

public class DeleteAdapter extends BaseAdapter {
    Context context;
    List<String> mDatas;

    //要删除的城市集合
    List<String> deleteCities;

    public DeleteAdapter(Context context, List<String> mDatas, List<String> deleteCities) {
        this.context = context;
        this.mDatas = mDatas;
        this.deleteCities = deleteCities;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_deletecity, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String city = mDatas.get(position);
        holder.tv.setText(city);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatas.remove(city);
                deleteCities.add(city);
                notifyDataSetChanged();//删除了提示适配器更新
            }
        });
        return convertView;
    }


    class ViewHolder {
        TextView tv;
        ImageView iv;

        public ViewHolder(View itemView) {
            this.tv = itemView.findViewById(R.id.item_delete_tv);
            this.iv = itemView.findViewById(R.id.item_delete_iv);
        }
    }
}
