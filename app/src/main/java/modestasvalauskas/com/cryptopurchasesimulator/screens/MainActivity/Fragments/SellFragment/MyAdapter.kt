package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.SellFragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.itemsellcell.view.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.statsfragment.MyItem
import modestasvalauskas.com.cryptopurchasesimulator.screens.TransactionActivity.TransactionActivity
import modestasvalauskas.com.cryptopurchasesimulator.screens.TransactionActivity.TransactionType
import modestasvalauskas.com.cryptopurchasesimulator.utils.Currency
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings
import modestasvalauskas.com.cryptopurchasesimulator.utils.TextFormatter
import java.text.DecimalFormat


class MyAdapter(var activity: () -> Activity,
                var updated: () -> Unit,
                var arrayList: ArrayList<MyItem>,
                val context: Context,
                val layout: Int) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(activity, LayoutInflater.from(parent.context).inflate(layout, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindItems(arrayList[position])

    override fun getItemCount(): Int = arrayList.size

    inner class ViewHolder(var activity: () -> Activity, itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: MyItem) {

            val has = TransactionData.amountOf(item.currencyMappings)

            itemView.sellcellimage.setImageDrawable(ContextCompat.getDrawable(context, item.currencyMappings.resID))
            itemView.sellcellimagetext.setText(item.currencyMappings.naming + " (${item.coinData?.symbol ?: "-"}))")
            itemView.sellowns.text = DecimalFormat("0.######").format(has) + " ${item.coinData?.symbol ?: "-"}"

            if (has > 0) {
                if (item.coinData != null) {
                    if (GlobalSettings.euroOrDollar == Currency.EURO) {
                        itemView.selltotalworth.text = "%.2f €".format(has * item.coinData!!.price_eur)
                    } else if (GlobalSettings.euroOrDollar == Currency.DOLLAR) {
                        itemView.selltotalworth.text = "%.2f $".format(has * item.coinData!!.price_usd)
                    } else if (GlobalSettings.euroOrDollar == Currency.BTC) {
                        itemView.selltotalworth.text = "%.2f BTC".format(has * item.coinData!!.price_btc)
                    }

                    itemView.sellsellbutton.setOnClickListener {
                        activity().startActivity(Intent(context, TransactionActivity::class.java)
                                .putExtra(TransactionActivity.TRANSACTIONTYPEEXTRA, TransactionType.SELL)
                                .putExtra(TransactionActivity.CURRENCYEXTRA, item.currencyMappings)
                                .putExtra(TransactionActivity.ONEVALUEDOLLAR, item.coinData!!.price_usd)
                                .putExtra(TransactionActivity.ONEVALUEEURO, item.coinData!!.price_eur)
                        )

                    }
                }
                itemView.sellsellbutton.text = activity().resources.getString(R.string.actionsell)
            } else {
                itemView.sellsellbutton.setOnClickListener {
                    MaterialDialog.Builder(context)
                            .title(activity().resources.getString(R.string.removetransactionstitle, item.currencyMappings.naming))
                            .positiveText(R.string.agree)
                            .negativeText(R.string.abort)
                            .onPositive { _, _ ->
                                TransactionData.removeTransactions(item.currencyMappings)
                                updated()
                            }
                            .show()
                }
                itemView.sellsellbutton.text = activity().resources.getString(R.string.actionremovetransactions)
            }
            itemView.sellprice.text = TextFormatter.price(GlobalSettings.euroOrDollar, item.coinData!!.price_eur, item.coinData!!.price_usd, item.coinData!!.price_btc)


        }
    }
}