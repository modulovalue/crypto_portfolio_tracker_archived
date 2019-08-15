package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.SettingsFragment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.itemsettingscell.view.*
import modestasvalauskas.com.cryptopurchasesimulator.R

class MyRecyclerViewAdapter(context: Context, var data: ArrayList<Triple<String, String, () -> Unit>>) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    var mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.itemsettingscell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val d = data.get(position)
        holder.title.text = d.first
        holder.subtitle.text = d.second
        if (d.second.isEmpty()) {
            holder.subtitle.visibility = View.GONE
        } else {
            holder.subtitle.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var title: TextView = itemView.settingstitle
        var subtitle: TextView = itemView.settingssubtitle

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            data[adapterPosition].third()
        }
    }

}