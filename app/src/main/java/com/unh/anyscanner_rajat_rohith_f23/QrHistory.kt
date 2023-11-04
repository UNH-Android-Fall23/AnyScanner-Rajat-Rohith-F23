package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Rect
import android.widget.ImageButton
import com.google.firebase.firestore.Query

class QrHistory : AppCompatActivity() {
    private lateinit var qrLinkAdapter: QrLinkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_history)

        val recyclerView = findViewById<RecyclerView>(R.id.QrRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        qrLinkAdapter = QrLinkAdapter()
        recyclerView.adapter = qrLinkAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.card_spacing)
        recyclerView.addItemDecoration(SpaceItemDecoration(spacingInPixels))

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val query = FirebaseFirestore.getInstance()
                .collection("QR")
                .document(user.uid)
                .collection("links")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            query.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val qrLinks = snapshots?.documents?.map { document ->
                    val url = document.getString("url") ?: ""
                    val isMalicious = document.getBoolean("isMalicious") ?: false
                    QrLink(url, isMalicious)
                }
                qrLinkAdapter.submitList(qrLinks)
            }
        }
    }
}

data class QrLink(val url: String = "", val isMalicious: Boolean = false)

class QrLinkAdapter : ListAdapter<QrLink, QrLinkViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<QrLink>() {
        override fun areItemsTheSame(oldItem: QrLink, newItem: QrLink): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: QrLink, newItem: QrLink): Boolean {
            return oldItem.url == newItem.url
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QrLinkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_qr_link, parent, false)
        return QrLinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: QrLinkViewHolder, position: Int) {
        val qrLink = getItem(position)
        holder.bind(qrLink)
    }
}

class QrLinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val urlTextView: TextView = itemView.findViewById(R.id.urlTextView)
    private val maliciousTextView: TextView = itemView.findViewById(R.id.maliciousTextView)
    private val shareButton: ImageButton = itemView.findViewById(R.id.shareButton)

    fun bind(qrLink: QrLink) {
        urlTextView.text = "URL: ${qrLink.url}"
        if (qrLink.isMalicious) {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.red))
            maliciousTextView.text = "QR Link is Malicious"
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green))
            maliciousTextView.text = "QR Link is Harmless"
        }

        shareButton.setOnClickListener {
            onShareClick(qrLink.url)
        }
    }
    private fun onShareClick(url: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        itemView.context.startActivity(shareIntent)
    }
}

class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space
        }
    }
}
