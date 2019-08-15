package modestasvalauskas.com.cryptopurchasesimulator.persistence

import android.content.Context
import android.util.Log
import io.paperdb.Book
import io.paperdb.Paper
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappings
import modestasvalauskas.com.cryptopurchasesimulator.network.CoinData
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.InvestmentFragment.InvestmentData
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.statsfragment.MyItem
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Transaction
import modestasvalauskas.com.cryptopurchasesimulator.utils.Currency
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class TransactionData {

    companion object {

        var COINDATA = "coindata"

        var TRANSACTIONDATA = "transaction"

        var MARKEDCURRENCYDATA = "markedcurrencies"

        var ACTIVEPROFILE = "activeprofile"

        var PROFILEIST = "profilelist"

        var CREDIT = "creditamount"

        fun saveCoinData(obj: Array<CoinData>, doThis: () -> Unit) {
            Paper.book().write(COINDATA, obj)
            doThis()
        }

        fun loadCoinData(): Array<CoinData> {
            return Paper.book().read<Array<CoinData>>("coindata") ?: arrayOf()
        }

        fun transactCurrency(currencyType: CurrencyMappings, amount: Double, totalPriceEuro: Currency2, totalPriceDollar: Currency2, timestamp: Long?) {

            val transactionTime: Long?

            if (timestamp != null) {
                transactionTime = timestamp
            } else {
                transactionTime = System.currentTimeMillis() / 1000
            }

            val transaction = Transaction(
                    currencyType = currencyType,
                    transactionAmount = amount,
                    paidDollar = totalPriceDollar,
                    paidEuro = totalPriceEuro,
                    transactionTimestamp = transactionTime)
            addTransaction(transaction)
        }

        fun amountOf(currencyMapping: CurrencyMappings): Double {
            return getTransactions()
                    .filter { it.currencyType == currencyMapping }
                    .sumByDouble { it.transactionAmount }
        }

        fun amountOfTransactions(currencyMapping: CurrencyMappings): Int {
            return getTransactions()
                    .filter { it.currencyType == currencyMapping }
                    .count()
        }

        fun hasTransacted(currencyMapping: CurrencyMappings): Boolean {
            return getTransactions().find { it.currencyType == currencyMapping } != null
        }

        fun removeTransaction(transaction: Transaction) {
            val transactions = getTransactions()
            transactions.remove(transaction)
            writeTransactions(transactions)
        }

        fun addTransaction(transaction: Transaction) {
            val transactions = getTransactions()
            transactions.add(transaction)
            writeTransactions(transactions)
        }

        fun writeTransactions(arrayListOfTransactions: ArrayList<Transaction>) {
            currentProfileBook().write(TRANSACTIONDATA, arrayListOfTransactions)
        }

        fun getTransactions(): ArrayList<Transaction> {
            return currentProfileBook().read<ArrayList<Transaction>>(TRANSACTIONDATA, arrayListOf())
        }

        fun removeTransactions(currencyMappings: CurrencyMappings) {
            writeTransactions(ArrayList(getTransactions().filter { it.currencyType != currencyMappings }))
        }

        fun resetTransactions() {
            currentProfileBook().delete(TRANSACTIONDATA)
        }

        fun markCurrency(currency: CurrencyMappings) {
            val marked = currentProfileBook().read<ArrayList<CurrencyMappings>>(MARKEDCURRENCYDATA, arrayListOf())
            if (isCurrencyMarked(currency)) {
                marked.remove(currency)
            } else {
                marked.add(currency)
            }
            currentProfileBook().write(MARKEDCURRENCYDATA, marked)
        }

        fun isCurrencyMarked(currency: CurrencyMappings): Boolean {
            return currentProfileBook().read(MARKEDCURRENCYDATA, ArrayList<CurrencyMappings>()).contains(currency)
        }

        fun netInvestmentCount(currencies: List<CurrencyMappings>): Int {
            var count = 0
            currencies.forEach {
                if (amountOf(it) > 0) count += 1
            }
            return count
        }

        fun netTransactedCurrencies(currencies: List<CurrencyMappings>): Int {
            var count = 0
            currencies.forEach {
                if (amountOfTransactions(it) > 0) count += 1
            }
            return count
        }

        fun getTransactionListPerCurrency(): List<InvestmentData> {
            val transactions: MutableMap<CurrencyMappings, ArrayList<Transaction>> = mutableMapOf()
            getTransactions().forEach {
                if (!transactions.containsKey(it.currencyType)) {
                    transactions.put(it.currencyType, arrayListOf())
                }
                transactions[it.currencyType]!!.add(it)
            }

            return transactions.map {
                InvestmentData(
                        currencyMapping = it.key,
                        owns = it.value.sumByDouble { it.transactionAmount },
                        allTransactions = it.value)
            }
        }

        fun currentPortfolioValue(currentPrices: ArrayList<MyItem>): Double {
            return TransactionData.getTransactions()
                    .map { m ->
                        val found = currentPrices.find { d ->
                            m.currencyType == d.currencyMappings
                        }
                        if (found != null) {
                            if (found.coinData != null) {
                                when (GlobalSettings.euroOrDollar) {
                                    Currency.EURO -> {
                                        (found.coinData as CoinData).price_eur * m.transactionAmount
                                    }
                                    Currency.DOLLAR -> {
                                        (found.coinData as CoinData).price_usd * m.transactionAmount
                                    }
                                    Currency.BTC -> {
                                        (found.coinData as CoinData).price_btc * m.transactionAmount
                                    }
                                }
                            } else {
                                0.0
                            }
                        } else {
                            0.0
                        }
                    }
                    .sum()
        }

        fun currentPortfolioValuePlusTransactionWin(currentPrices: ArrayList<MyItem>): Double {
            return TransactionData.getTransactions()
                    .map { m ->
                        val found = currentPrices.find { d ->
                            m.currencyType == d.currencyMappings
                        }
                        if (found != null) {
                            if (found.coinData != null) {
                                when (GlobalSettings.euroOrDollar) {
                                    Currency.EURO -> {
                                        (found.coinData as CoinData).price_eur * m.transactionAmount
                                    }
                                    Currency.DOLLAR -> {
                                        (found.coinData as CoinData).price_usd * m.transactionAmount
                                    }
                                    Currency.BTC -> {
                                        (found.coinData as CoinData).price_btc * m.transactionAmount
                                    }
                                }
                            } else {
                                0.0
                            }
                        } else {
                            0.0
                        }
                    }
                    .sum()
        }

        fun currentPortfolioValueString(currentPrices: ArrayList<MyItem>): String {

            val currentmoney = currentPortfolioValue(currentPrices)

            when (GlobalSettings.euroOrDollar) {
                Currency.EURO -> {
                    return "%.2f €".format(currentmoney)
                }
                Currency.DOLLAR -> {
                    return "%.2f $".format(currentmoney)
                }
                Currency.BTC -> {
                    return "%.6f BTC".format(currentmoney)
                }
            }
        }

        fun setCredit(c: Currency2) {
            currentProfileBook().write(CREDIT, c)
        }

        fun getCredit(): Currency2 {
            return currentProfileBook().read(CREDIT, Currency2(2000.0, Currency.EURO))
        }

        /**
         * if a profit was made, this number is negative
         */
        fun totalBoughtAndSold(): Currency2 {
            val ret = Currency2(0.0, GlobalSettings.euroOrDollar)
            TransactionData.getTransactions().forEach {
                ret.value += it.valueInGlobalCurrency()
            }
            return ret
        }

        // -------------- Profile management --------------

        // different currentProfileBook for every Profile with
        fun currentProfileBook(): Book {
            return Paper.book(activeProfile().dbName)
        }

        fun activeProfile(): Profile {
            return Paper.book().read<Profile>(ACTIVEPROFILE, Profiles.standardProfile)
        }

        fun setActiveProfile(profile: Profile) {
            Paper.book().write(ACTIVEPROFILE, profile)
        }

        fun addProfile(profileName: String) {
            val profs = profiles()
            profs.add(Profile(UUID.randomUUID().toString(), profileName))
            Paper.book().write<ArrayList<Profile>>(PROFILEIST, profs)
        }

        fun profiles(): ArrayList<Profile> {
            return Paper.book().read<ArrayList<Profile>>(PROFILEIST, arrayListOf(Profiles.standardProfile))
        }

        fun renameProfile(profileToRename: Profile, toString: String) {
            val profs = profiles()
            profs.find { it.dbName.equals(profileToRename.dbName) }!!.profileName = toString
            Paper.book().write<ArrayList<Profile>>(PROFILEIST, profs)
        }

        fun deleteProfile(profileToDelete: Profile) {
            val profs = profiles()
            profs.remove(profs.find{ it.dbName.equals(profileToDelete.dbName) })
            Paper.book().write<ArrayList<Profile>>(PROFILEIST, profs)
        }

        // ------------------------------------------------
    }
}

operator fun Currency2.minus(c: Currency2): Currency2 {
    return when {
        this.currency == Currency.BTC || c.currency == Currency.BTC -> {
            throw UnsupportedCurrencyException()
        }
        this.currency == c.currency -> {
            Currency2(this.value - c.value, this.currency)
        }
        this.currency == Currency.EURO && c.currency == Currency.DOLLAR -> {
            Currency2(this.value - (c.value * getOneDollarInEuro()), Currency.EURO)
        }
        this.currency == Currency.DOLLAR && c.currency == Currency.EURO -> {
            Currency2((this.value * getOneDollarInEuro()) - c.value, Currency.EURO)
        }
        else -> {
            throw RuntimeException("ERROR Forgot case")
        }
    }
}

operator fun Currency2.plus(c: Currency2): Currency2 {
    return when {
        this.currency == Currency.BTC || c.currency == Currency.BTC -> {
            throw UnsupportedCurrencyException()
        }
        this.currency == c.currency -> {
             Currency2(this.value + c.value, this.currency)
        }
        this.currency == Currency.EURO && c.currency == Currency.DOLLAR -> {
             Currency2((this.value * getOneEuroInDollar()) + c.value, Currency.EURO)
        }
        this.currency == Currency.DOLLAR && c.currency == Currency.EURO -> {
             Currency2((this.value * getOneDollarInEuro()) + c.value, Currency.EURO)
        }
        else -> {
            throw RuntimeException("ERROR Forgot case")
        }
    }
}

data class Currency2(var value: Double, var currency: Currency) {

     fun get(c: Currency): Double {
         return when {
            currency == Currency.BTC || c == Currency.BTC -> {
                throw UnsupportedCurrencyException()
            }
            currency == c -> {
                value
            }
            currency == Currency.EURO && c == Currency.DOLLAR -> {
                value * getOneEuroInDollar()
            }
            currency == Currency.DOLLAR && c == Currency.EURO -> {
                value * getOneDollarInEuro()
            }
            else -> {
                throw RuntimeException("ERROR Forgot case")
            }
        }
    }

    fun format(c: Currency, decimalPlaces: Int, dynamicPlaces: Boolean, symbol: Boolean, context: Context, multip: Double = 1.0): String {
        return if (dynamicPlaces) {
            DecimalFormat("0.${"#".repeat(decimalPlaces)}").format(get(c) * multip) + if (symbol) " ${context.resources.getString(c.resID)}" else ""
        } else {
            "%.${decimalPlaces}f".format(get(c) * multip) + if (symbol) " ${context.resources.getString(c.resID)}" else ""
        }
    }

    fun getOneDollarInEuro(): Double {
        return 0.85
    }

    fun getOneEuroInDollar(): Double {
        return 1/getOneDollarInEuro()
    }

}


class UnsupportedCurrencyException : Exception()

/**
 *  Profiles are for accessing different books. store data in those books
 */
class Profile(var dbName: String, var profileName: String)

object Profiles {
    var standardProfile: Profile = Profile("defaultprofile", "1")
}

fun Any.log(s: String = "") {
    Log.e("LOGTHIS", "Logging: ($s) " + this.toString())
}