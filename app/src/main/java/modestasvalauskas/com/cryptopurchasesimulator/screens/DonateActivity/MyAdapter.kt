package modestasvalauskas.com.cryptopurchasesimulator.screens.DonateActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.donalecell.view.*
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappings

class MyAdapter(var activity: () -> Activity, var arrayList: ArrayList<Triple<CurrencyMappings, String, Int>>, val context: Context, val layout: Int) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindItems(arrayList[position])

    override fun getItemCount(): Int = arrayList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Triple<CurrencyMappings, String, Int>) {
            itemView.donateimage.setImageDrawable(ContextCompat.getDrawable(context, item.first.resID))
            itemView.donatename.setText(item.first.naming + " (${item.first.shortnaming})")
            itemView.donatebutton.setOnClickListener {
                activity().startActivity(Intent(context, DisplayAddressActivity::class.java)
                        .putExtra(DisplayAddressActivity.COINTYPE, item))
            }
        }
    }
}