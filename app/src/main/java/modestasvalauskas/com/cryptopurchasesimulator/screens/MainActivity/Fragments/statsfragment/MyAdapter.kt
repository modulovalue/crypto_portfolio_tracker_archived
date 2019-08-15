package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.statsfragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.itemstatscell.view.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappings
import modestasvalauskas.com.cryptopurchasesimulator.network.CoinData
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.screens.TransactionActivity.TransactionActivity
import modestasvalauskas.com.cryptopurchasesimulator.screens.TransactionActivity.TransactionType
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings
import modestasvalauskas.com.cryptopurchasesimulator.utils.TextFormatter
import java.text.NumberFormat

data class MyItem(var currencyMappings: CurrencyMappings, var coinData: CoinData?)

class MyAdapter(var activity: () -> Activity, var update: () -> Unit, var arrayList: ArrayList<Pair<MyItem, Boolean>>, val context: Context, val layout: Int) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(activity, LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindItems(arrayList[position])

    override fun getItemCount() = arrayList.size

    inner class ViewHolder(activity: () -> Activity, itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var item: Pair<MyItem, Boolean>

        init {
            itemView.setOnClickListener {
                if (item.first.coinData != null) {
                    var cdata = item.first.coinData
                    val dialog = MaterialDialog.Builder(context)
                            .title("${cdata?.name ?: "Unknown"} (${cdata?.symbol ?: "Unknown"})")
                            .content(R.string.whatdoyouwanttodo)
                            .positiveText(R.string.buy)
                            .onPositive { _, _ ->
                                activity().startActivity(Intent(context, TransactionActivity::class.java)
                                        .putExtra(TransactionActivity.TRANSACTIONTYPEEXTRA, TransactionType.BUY)
                                        .putExtra(TransactionActivity.CURRENCYEXTRA, item.first.currencyMappings)
                                        .putExtra(TransactionActivity.ONEVALUEDOLLAR, item.first.coinData!!.price_usd)
                                        .putExtra(TransactionActivity.ONEVALUEEURO, item.first.coinData!!.price_eur)
                                )
                            }
                            .neutralText(if(item.second) R.string.unmarktext else R.string.marktext)
                            .onNeutral { _, _ ->
                                TransactionData.markCurrency(item.first.currencyMappings)
                                update()
                            }
                    if (TransactionData.amountOf(item.first.currencyMappings) > 0) {
                        dialog
                                .negativeText(R.string.sell)
                                .onNegative { _, _ ->
                                    activity().startActivity(Intent(context, TransactionActivity::class.java)
                                            .putExtra(TransactionActivity.TRANSACTIONTYPEEXTRA, TransactionType.SELL)
                                            .putExtra(TransactionActivity.CURRENCYEXTRA, item.first.currencyMappings)
                                            .putExtra(TransactionActivity.ONEVALUEDOLLAR, item.first.coinData!!.price_usd)
                                            .putExtra(TransactionActivity.ONEVALUEEURO, item.first.coinData!!.price_eur)
                                    )
                                }
                    }
                    dialog.show()
                }
            }
        }

        fun bindItems(item: Pair<MyItem, Boolean>) {

            this.item = item
            itemView.statcellimage.setImageDrawable(ContextCompat.getDrawable(context, item.first.currencyMappings.resID))

            itemView.statcellimagetext.setText((item.first.coinData?.name ?: "Unknown") + " (${item.first.coinData?.symbol ?: "Unknown"})")

            if (item.second) {
                itemView.markedimageview.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.markedcurrency))
            } else {
                itemView.markedimageview.setImageDrawable(null)
            }

            if (item.first.coinData != null) {
                itemView.statsprice.text = TextFormatter.price(
                        GlobalSettings.euroOrDollar,
                        this.item.first.coinData!!.price_eur,
                        item.first.coinData!!.price_usd,
                        item.first.coinData!!.price_btc)

                itemView.statsmarketcap.text = TextFormatter.marketCapText(
                        GlobalSettings.euroOrDollar,
                        this.item.first.coinData!!.market_cap_eur,
                        this.item.first.coinData!!.market_cap_usd,
                        null, context)

                itemView.circulationsupply.text =
                        NumberFormat
                            .getNumberInstance()
                            .format(item.first.coinData!!.available_supply)

                TextFormatter.percentage24H(itemView.change24h, item.first.coinData!!.percent_change_24h, context)
            }

        }
    }
}