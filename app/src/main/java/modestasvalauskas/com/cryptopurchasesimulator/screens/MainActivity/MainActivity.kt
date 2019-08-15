package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.chibatching.kotpref.Kotpref
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.stephentuso.welcome.WelcomeHelper
import es.dmoral.toasty.Toasty
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappings
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappingsToCoinMarketCapID
import modestasvalauskas.com.cryptopurchasesimulator.library.LoadToast2
import modestasvalauskas.com.cryptopurchasesimulator.network.*
import modestasvalauskas.com.cryptopurchasesimulator.persistence.Currency2
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.persistence.UnsupportedCurrencyException
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.BuyFragment.BuyFragment
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.InvestmentFragment.InvestmentFragment
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.NothingToSellFragment.NothingToSellFragment
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.SellFragment.SellFragment
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.SettingsFragment.SettingsFragment
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.TransactionOverviewFragment.TransactionOverviewFragment
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.statsfragment.MyItem
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.statsfragment.StatsFragment
import modestasvalauskas.com.cryptopurchasesimulator.screens.ProfileActivity.ProfileActivity
import modestasvalauskas.com.cryptopurchasesimulator.screens.Tutorial.WelcomeClass
import modestasvalauskas.com.cryptopurchasesimulator.utils.Currency
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings
import modestasvalauskas.com.cryptopurchasesimulator.utils.WorkingObservable
import java.util.*


data class Transaction(
        // Which cryptocurrency it is
        var currencyType: CurrencyMappings,
        // how much of the transaction was transacted
        var transactionAmount: Double,
        // how much one paid in euro if dollar, converted to euro
        var paidEuro: Currency2,
        // how much one paid in dollar if euro, converted to dollar
        var paidDollar: Currency2,
        // when transaction happened
        var transactionTimestamp: Long = System.currentTimeMillis() / 1000) {

    fun valueInGlobalCurrency(): Double {
        return when (GlobalSettings.euroOrDollar) {
            Currency.EURO -> transactionAmount * paidEuro.value
            Currency.DOLLAR -> transactionAmount * paidDollar.value
            Currency.BTC -> throw UnsupportedCurrencyException()
        }
    }
}


class MainActivity : AppCompatActivity(), Refreshable, TitleInterface, PriceDataInterface {

    companion object {
        var staticActivity: MainActivity? = null
    }

    val dataArray: ArrayList<MyItem> = arrayListOf()
    var observableNewData: WorkingObservable = WorkingObservable()
    lateinit var mixpanel: MixpanelAPI
    lateinit var welcomeScreen: WelcomeHelper

    val fragmentViews = arrayOf(StatsFragment(), BuyFragment(), SellFragment(), SettingsFragment(), NothingToSellFragment(), InvestmentFragment(), TransactionOverviewFragment())

    // <oldFrag, newFrag>
    val stack = Stack<Pair<Int, Int>>()

    var currentFragment: Int = -1

    fun getFragment(nr: Int): Fragment {
        if (currentFragment != -1) {
            stack.push(Pair(currentFragment, nr))
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(fragmentViews[nr] is BackableToOverview)

        currentFragment = nr
        return fragmentViews[nr]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mixpanel = MixpanelAPI.getInstance(this, "4a4fcdcbba75fb2fa491884447182aa1");
        Kotpref.init(context = this)
        Toasty.Config.getInstance().setInfoColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary)).apply()
        Paper.init(this)

        setContentView(R.layout.activity_main)

        welcomeScreen = WelcomeHelper(this, WelcomeClass::class.java)
        welcomeScreen.show(savedInstanceState)

        bottomNavigationView.setOnNavigationItemSelectedListener({ item ->
            onOptionsItemSelected(item)
        })
        bottomNavigationView.findViewById<View>(R.id.action_investments).performClick()

        dataArray.clear()
        CurrencyMappings.values().map { dataArray.add(MyItem(it, null)) }
        populateDataArray()

        if (checkLastRefreshedLessThan5Minutes(GlobalSettings.lastRefreshed)) {
            refresh()
        }

        staticActivity = this
    }

    override fun onStart() {
        super.onStart()
    }

    var isRefreshing = false

    override fun refresh() {
        if (!isRefreshing) {
            isRefreshing = true
            val loadToast = LoadToast2(this)
                    .setText(resources.getString(R.string.dataisloading))
                    .setTextColor(ContextCompat.getColor(applicationContext, R.color.whitecolor))
                    .setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                    .setProgressColor(ContextCompat.getColor(applicationContext, R.color.whitecolor))
                    .show()

            if (((System.currentTimeMillis() / 1000) - GlobalSettings.lastRefreshed) < 30) {
                Handler().postDelayed({
                    loadToast.success()
                    Toasty.info(
                            applicationContext,
                            getString(R.string.dataisfresh),
                            Toast.LENGTH_SHORT, true).show()
                    isRefreshing = false
                }, 1000)
            } else {
                CoinMarketCapRest().getAllData(
                        converioncurrency = ConversionCurrency.EUR,
                        restCallback = object : RestCallback<Array<CoinData>> {
                            override fun handleError(errorTypeAlert: RESTError) {
                                when (errorTypeAlert.errorType) {
                                    ErrorTypeREST.CERTIFICATE_ERROR -> {
                                        Toasty.error(applicationContext, resources.getString(R.string.anerrorhaseccured, "${errorTypeAlert.reason}"), Toast.LENGTH_LONG, true).show()
                                    }
                                    ErrorTypeREST.NO_INTERNET -> {
                                        Toasty.error(applicationContext, resources.getString(R.string.pleasecheckyourinternetconnection), Toast.LENGTH_LONG, true).show()
                                    }
                                    ErrorTypeREST.UNKNOWN -> {
                                        Toasty.error(applicationContext, resources.getString(R.string.anerrorhaseccured, "${errorTypeAlert.reason}"), Toast.LENGTH_LONG, true).show()

                                    }
                                }
                                Log.e("LOG ERROR", "$errorTypeAlert   ${errorTypeAlert.reason} error")
                                loadToast.error()
                                isRefreshing = false
                            }

                            override fun success(obj: Array<CoinData>) {
                                TransactionData.saveCoinData(obj) {
                                    populateDataArray()
                                    GlobalSettings.lastRefreshed = System.currentTimeMillis() / 1000
                                    observableNewData.notifyObservers()
                                }
                                Toasty.info(applicationContext, getString(R.string.dataisnowfresh), Toast.LENGTH_SHORT, true).show()
                                loadToast.success()

                                isRefreshing = false
                            }
                        }
                )
            }
        }
    }

    fun showTransactionOverview() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, getFragment(6))
                .commit()
    }

    override fun onBackPressed() {
        try {
            val lastPair = stack.pop()
            if (fragmentViews[lastPair.second] is BackableToOverview) {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, getFragment(lastPair.first)).commit()
            } else {
                super.onBackPressed()
            }
        } catch (e: EmptyStackException) {
            super.onBackPressed()
        }
    }

    fun checkLastRefreshedLessThan5Minutes(lastRefreshed: Long): Boolean {
        return (((System.currentTimeMillis() / 1000) - lastRefreshed) > 60)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        welcomeScreen.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.statsmenubar, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        observableNewData.notifyObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.statsprofilechange -> startActivity(Intent(this, ProfileActivity::class.java))
            R.id.action_investments -> supportFragmentManager.beginTransaction().replace(R.id.container, getFragment(5)).commit()
            R.id.action_stats -> supportFragmentManager.beginTransaction().replace(R.id.container, getFragment(0)).commit()
            R.id.action_buy -> supportFragmentManager.beginTransaction().replace(R.id.container, getFragment(1)).commit()
            R.id.action_settings -> supportFragmentManager.beginTransaction().replace(R.id.container, getFragment(3)).commit()
            R.id.action_sell -> {
                val showView: Fragment?
                if (TransactionData.netTransactedCurrencies(dataArray.map { it.currencyMappings}) == 0)
                    showView = getFragment(4)
                else {
                    showView = getFragment(2)
                }
                supportFragmentManager.beginTransaction().replace(R.id.container, showView).commit()
            }
            R.id.refresh -> refresh()
            R.id.statseuroordollar -> {
                GlobalSettings.euroOrDollar = Currency.toggle(GlobalSettings.euroOrDollar)
                Toasty.info(
                        applicationContext,
                        getString(R.string.selectedcurrencyrepresentation, resources.getString(GlobalSettings.euroOrDollar.resID)),
                        Toast.LENGTH_SHORT, true).show()
                observableNewData.notifyObservers()
                return true
            }
        }
        return true
    }

    fun populateDataArray() {
        val obj: Array<CoinData> = TransactionData.loadCoinData()
        dataArray.forEach { listItem ->
            val found = obj.find { it.id == CurrencyMappingsToCoinMarketCapID.map[listItem.currencyMappings] }
            if (found != null) {
                listItem.coinData = found
            } else {
                Log.e("COIN", "NOT FOUND IN DATA " + (listItem.coinData?.name ?: "Unknown") + " is null ")
            }
        }
    }

    override fun setTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun setSubtitle(subtitle: String) {
        supportActionBar?.subtitle = subtitle
    }

    override fun getData(): ArrayList<MyItem> {
        return dataArray
    }

}

interface TitleInterface {
    fun setTitle(title: String)
    fun setSubtitle(subtitle: String)
}

interface PriceDataInterface {
    fun getData(): ArrayList<MyItem>
}

interface Refreshable {
    fun refresh()
}

interface BackableToOverview {

}