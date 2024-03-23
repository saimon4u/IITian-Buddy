package com.example.iitianbuddy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.iitianbuddy.R
import com.example.iitianbuddy.models.Event
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HomeFragmentEventAdapter(val context: Context, val list:ArrayList<Event>): RecyclerView.Adapter<HomeFragmentEventAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentEventAdapter.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.event_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeFragmentEventAdapter.ViewHolder, position: Int) {
        val event: Event = list[position]
        Picasso.get().load(event.imgLink).into(holder.img)
        holder.title.text = event.title

        holder.itemView.setOnClickListener {
            holder.bottomCard.visibility = View.VISIBLE
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.bottom_animation)
            holder.bottomCard.startAnimation(animation)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.event_img)
        val title: TextView = itemView.findViewById(R.id.event_title)
        val details: CircleImageView = itemView.findViewById(R.id.event_details)
        val bottomCard: CardView = itemView.findViewById(R.id.bottom_card)
    }
}