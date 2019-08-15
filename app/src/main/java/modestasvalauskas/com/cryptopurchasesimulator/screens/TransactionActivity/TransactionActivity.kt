package modestasvalauskas.com.cryptopurchasesimulator.screens.TransactionActivity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_transaction.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappings
import modestasvalauskas.com.cryptopurchasesimulator.library.LoadToast2
import modestasvalauskas.com.cryptopurchasesimulator.persistence.Currency2
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.utils.Currency
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*


enum class TransactionType {
    BUY,
    SELL
}

class TransactionActivity : AppCompatActivity() {

    companion object {
        val TRANSACTIONTYPEEXTRA = "tranactiontype"
        val CURRENCYEXTRA = "currency"
        val ONEVALUEEURO = "ineuro"
        val ONEVALUEDOLLAR = "indollar"
    }

    lateinit var transactionType: TransactionType

    lateinit var currency: CurrencyMappings

    var valueInDollar: Double = 0.0
    var valueInEuro: Double = 0.0
    var changeTextManuallytransactionamount = false
    var changeTextManuallytransactionamountfiat = false
    var ownsamount: Double = 0.0
    var lastTransactionTimestamp: Long? = null
    var customTransactionTimestamp: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initPassedData()
        notifyUICurrencyChanged()
        closeKeyboardOnFocusChangeAndAddChangeListeners()
        setTransactClickListerner()
        initDateButton()
    }

    fun initDateButton() {

        fun updateDateButtonText() {
            if (customTransactionTimestamp == null) {
                transactionDifferentDate.text = resources.getString(R.string.transactiondatecurrent)
            } else {
                val cal = Calendar.getInstance()
                val tz = cal.timeZone
                val sdf = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)
                sdf.timeZone = tz
                val localTime = sdf.format(Date(customTransactionTimestamp!! * 1000))
                transactionDifferentDate.text = resources.getString(R.string.transactiondatecustom).format(localTime)
            }
        }

        var dialogContentText = resources.getString(R.string.informationaboutcustomdateresettonoworset)

        val transactions = TransactionData
                .getTransactions()
                .filter { it.currencyType == currency }

        val thereAreOlderTransactions = !transactions.isEmpty()

        if (!thereAreOlderTransactions) {
            dialogContentText = dialogContentText.format(resources.getString(R.string.transactiontherearenooldtransactions))
        } else {
            lastTransactionTimestamp = transactions.last().transactionTimestamp
            val cal = Calendar.getInstance()
            val tz = cal.timeZone
            val sdf = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)
            sdf.timeZone = tz
            val lastTransactionTime = sdf.format(Date(lastTransactionTimestamp!! * 1000))
            dialogContentText = dialogContentText.format(lastTransactionTime)

        }

        transactionDifferentDate.setOnClickListener {
            MaterialDialog.Builder(this)
                    .title(R.string.informationaboutcustomdate)
                    .content(dialogContentText)
                    .negativeText(getString(R.string.setdatetonow))
                    .onNegative { _, _ ->
                        customTransactionTimestamp = null
                        updateDateButtonText()
                    }
                    .positiveText(getString(R.string.setcustomdate))
                    .onPositive { _, _ ->

                        var ayear: Int?
                        var amonthOfYear: Int?
                        var adayOfMonth: Int?
                        var ahourOfDay: Int?
                        var aminuteOfDay: Int?
                        var asecond: Int?

                        val now = Calendar.getInstance()
                        val dpd = DatePickerDialog.newInstance(
                                { _, year, monthOfYear, dayOfMonth ->
                                    ayear = year
                                    amonthOfYear = monthOfYear
                                    adayOfMonth = dayOfMonth

                                    val now = Calendar.getInstance()
                                    val dpd = TimePickerDialog.newInstance(
                                            { _, hourOfDay, minute, second ->

                                                ahourOfDay = hourOfDay
                                                aminuteOfDay = minute
                                                asecond = second

                                                val calendar = GregorianCalendar()
                                                calendar.set(Calendar.DAY_OF_MONTH, adayOfMonth!!)
                                                calendar.set(Calendar.MONTH, amonthOfYear!!)
                                                calendar.set(Calendar.YEAR, ayear!!)
                                                calendar.set(Calendar.HOUR_OF_DAY, ahourOfDay!!)
                                                calendar.set(Calendar.MINUTE, aminuteOfDay!!)
                                                calendar.set(Calendar.SECOND, asecond!!)
                                                customTransactionTimestamp = calendar.timeInMillis / 1000
                                                updateDateButtonText()
                                            },
                                            now.get(Calendar.HOUR_OF_DAY),
                                            now.get(Calendar.MINUTE),
                                            true)


                                    if (thereAreOlderTransactions) {
                                        val d = Date(lastTransactionTimestamp!! * 1000)
                                        val c = Calendar.getInstance()
                                        c.time = d
                                        if (ayear == c.get(Calendar.YEAR) && amonthOfYear == c.get(Calendar.MONTH) && adayOfMonth == c.get(Calendar.DAY_OF_MONTH)) {
                                            dpd.setMinTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND))
                                        }
                                    }

                                    if (ayear == now.get(Calendar.YEAR) && amonthOfYear == now.get(Calendar.MONTH) && adayOfMonth == now.get(Calendar.DAY_OF_MONTH)) {
                                        dpd.setMaxTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND))
                                    }

                                    dpd.show(fragmentManager, resources.getString(R.string.setcustomdate))
                                },
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        )

                        if (thereAreOlderTransactions) {
                            val d = Date(lastTransactionTimestamp!! * 1000)
                            val c = Calendar.getInstance()
                            c.time = d
                            dpd.minDate = c
                        }

                        dpd.maxDate = Calendar.getInstance()
                        dpd.show(fragmentManager, resources.getString(R.string.setcustomdate))
                    }
                    .neutralText(R.string.abort)
                    .show()

        }

        updateDateButtonText()
    }

    fun setTransactClickListerner() {

        val transactError: () -> Unit

        if (transactionType == TransactionType.BUY) {
            supportActionBar?.title = resources.getString(R.string.buy)
            transactionaction.setText(R.string.buyfinnaly)
            transactError = {
                Toasty.error(this, resources.getString(R.string.cantbuyzero, currency.shortnaming), Toast.LENGTH_SHORT, true).show()
            }
        } else {
            supportActionBar?.title = resources.getString(R.string.sell)
            transactionaction.setText(R.string.sellfinally)
            transactError = {
                Toasty.error(this, resources.getString(R.string.cantsellzero, currency.shortnaming), Toast.LENGTH_SHORT, true).show()
            }
        }

        transactionaction.setOnClickListener {

            val transactionNumber = transactionamount.text.toString().toDoubleOrNull() ?: transactionamount.text.toString().replace(",", ".").toDoubleOrNull()
            val transactionNumberFiat = transactionamountfiat.text.toString().toDoubleOrNull() ?: transactionamountfiat.text.toString().replace(",", ".").toDoubleOrNull()

            //both fields cant be empty
            if (transactionNumber != null && transactionNumberFiat != null) {
                val cantSellZeroCheck = transactionNumber > 0 && transactionNumberFiat > 0
                if (cantSellZeroCheck) {
                    transactionaction.isClickable = false
                    doAction {
                        val curFiat = GlobalSettings.transactionEuroOrDollar
                        if (GlobalSettings.priceBoundTransaction) {
                            TransactionData.transactCurrency(
                                    currencyType = currency,
                                    amount = transactionNumber * if(transactionType == TransactionType.SELL) -1 else 1 ,
                                    totalPriceDollar = Currency2(valueInDollar, Currency.DOLLAR),
                                    totalPriceEuro = Currency2(valueInEuro, Currency.EURO),
                                    timestamp = customTransactionTimestamp
                            )
                        } else {
                            if (curFiat == Currency.EURO) {
                                TransactionData.transactCurrency(
                                        currencyType = currency,
                                        amount = transactionNumber * if(transactionType == TransactionType.SELL) -1 else 1,
                                        totalPriceDollar = Currency2(euroToDollarValue(transactionNumberFiat, transactionNumber), Currency.DOLLAR),
                                        totalPriceEuro = Currency2(valueToSingleValue(transactionNumberFiat, transactionNumber), Currency.EURO),
                                        timestamp = customTransactionTimestamp
                                )
                            } else if (curFiat == Currency.DOLLAR){
                                TransactionData.transactCurrency(
                                        currencyType = currency,
                                        amount = transactionNumber * if(transactionType == TransactionType.SELL) -1 else 1,
                                        totalPriceDollar = Currency2(valueToSingleValue(transactionNumberFiat, transactionNumber), Currency.DOLLAR),
                                        totalPriceEuro = Currency2(dollarToEuroValue(transactionNumberFiat, transactionNumber), Currency.EURO),
                                        timestamp = customTransactionTimestamp
                                )
                            }
                        }
                        finish()
                    }
                } else {
                    transactError()
                }
            } else {
                Toasty.error(this, resources.getString(R.string.fieldsempty), Toast.LENGTH_SHORT, true).show()
            }
        }
    }

    fun updatePriceBound() {
        if (GlobalSettings.priceBoundTransaction) {
            val transactionAmount = transactionamount.text.toString().toDoubleOrNull() ?: transactionamount.text.toString().replace(",", ".").toDoubleOrNull()
            changeTextManuallytransactionamountfiat = true
            if (GlobalSettings.transactionEuroOrDollar == Currency.EURO) {
                supportActionBar?.subtitle = "1 ${currency.shortnaming} = ${"%.2f".format(valueInEuro)} ${resources.getString(GlobalSettings.transactionEuroOrDollar.resID)}"
                transactionamountfiat.setText(if (transactionAmount != null) "%.2f".format(transactionAmount * valueInEuro).replace(",", ".") else "")
            } else if (GlobalSettings.transactionEuroOrDollar == Currency.DOLLAR) {
                supportActionBar?.subtitle = "1 ${currency.shortnaming} = ${"%.2f".format(valueInDollar)} ${resources.getString(GlobalSettings.transactionEuroOrDollar.resID)}"
                transactionamountfiat.setText(if (transactionAmount != null) "%.2f".format(transactionAmount * valueInDollar).replace(",", ".") else "")
            }
            changeTextManuallytransactionamountfiat = false
        } else {
            supportActionBar?.subtitle = "${resources.getString(R.string.customtransactionsubtitle)}"
        }
    }

    fun initPassedData() {

        fun initPriceBoundCheckBox() {
            priceBoundCheckbox.isChecked = GlobalSettings.priceBoundTransaction
            priceBoundCheckbox.setOnCheckedChangeListener({ _: View?, isChecked: Boolean ->
                GlobalSettings.priceBoundTransaction = isChecked
                updatePriceBound()
            })
            updatePriceBound()
        }

        transactionType = intent.getSerializableExtra(TRANSACTIONTYPEEXTRA) as TransactionType
        currency = intent.getSerializableExtra(CURRENCYEXTRA) as CurrencyMappings
        valueInDollar = intent.getDoubleExtra(ONEVALUEDOLLAR, 0.0)
        valueInEuro = intent.getDoubleExtra(ONEVALUEEURO, 0.0)
        ownsamount = TransactionData.amountOf(currency)

        initPriceBoundCheckBox()
    }

    fun closeKeyboardOnFocusChangeAndAddChangeListeners() {
        val a = { v: View, hasFocus: Boolean ->
            if (!hasFocus) {
                val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
        transactionamount.setOnFocusChangeListener(a)
        transactionamountfiat.setOnFocusChangeListener(a)

        transactionamount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {

                val d = p0.toString().toDoubleOrNull()

                if (GlobalSettings.priceBoundTransaction) {
                    changeTextManuallytransactionamountfiat = true
                    if (!changeTextManuallytransactionamount) {
                        if (d != null) {
                            transactionamountfiat.setText("%.2f".format(d * if (GlobalSettings.transactionEuroOrDollar == Currency.EURO) valueInEuro else valueInDollar).replace(",", "."))
                        } else {
                            transactionamountfiat.setText("")
                        }
                    }
                    changeTextManuallytransactionamountfiat = false
                }

                if (transactionType == TransactionType.SELL) {
                    if (d != null && d > TransactionData.amountOf(currency)) {
                        transactionaction.text = resources.getString(R.string.notenoughcurrency, currency.naming)
                        transactionaction.isEnabled = false
                    } else {
                        transactionaction.text = resources.getString(R.string.sellfinally)
                        transactionaction.isEnabled = true
                    }
                }
            }
        })
        transactionamountfiat.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {

                val d = p0.toString().toDoubleOrNull()

                if (GlobalSettings.priceBoundTransaction) {
                    changeTextManuallytransactionamount = true
                    if (!changeTextManuallytransactionamountfiat) {
                        if (d != null) {
                            transactionamount.setText("%.6f".format(d / if (GlobalSettings.transactionEuroOrDollar == Currency.EURO) valueInEuro else valueInDollar).replace(",", "."))
                        } else {
                            transactionamount.setText("")
                        }
                    }
                    changeTextManuallytransactionamount = false
                }

                val amount = transactionamount.text.toString().toDoubleOrNull()
                Log.e("TOGO", "amount ${amount}")
                if (transactionType == TransactionType.SELL) {
                    if (amount != null && amount > TransactionData.amountOf(currency)) {
                        transactionaction.text = resources.getString(R.string.notenoughcurrency, currency.naming)
                        transactionaction.isEnabled = false
                        Log.e("TOGO", "55")
                    } else {
                        transactionaction.text = resources.getString(R.string.sellfinally)
                        transactionaction.isEnabled = true
                        Log.e("TOGO", "66")
                    }
                }
            }
        })

    }

    fun doAction(action: () -> Unit) {
        val toast = LoadToast2(this)
                .setText("")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.whitecolor))
                .setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .setProgressColor(ContextCompat.getColor(applicationContext, R.color.whitecolor))
                .show()
        Handler().postDelayed({
            toast.success()
        }, 500)
        Handler().postDelayed({
            action()
        }, 1000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.transactionmenubar, menu)
        if (transactionType == TransactionType.BUY) {
            menu.findItem(R.id.transactionselecteverything).isVisible = false
        }
        return true
    }

    fun dollarToEuroValue(dollarValue: Double, amount: Double): Double {
        return valueToSingleValue((valueInEuro / valueInDollar) * dollarValue, amount)
    }

    fun euroToDollarValue(euroValue: Double, amount: Double): Double {
        return valueToSingleValue((valueInDollar / valueInEuro) * euroValue, amount)
    }

    fun valueToSingleValue(value: Double, amount: Double): Double {
        return value / amount
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.transactioneuroordollar -> {
                GlobalSettings.toggleTransactionEuroOrDollar()
                notifyUICurrencyChanged()
            }
            R.id.transactionselecteverything -> {
                transactionamount.setText(TransactionData.amountOf(currency).toString())
            }
        }
        return true
    }

    fun notifyUICurrencyChanged() {
        transactionimage.setImageDrawable(ContextCompat.getDrawable(applicationContext, this.currency.resID))
        transactionimagename.text = "${this.currency.naming} (${this.currency.shortnaming})"
        transactionowns.text = resources.getString(R.string.ownsamountof, DecimalFormat("0.######").format(ownsamount))
        transactionamountfiat.setHint(GlobalSettings.transactionEuroOrDollar.resID)
        transactionamountfiathint.setHint(resources.getString(GlobalSettings.transactionEuroOrDollar.resID))
        transactionamount.setHint("${currency.shortnaming}")
        transactionamounthint.setHint("${currency.shortnaming}")
        customtransactioncurrency.text = resources.getString(GlobalSettings.transactionEuroOrDollar.resID)
        updatePriceBound()
    }

}
