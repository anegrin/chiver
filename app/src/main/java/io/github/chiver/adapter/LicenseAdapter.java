package io.github.chiver.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LicenseAdapter extends ArrayAdapter<String> {

    private final Map<String, List<String>> linksToItems;

    public LicenseAdapter(@NonNull Context context, Map<String, List<String>> linksToItems) {
        super(context, android.R.layout.simple_list_item_2, android.R.id.text1, new ArrayList<>(linksToItems.keySet()));
        this.linksToItems = linksToItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        String link = getItem(position);

        TextView text2 = view.findViewById(android.R.id.text2);
        //noinspection ConstantConditions,SimplifyStreamApiCallChains
        text2.setText(linksToItems.getOrDefault(link, Collections.emptyList()).stream().collect(Collectors.joining(", ")));

        return view;
    }
}
