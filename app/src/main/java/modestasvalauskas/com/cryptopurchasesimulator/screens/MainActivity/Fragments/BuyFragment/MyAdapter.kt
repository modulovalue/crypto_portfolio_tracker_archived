package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.BuyFragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.itembuycell.view.*
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.statsfragment.MyItem
import modestasvalauskas.com.cryptopurchasesimulator.screens.TransactionActivity.TransactionActivity
import modestasvalauskas.com.cryptopurchasesimulator.screens.TransactionActivity.TransactionType
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings
import modestasvalauskas.com.cryptopurchasesimulator.utils.TextFormatter


class MyAdapter(var activity: () -> Activity,
                var arrayList: ArrayList<MyItem>,
                val context: Context,
                val layout: Int) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])
    }

    override fun getItemCount(): Int = arrayList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: MyItem) {

            if (item.coinData != null) {
                TextFormatter.percentage24H(itemView.buychange24h, item.coinData!!.percent_change_24h, context)
                itemView.buyprice.text = TextFormatter.price(GlobalSettings.euroOrDollar, item.coinData!!.price_eur, item.coinData!!.price_usd, item.coinData!!.price_btc)
                itemView.buybuybutton.setOnClickListener {
                    activity().startActivity(Intent(context, TransactionActivity::class.java)
                            .putExtra(TransactionActivity.TRANSACTIONTYPEEXTRA, TransactionType.BUY)
                            .putExtra(TransactionActivity.CURRENCYEXTRA, item.currencyMappings)
                            .putExtra(TransactionActivity.ONEVALUEDOLLAR, item.coinData!!.price_usd)
                            .putExtra(TransactionActivity.ONEVALUEEURO, item.coinData!!.price_eur)
                    )
                }
            }

            itemView.buycellimage.setImageDrawable(ContextCompat.getDrawable(context, item.currencyMappings.resID))
            itemView.buycellimagetext.setText(item.coinData?.name ?: "-" + " (${item.coinData?.symbol ?: "-"})")

        }
    }
}