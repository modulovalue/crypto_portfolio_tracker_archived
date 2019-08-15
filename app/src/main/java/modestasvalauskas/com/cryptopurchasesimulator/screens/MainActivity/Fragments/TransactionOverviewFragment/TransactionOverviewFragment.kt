package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.TransactionOverviewFragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_transactionoverview.*
import kotlinx.android.synthetic.main.itemtransactioncell.view.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappings
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.persistence.UnsupportedCurrencyException
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.*
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings
import java.text.DateFormat
import java.util.*
import kotlin.properties.Delegates


class TransactionOverviewFragment : Fragment(), BackableToOverview {

    private val priceDataInterface: PriceDataInterface by lazy { (activity as PriceDataInterface) }

    private val titleInterface: TitleInterface by lazy { (activity as TitleInterface) }

    var newDataObservable = Observer { _, _ ->
        if (transactionoverviewrv != null) {
            (transactionoverviewrv.adapter as MyAdapterTransaction).arrayList = TransactionData.getTransactions()
            (transactionoverviewrv.adapter as MyAdapterTransaction).notifyDataSetChanged()
        }
        titleInterface.setSubtitle("")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transactionoverview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionoverviewrv.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        transactionoverviewrv.adapter = MyAdapterTransaction(
                { activity!! },
                { newDataObservable.update(null, null) },
                arrayListOf(),
                context!!,
                R.layout.itemtransactioncell)
        transactionoverviewrv.addItemDecoration(DividerItemDecoration(context, (transactionoverviewrv.layoutManager as LinearLayoutManager).orientation))
        newDataObservable.update(null, null)
    }

    override fun onStart() {
        super.onStart()
        newDataObservable.update(null, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).observableNewData.addObserver(newDataObservable)
        titleInterface.setTitle(activity!!.resources.getString(R.string.transactionoverviewtitle))
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).observableNewData.deleteObserver(newDataObservable)
    }

}


class MyAdapterTransaction : RecyclerView.Adapter<MyAdapterTransaction.ViewHolder> {

    var activity: () -> Activity
    var dataDidUpdate: () -> Unit
    var lastCurrencyTransactions: HashMap<CurrencyMappings, Transaction> = hashMapOf()
    var arrayList: ArrayList<Transaction> by Delegates.observable(arrayListOf()) { property, old, new ->
        lastCurrencyTransactions = hashMapOf()
        new.forEach { transaction ->
            lastCurrencyTransactions.put(transaction.currencyType, transaction)
        }
    }
    val context: Context
    val layout: Int

    constructor(activity: () -> Activity, dataDidUpdate: () -> Unit, arrayList: ArrayList<Transaction>, context: Context, layout: Int) : super() {
        this.activity = activity
        this.dataDidUpdate = dataDidUpdate
        this.arrayList = arrayList
        this.context = context
        this.layout = layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(activity, LayoutInflater.from(parent.context).inflate(layout, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindItems(arrayList[position])

    override fun getItemCount(): Int = arrayList.size

    inner class ViewHolder(var activity: () -> Activity, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: Transaction) {
            setBasicData(item)
            setTime(item)
            transactionArrow(item)
            editButtons(item)
        }

        fun setBasicData(item: Transaction) {
            itemView.transacticryptoname.text = "${item.currencyType.naming} (${item.currencyType.shortnaming})"
            itemView.transacticryptoimage.setImageDrawable(ContextCompat.getDrawable(context, item.currencyType.resID))
            itemView.transactivalue.text = "${Math.abs(item.transactionAmount)} ${item.currencyType.shortnaming}"
            itemView.transactitotext.text = GlobalSettings.euroOrDollar.name
            try {
                itemView.transactitovalue.text = "%.2f ${context.resources.getString(GlobalSettings.euroOrDollar.resID)}".format(Math.abs(item.valueInGlobalCurrency()))
            } catch (e: UnsupportedCurrencyException) {
                itemView.transactitovalue.text = "-"
            }
        }

        fun setTime(item: Transaction) {
            val cal = Calendar.getInstance()
            val tz = cal.timeZone
            val sdf = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)
            sdf.timeZone = tz
            val localTime = sdf.format(Date(item.transactionTimestamp * 1000)) // I assume your timestamp is in seconds and you're converting to milliseconds?
            itemView.transactitime.text = "${localTime}"
        }

        fun transactionArrow(item: Transaction) {
            if (item.transactionAmount < 0) {
                val drawable = ContextCompat.getDrawable(context, R.drawable.arrowright)
                DrawableCompat.setTint(drawable!!, ContextCompat.getColor(context, R.color.greenpercentage))
                itemView.transactiarrow.setImageDrawable(drawable)
            } else {
                val drawable = ContextCompat.getDrawable(context, R.drawable.arrowleft)
                DrawableCompat.setTint(drawable!!, ContextCompat.getColor(context, R.color.redpercentage))
                itemView.transactiarrow.setImageDrawable(drawable)

            }
        }

        fun editButtons(item: Transaction) {
            if(lastCurrencyTransactions.get(item.currencyType) == item) {
                itemView.transactieditlayouts.visibility = View.VISIBLE

                itemView.transactideltebutton.setOnClickListener {
                    MaterialDialog.Builder(context)
                            .title(R.string.areyousureaboutdeletion)
                            .content(R.string.doyoureallywanttodeletethistransaction)
                            .positiveText(R.string.agree)
                            .onPositive { _, _ ->
                                TransactionData.removeTransaction(item)
                                dataDidUpdate()
                            }
                            .negativeText(R.string.disagree)
                            .show()
                }

                itemView.transactieditbutton.setOnClickListener {
                    Toasty.normal(context, context.resources.getString(R.string.willworknextversion), Toast.LENGTH_LONG).show()
                    dataDidUpdate()
                }
            } else {
                itemView.transactieditlayouts.visibility = View.GONE
            }
        }

    }
}