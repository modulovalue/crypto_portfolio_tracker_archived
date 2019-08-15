package modestasvalauskas.com.cryptopurchasesimulator.utils

import android.content.Context
import modestasvalauskas.com.cryptopurchasesimulator.R
import java.math.BigDecimal
import java.math.RoundingMode


class NumberFormatStringHelper {

    companion object {

        val MILLION: BigDecimal = BigDecimal(1000000)

        val BILLION: BigDecimal = BigDecimal( 1000000000)

        val TRILLION: BigDecimal = BigDecimal( 1000000000000)

        fun truncateNumber(x: BigDecimal, context: Context): String {
            return when {
                x < MILLION     -> x.toString()
                x < BILLION     -> x.divide(MILLION, 3, RoundingMode.HALF_UP).toString().replace(".", ",") + " " + context.resources.getString(R.string.million)
                x < TRILLION    -> x.divide(BILLION, 2, RoundingMode.HALF_UP).toString().replace(".", ",") + " " + context.resources.getString(R.string.billion)
                else            -> x.divide(TRILLION, 2, RoundingMode.HALF_UP).toString().replace(".", ",") + " " + context.resources.getString(R.string.trillion)
            }
        }

    }

}