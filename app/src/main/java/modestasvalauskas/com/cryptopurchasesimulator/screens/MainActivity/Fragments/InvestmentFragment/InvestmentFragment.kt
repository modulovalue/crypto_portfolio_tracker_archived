package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.InvestmentFragment

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.fragment_investment.*
import kotlinx.android.synthetic.main.investmentline.view.*
import kotlinx.android.synthetic.main.transactionline.view.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappings
import modestasvalauskas.com.cryptopurchasesimulator.network.*
import modestasvalauskas.com.cryptopurchasesimulator.persistence.Currency2
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.persistence.UnsupportedCurrencyException
import modestasvalauskas.com.cryptopurchasesimulator.persistence.minus
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.MainActivity
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.PriceDataInterface
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.TitleInterface
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Transaction
import modestasvalauskas.com.cryptopurchasesimulator.utils.Currency
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings
import java.text.DecimalFormat
import java.util.*


class InvestmentFragment : Fragment() {

    val priceDataInterface: PriceDataInterface by lazy { (activity as PriceDataInterface) }

    val titleInterface: TitleInterface by lazy { (activity as TitleInterface) }

    var newDataObservable = Observer { _, _ ->
        if (investmentcontainer != null) {
            val quantity = TransactionData.netInvestmentCount(priceDataInterface.getData().map { it.currencyMappings })
            investmentnoinvestmentsavailable.text = resources.getQuantityString(R.plurals.investmentnocryptocurrencyavailable, quantity, quantity)
            populateInvestments()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_investment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).observableNewData.addObserver(newDataObservable)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).observableNewData.deleteObserver(newDataObservable)
    }

    override fun onStart() {
        super.onStart()
        newDataObservable.update(null, null)
        initClickListeners()
        initTitleSubtitle()
    }

    fun initTitleSubtitle() {
        titleInterface.setTitle(activity!!.resources.getString(R.string.investmenttitle))
        titleInterface.setSubtitle("")
    }

    fun initClickListeners() {
        limitlayout.setOnClickListener {
            MaterialDialog.Builder(context!!)
                    .title(R.string.choosethecurrency)
                    .content(R.string.whichcurrencywouldyoulike)
                    .positiveText(Currency.EURO.resID)
                    .negativeText(Currency.DOLLAR.resID)
                    .neutralText(R.string.abort)
                    .onPositive { _, _ ->
                        MaterialDialog.Builder(context!!)
                                .title(R.string.choosealimitineuro)
                                .content("")
                                .inputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                                .negativeText(R.string.abort)
                                .input(Currency.EURO.resID, 0, { _, input ->
                                    if (input.toString().toDoubleOrNull() != null) {
                                        TransactionData.setCredit(Currency2(input.toString().toDouble(), Currency.EURO))
                                    }
                                    newDataObservable.update(null, null)
                                })
                                .show()
                    }
                    .onNegative { _, _ ->
                        MaterialDialog.Builder(context!!)
                                .title(R.string.choosealimitindollar)
                                .content("")
                                .inputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                                .negativeText(R.string.abort)
                                .input(Currency.DOLLAR.resID, 0, { _, input ->
                                    if (input.toString().toDoubleOrNull() != null) {
                                        TransactionData.setCredit(Currency2(input.toString().toDouble(), Currency.DOLLAR))
                                    }
                                    newDataObservable.update(null, null)
                                })
                                .show()
                    }
                    .show()
        }

        investmentbuttonleft.setOnClickListener {
            val appPackageName = context!!.getPackageName()
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
            }
        }

        investmentbuttonright.setOnClickListener {
            val emailIntent = Intent(android.content.Intent.ACTION_SEND)
            emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            emailIntent.type = "vnd.android.cursor.item/email"
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("mod-val@web.de"))
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity!!.resources.getString(R.string.featurerequest))
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.sendmailusing)))
        }

        gototransactionoverview.setOnClickListener {
            (activity as MainActivity).showTransactionOverview()
        }
    }

    fun populateInvestments() {
        setGlobalData()
        setLimitAndLeft()
        setValueAndSpent()
        setInvestmentLines()
    }

    fun setLimitAndLeft() {
        // ----------------------------------
        try {
            val credit = TransactionData.getCredit().get(GlobalSettings.euroOrDollar)
            investmentcreditlimit.text = "%.2f ${resources.getString(GlobalSettings.euroOrDollar.resID)}".format(credit)
        } catch (e: UnsupportedCurrencyException) {
            investmentcreditlimit.text = "-"
        }
        try {
            investmentcreditleft.text = (TransactionData.getCredit() - TransactionData.totalBoughtAndSold()).format(GlobalSettings.euroOrDollar, 2, false, true, context!!)
        } catch (e: UnsupportedCurrencyException) {
            investmentcreditleft.text = "-"
        }
    }

    fun setValueAndSpent() {
        investmentvalue.text = TransactionData.currentPortfolioValueString(priceDataInterface.getData())
        try {
            investmentspent.text = TransactionData.totalBoughtAndSold().format(GlobalSettings.euroOrDollar, 2, false, true, context!!)
            val color = if (TransactionData.totalBoughtAndSold().value <= 0) R.color.greenpercentage else R.color.redpercentage
            investmentspent.setTextColor(ContextCompat.getColor(context!!, color))
        } catch (e: UnsupportedCurrencyException) {
            investmentspent.text = "-"
            investmentspent.setTextColor(ContextCompat.getColor(context!!, R.color.lighterdark))
        }

    }

    fun setInvestmentLines() {
        try {

            var allSumProfit = 0.0
            var allPaid = 0.0

            /**
             * Populate each investment line
             */
            val data: ArrayList<InvestmentLine> = arrayListOf()
            TransactionData.getTransactionListPerCurrency().forEach {

                try {
                    val currentPricePerCoin = priceDataInterface
                            .getData()
                            .find { f ->
                                f.currencyMappings == it.currencyMapping
                            }!!
                            .coinData!!
                            .getPrice(GlobalSettings.euroOrDollar)

                    val netPurchased = it.allTransactions.sumByDouble {
                        it.valueInGlobalCurrency()
                    } * -1

                    val currentValueOwns = currentPricePerCoin * it.owns

                    val sumProfit = netPurchased + currentValueOwns
                    allSumProfit += sumProfit

                    val paid = it.allTransactions
                            .filter { it.transactionAmount > 0 }
                            .sumByDouble {
                                it.valueInGlobalCurrency()
                            }
                    allPaid += paid

                    val netWinPercent = (sumProfit / paid) * 100

                    val profitString = "%.2f ${resources.getString(GlobalSettings.euroOrDollar.resID)}".format(sumProfit)

                    val paidMadePercentage: String
                    val percentageSignColor: Int

                    val hasEverTransactedWithThatCurrency = TransactionData.amountOfTransactions(it.currencyMapping) > 0

                    if (hasEverTransactedWithThatCurrency) {
                        paidMadePercentage = "${DecimalFormat("0.###").format(netWinPercent)} %"
                    } else {
                        if (sumProfit >= 0) {
                            paidMadePercentage = "+"
                        } else {
                            paidMadePercentage = "-"
                        }
                    }

                    if (netWinPercent >= 0) {
                        percentageSignColor = ContextCompat.getColor(context!!, R.color.greenpercentage)
                    } else {
                        percentageSignColor = ContextCompat.getColor(context!!, R.color.redpercentage)
                    }

                    Log.e("TAGO", "net purchased ${netPurchased}  ownsValue ${currentValueOwns}  sumprofit ${sumProfit}  netwinpercent ${netWinPercent}   profitstring ${profitString}  padimadepercentage ${paidMadePercentage} paid ${paid}")

                    val img = ContextCompat.getDrawable(context!!, it.currencyMapping.resID)
                    val name = "${it.currencyMapping.naming} (${it.currencyMapping.shortnaming})"

                    data.add(InvestmentLine(img = img!!, name = name, amountplus = profitString, percentage = paidMadePercentage, percentageColor = percentageSignColor))
                } catch (e: Exception) {
                    val img = ContextCompat.getDrawable(context!!, it.currencyMapping.resID)
                    val name = "${it.currencyMapping.naming} (${it.currencyMapping.shortnaming})"
                    data.add(InvestmentLine(img = img!!, name = name, amountplus = "Error", percentage = "Error", percentageColor = ContextCompat.getColor(context!!, R.color.redpercentage)))
                }
            }


            /**
             * apply changes to investment table
             */
            investmentRV.layoutManager = LinearLayoutManager(activity)
            investmentRV.adapter = MyRecyclerViewAdapterInvestment({ activity!! }, data)
            investmentRV.adapter.notifyDataSetChanged()


            /**
             * Change net win and percentage
             */
            val netWinTotalFiat = allSumProfit
            val netWinTotalPercent = (allSumProfit / allPaid) * 100

            if (!netWinTotalPercent.isNaN()) {
                investmentperformancepercentage.text = "${DecimalFormat("0.##").format(netWinTotalPercent)} %"
            } else {
                investmentperformancepercentage.text = "- %"
            }

            val color = if (netWinTotalPercent.isNaN() || netWinTotalPercent >= 0) R.color.greenpercentage else R.color.redpercentage
            investmentperformancepercentage.setTextColor(ContextCompat.getColor(context!!, color))




            investmentfiat.text = "${DecimalFormat("0.##").format(netWinTotalFiat)} ${resources.getString(GlobalSettings.euroOrDollar.resID)}"
            investmentfiat.setTextColor(ContextCompat.getColor(context!!, if (netWinTotalFiat >= 0) R.color.greenpercentage else R.color.redpercentage))




            val spent = TransactionData.totalBoughtAndSold().get(GlobalSettings.euroOrDollar)
            var percentage: Double = 0.0

            if (spent > 0.0) {
                percentage = (allSumProfit / spent) * 100
                investmentpercentage.text = "${DecimalFormat("0.##").format(percentage)} %"
            } else {
                investmentpercentage.text = "+++"
            }

            val color2 = if (percentage >= 0) R.color.greenpercentage else R.color.redpercentage
            investmentpercentage.setTextColor(ContextCompat.getColor(context!!, color2))


        } catch (e: UnsupportedCurrencyException) {
            investmentnoinvestmentsavailable.text = resources.getString(R.string.unsupportedCurrency)
        } catch (e: Exception) {
            investmentnoinvestmentsavailable.text = resources.getString(R.string.errorpleasecontactdev)
        }


        val quantity = TransactionData.getTransactions().count()
        transactioncount.text = resources.getQuantityString(R.plurals.transaktionspresent, quantity, quantity)
        transactionsRV.layoutManager = LinearLayoutManager(activity)
        transactionsRV.adapter = MyRecyclerViewAdapterTransaction({ activity!! }, TransactionData.getTransactions())
        transactionsRV.adapter.notifyDataSetChanged()

    }

    //global marketcap and such
    fun setGlobalData() {
        // TEST GLOBAL DATA
        CoinMarketCapRest().getGlobalData(ConversionCurrency.EUR,
                object : RestCallback<CMCGlobalData> {
                    override fun handleError(errorTypeAlert: RESTError) {
                        Log.e("LOG ERROR", "GLOBAL DATA $errorTypeAlert   ${errorTypeAlert.reason} error")
                    }

                    override fun success(obj: CMCGlobalData) {
                        Log.e("SUCCESS", "GLOBAL DATA " + obj.toString())
                    }
                })
        //////////////////////////////////////
    }

}

data class InvestmentLine(var img: Drawable, var name: String, var amountplus: String, var percentage: String, var percentageColor: Int)

data class InvestmentData(var currencyMapping: CurrencyMappings, var owns: Double, var allTransactions: ArrayList<Transaction>)

class MyRecyclerViewAdapterInvestment(var context: () -> Context, var data: ArrayList<InvestmentLine>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(context()).inflate(R.layout.investmentline, parent, false)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val d = data.get(position)
        holder.itemView.lineimage.setImageDrawable(d.img)
        holder.itemView.linename.text = d.name
        holder.itemView.lineprofitfiat.text = d.amountplus
        holder.itemView.lineprofitpercentage.text = d.percentage
        holder.itemView.lineprofitpercentage.setTextColor(d.percentageColor)
    }
}


class MyRecyclerViewAdapterTransaction(var context: () -> Context, var data: ArrayList<Transaction>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(context()).inflate(R.layout.transactionline, parent, false)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val d = data.get(position)
        if (d.transactionAmount >= 0) {
            val drawable = ContextCompat.getDrawable(context(), R.drawable.arrowright)
            DrawableCompat.setTint(drawable!!, ContextCompat.getColor(context(), R.color.greenpercentage))
            holder.itemView.transactiontype.setImageDrawable(drawable)
        } else {
            val drawable = ContextCompat.getDrawable(context(), R.drawable.arrowleft)
            DrawableCompat.setTint(drawable!!, ContextCompat.getColor(context(), R.color.redpercentage))
            holder.itemView.transactiontype.setImageDrawable(drawable)
        }
        holder.itemView.transactioncurrency.text = "${d.currencyType.naming} (${d.currencyType.shortnaming})"
        holder.itemView.transactionamount.text = "${d.transactionAmount}"
        holder.itemView.transactionfiat.text =
                when {
                    GlobalSettings.euroOrDollar == Currency.EURO -> d.paidEuro.format(GlobalSettings.euroOrDollar, 2, false, true, context(), d.transactionAmount)
                    GlobalSettings.euroOrDollar == Currency.DOLLAR -> d.paidDollar.format(GlobalSettings.euroOrDollar, 2, false, true, context(), d.transactionAmount)
                    else -> ""
                }
    }

}
