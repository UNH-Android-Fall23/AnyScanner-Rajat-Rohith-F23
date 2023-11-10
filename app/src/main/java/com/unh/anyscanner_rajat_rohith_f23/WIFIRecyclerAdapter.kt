package com.unh.anyscanner_rajat_rohith_f23

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WIFIRecyclerAdapter(private val dataSet: Array<String>,
                          private val dataSet2: Array<String>,
                          private val connectedPosition: Int) :
    RecyclerView.Adapter<WIFIRecyclerAdapter.ViewHolder>() {
    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wifiSSID: TextView
        val wifiSecurity: TextView

        init {
            // Define click listener for the ViewHolder's View
            wifiSSID = view.findViewById(R.id.wifiSSID)
            wifiSecurity = view.findViewById(R.id.wifiSecurity)

        }
    }
    private var itemClickListener: ItemClickListener? = null

    fun setItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
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
        // Highlight the connected network
        if (position == connectedPosition) {
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#FFD700")) // Use your desired highlight color
        } else {
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
        viewHolder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
