package modestasvalauskas.com.cryptopurchasesimulator.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.format.DateUtils
import android.widget.TextView
import modestasvalauskas.com.cryptopurchasesimulator.R
import java.math.BigDecimal


object TextFormatter {

    fun price(euroOrDollar: Currency, price_eur: Double, price_usd: Double, price_btc: Double): String {

        when (euroOrDollar) {

            Currency.EURO -> {
                if (price_eur > 1) {
                    return "${"%.2f".format(price_eur)} €"
                } else {
                    return "${"%.5f".format(price_eur)} €"
                }
            }
            Currency.DOLLAR -> {
                if (price_usd > 1) {
                    return "${"%.2f".format(price_usd)} $"
                } else {
                    return "${"%.5f".format(price_usd)} $"
                }
            }
            Currency.BTC -> {
                if (price_btc > 1) {
                    return "${"%.2f".format(price_btc)} BTC"
                } else {
                    return "${"%.4f".format(price_btc)} BTC"
                }
            }
        }
    }

    fun marketCapText(euroOrDollar: Currency, market_cap_eur: Long, market_cap_usd: Long, market_cap_btc: Long?, context: Context): String {
        when (euroOrDollar) {
            Currency.EURO -> {
                return "${NumberFormatStringHelper.truncateNumber(BigDecimal(market_cap_eur), context)} €"
            }
            Currency.DOLLAR -> {
                return "${NumberFormatStringHelper.truncateNumber(BigDecimal(market_cap_usd), context)} $"
            }
            Currency.BTC -> {
                if (market_cap_btc != null) {
                    return "${NumberFormatStringHelper.truncateNumber(BigDecimal(market_cap_btc), context)} $"
                } else {
                    return "NA"
                }
            }
        }
    }

    fun timestampToLastRefreshed(timestamp: Long, context: Context): String {

        var data = DateUtils.formatDateTime(context, timestamp * 1000, DateUtils.FORMAT_SHOW_TIME)
        if (data != null) {
            return data
        } else {
            return "/"
        }
    }

    fun percentage24H(tv: TextView, percent_change_24h: Double, context: Context) {
        // change in 24h
        if (percent_change_24h >= 0) {
            tv.setTextColor(ContextCompat.getColor(context, R.color.greenpercentage))
        } else {
            tv.setTextColor(ContextCompat.getColor(context, R.color.redpercentage))
        }
        tv.setText("${"%.2f".format(percent_change_24h)} %")
    }

}