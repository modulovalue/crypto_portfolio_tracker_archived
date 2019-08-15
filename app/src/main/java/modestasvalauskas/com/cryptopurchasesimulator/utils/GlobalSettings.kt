package modestasvalauskas.com.cryptopurchasesimulator.utils

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.statsfragment.MyItem


object GlobalSettings : KotprefModel() {

    var euroOrDollar by enumValuePref(Currency.EURO)

    var transactionEuroOrDollar by enumValuePref(Currency.EURO)

    var sortEnum by enumValuePref(SortBy.nothingdesc)

    var priceBoundTransaction by booleanPref(true)

    var lastRefreshed by longPref()

    fun toggleTransactionEuroOrDollar() {
        if (GlobalSettings.transactionEuroOrDollar == Currency.EURO) {
            GlobalSettings.transactionEuroOrDollar = Currency.DOLLAR
        } else {
            GlobalSettings.transactionEuroOrDollar = Currency.EURO
        }
    }

}

enum class Currency(var resID: Int) {

    EURO(R.string.euro),
    DOLLAR(R.string.dollar),
    BTC(R.string.btc);

    companion object {
        fun toggle(currency: Currency): Currency {
            when (currency) {
                Currency.EURO -> return Currency.DOLLAR
                Currency.DOLLAR -> return Currency.BTC
                Currency.BTC -> return Currency.EURO
            }
        }
    }

}

enum class ShowHelpText {

    YES,
    NO;

    companion object {
        fun toggle(showHelpText: ShowHelpText): ShowHelpText {
            when (showHelpText) {
                ShowHelpText.YES -> return ShowHelpText.NO
                ShowHelpText.NO -> return ShowHelpText.YES
            }
        }
    }

}

enum class SortBy(val stringID: Int) {
    nothingasc(R.string.sortbynothingasc),
    nothingdesc(R.string.sortbynothingdesc),
    longnameasc(R.string.sortbyNameasc),
    longnamedesc(R.string.sortbyNamedesc),
    changeIn1hasc(R.string.sorbyChangein1hasc),
    changeIn1hdesc(R.string.sorbyChangein1hdesc),
    changeIn24hasc(R.string.sortbyChangein24hasc),
    changeIn24hdesc(R.string.sortbyChangein24hdesc),
    changeIn7dasc(R.string.sortByChangein7dasc),
    changeIn7ddesc(R.string.sortByChangein7ddesc),
    marketCapasc(R.string.sortbyMarketCapasc),
    marketCapdesc(R.string.sortbyMarketCapdesc),
    priceasc(R.string.sortbyPriceasc),
    pricedesc(R.string.sortbyPricedesc),
    shortnameasc(R.string.sortbyShortnameasc),
    shortnamedesc(R.string.sortbyShortnamedesc);

    val sort: (ArrayList<Pair<MyItem, Boolean>>) -> ArrayList<Pair<MyItem, Boolean>> = { list ->
        when (GlobalSettings.sortEnum) {
            SortBy.nothingasc -> {
                list
            }
            SortBy.nothingdesc -> {
                list.apply { reverse() }
            }
            SortBy.longnameasc -> {
                ArrayList(list.sortedWith(compareBy({ it.first.coinData?.name ?: "zzz" })))
            }
            SortBy.longnamedesc -> {
                ArrayList(list.sortedWith(compareBy({ it.first.coinData?.name ?: "111" }))).apply { reverse() }
            }
            SortBy.changeIn1hasc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.percent_change_1h
                    } ?: run {
                        Double.MAX_VALUE
                    }
                }))

            }
            SortBy.changeIn1hdesc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.percent_change_1h
                    } ?: run {
                        Double.MAX_VALUE
                    }
                })).apply { reverse() }

            }
            SortBy.changeIn24hasc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.percent_change_24h
                    } ?: run {
                        Double.MAX_VALUE
                    }
                }))
            }
            SortBy.changeIn24hdesc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.percent_change_24h
                    } ?: run {
                        Double.MAX_VALUE
                    }
                })).apply { reverse() }
            }
            SortBy.changeIn7dasc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.percent_change_7d
                    } ?: run {
                        Double.MAX_VALUE
                    }
                }))
            }
            SortBy.changeIn7ddesc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.percent_change_7d
                    } ?: run {
                        Double.MAX_VALUE
                    }
                })).apply { reverse() }
            }
            SortBy.marketCapasc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.market_cap_usd
                    } ?: run {
                        Long.MAX_VALUE
                    }
                }))

            }
            SortBy.marketCapdesc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.market_cap_usd
                    } ?: run {
                        Long.MAX_VALUE
                    }
                })).apply { reverse() }

            }
            SortBy.priceasc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.price_usd
                    } ?: run {
                        Double.MAX_VALUE
                    }
                }))
            }
            SortBy.pricedesc -> {
                ArrayList(list.sortedWith(comparator = compareBy { it: Pair<MyItem, Boolean> ->
                    it.first.coinData?.let {
                        it.price_usd
                    } ?: run {
                        Double.MAX_VALUE
                    }
                })).apply { reverse() }
            }
            SortBy.shortnameasc -> {
                ArrayList(list.sortedWith(compareBy({
                    it.first.coinData?.symbol ?: "-"
                })))
            }
            SortBy.shortnamedesc -> {
                ArrayList(list.sortedWith(compareBy({
                    it.first.coinData?.symbol ?: "-"
                }))).apply { reverse() }
            }
        }
    }
}