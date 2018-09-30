package com.atta.ana.classes;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.atta.ana.R;

import java.util.List;

public class PartyCustomListViewAdapter extends ArrayAdapter<OfficialParties> {

    public PartyCustomListViewAdapter(Activity context, List<OfficialParties> parties) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, parties);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Get the  object located at this position in the list
        OfficialParties currentParty = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView shopNameText = (TextView) listItemView.findViewById(R.id.name_text_view);

        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        shopNameText.setText(currentParty.getPartyName());




        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }
}
