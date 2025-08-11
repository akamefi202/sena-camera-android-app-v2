package com.sena.senacamera.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sena.senacamera.R;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.fragment.FragmentNewPassword;
import com.sena.senacamera.ui.fragment.FragmentOptions;

import java.util.ArrayList;

public class OptionListAdapter extends ArrayAdapter<String> {
    private static final String TAG = OptionListAdapter.class.getSimpleName();

    public ArrayList<String> arrayList = null;
    private Context context;
    private FragmentOptions parentFragment;
    private final View.OnClickListener buttonClickListener = view -> {
        int itemIndex = Integer.parseInt(view.getTag().toString());
        if (itemIndex > -1 && itemIndex < OptionListAdapter.this.arrayList.size()) {
            int id = view.getId();
            if (id == R.id.option_layout) {
                this.parentFragment.selectOption(itemIndex);
                this.notifyDataSetChanged();
            }
        }
    };

    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;
    static class ViewHolder {
        ImageView optionStatus = null;
        TextView optionTitle = null;
        LinearLayout optionLayout = null;

        ViewHolder() {
        }
    }

    public OptionListAdapter(@NonNull Context context, int resource, ArrayList<String> arrayList, FragmentOptions parentFragment) {
        super(context, resource, arrayList);

        this.context = context;
        this.arrayList = arrayList;
        this.inflater = LayoutInflater.from(context);
        this.parentFragment = parentFragment;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public String getItem(int i) {
        return (String) super.getItem(i);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.inflater.inflate(R.layout.item_option, (ViewGroup) null);
            this.viewHolder = new ViewHolder();
            this.viewHolder.optionTitle = (TextView) view.findViewById(R.id.option_title);
            this.viewHolder.optionStatus = (ImageView) view.findViewById(R.id.option_status);
            this.viewHolder.optionLayout = (LinearLayout) view.findViewById(R.id.option_layout);
            view.setTag(this.viewHolder);
        } else {
            this.viewHolder = (ViewHolder) view.getTag();
        }

        this.viewHolder.optionLayout.setTag(position);
        this.viewHolder.optionLayout.setOnClickListener(this.buttonClickListener);
        this.viewHolder.optionLayout.setFocusable(false);

        this.viewHolder.optionTitle.setText(this.arrayList.get(position));

        if (this.arrayList.get(position).equals(parentFragment.getValue())) {
            this.viewHolder.optionStatus.setVisibility(View.VISIBLE);
        } else {
            this.viewHolder.optionStatus.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
