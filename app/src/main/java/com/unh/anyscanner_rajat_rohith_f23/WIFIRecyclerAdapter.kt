package com.unh.anyscanner_rajat_rohith_f23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WIFIRecyclerAdapter(private val dataSet: Array<String>,private val dataSet2: Array<String>) :
    RecyclerView.Adapter<WIFIRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wifiSSID: TextView
        val wifiSecurity: TextView

        init {
            // Define click listener for the ViewHolder's View
            wifiSSID = view.findViewById(R.id.wifiSSID)
            wifiSecurity = view.findViewById(R.id.wifiSecurity)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.wifi_card, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.wifiSSID.text = dataSet[position]
        viewHolder.wifiSecurity.text = dataSet2[position]

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
