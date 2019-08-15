package modestasvalauskas.com.cryptopurchasesimulator.network

import com.google.gson.GsonBuilder
import modestasvalauskas.com.cryptopurchasesimulator.utils.Currency
import modestasvalauskas.com.cryptopurchasesimulator.utils.NetworkPrefs
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


interface CoinMarketCapInterface {

    @GET("v1/ticker/")
    fun getAllData(
            @Query("convert") convert: String,
            @Query("limit") limit: Int)
            : Call<Array<CoinData>>

    @GET("v1/ticker/{coin}/")
    fun getDataFor(
            @Path("coin") coinType: String,
            @Query("convert") convert: String)
            : Call<Array<CoinData>>

    @GET("v1/global/")
    fun getGlobalData(
            @Query("convert") convert: String)
            : Call<CMCGlobalData>
}

data class CMCGlobalData(
        var total_market_cap_usd: Double,
        var total_24h_volume_usd: Double,
        var bitcoin_percentage_of_market_cap: Double,
        var active_currencies: Long,
        var active_assets: Long,
        var active_markets: Long,
        var total_market_cap_eur: Double,
        var total_24h_volume_eur: Double
)

enum class ConversionCurrency(var value: String) {
    EUR("EUR"),
    USD("USD")
}

class CoinMarketCapRest {

    var retrofit: Retrofit

    var omniaRestService: CoinMarketCapInterface

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
//                .addNetworkInterceptor(logging)
//                .addInterceptor(logging)
                .build()

        retrofit = Retrofit.Builder()
                .baseUrl(NetworkPrefs.RESTLOCATION)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .client(client)
                .build()

        omniaRestService = retrofit.create(CoinMarketCapInterface::class.java)
    }

    fun getAllData(
            converioncurrency: ConversionCurrency,
            restCallback: RestCallback<Array<CoinData>>) {
        omniaRestService
                .getAllData(
                        convert = converioncurrency.value,
                        limit = 800
                )
                .enqueue(MyCallback(restCallback))
    }

    fun getDataFor(
            coinType: String,
            conversionCurrency: ConversionCurrency,
            restCallback: RestCallback<Array<CoinData>>) {
        omniaRestService
                .getDataFor(
                        coinType = coinType,
                        convert = conversionCurrency.value
                )
                .enqueue(MyCallback(restCallback))
    }

    fun getGlobalData(
            conversionCurrency: ConversionCurrency,
            restCallback: RestCallback<CMCGlobalData>) {
        omniaRestService
                .getGlobalData(
                        convert = conversionCurrency.value
                )
                .enqueue(MyCallback(restCallback))
    }

    internal inner class MyCallback<T>(var callback: RestCallback<T>) : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful)
                callback.success(response.body() as T)
            else
                onFailure(call, Throwable(response.body().toString(), null))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            if (t is UnknownHostException) {
                callback.handleError(RESTError(t.localizedMessage, ErrorTypeREST.NO_INTERNET))
            } else if (t is TimeoutException) {
                callback.handleError(RESTError(t.localizedMessage, ErrorTypeREST.NO_INTERNET))
            } else {
                callback.handleError(RESTError(t.localizedMessage, ErrorTypeREST.UNKNOWN))
            }
            t.printStackTrace()
        }
    }

}


interface RestCallback<T> {
    fun handleError(errorTypeAlert: RESTError)
    fun success(obj: T)
}

class RESTError(var reason: String, var errorType: ErrorTypeREST)

enum class ErrorTypeREST {
    CERTIFICATE_ERROR,
    NO_INTERNET,
    UNKNOWN
}

data class CoinData(
        var id: String,
        var name: String,
        var symbol: String,
        var rank: Int,
        var price_usd: Double,
        var price_btc: Double,
        var `24h_volume_usd`: Double,
        var market_cap_usd: Long,
        var available_supply: Long,
        var total_supply: Long,
        var percent_change_1h: Double,
        var percent_change_24h: Double,
        var percent_change_7d: Double,
        var last_updated: Long,
        var price_eur: Double,
        var `24h_volume_eur`: Double,
        var market_cap_eur: Long
) {
    fun getPrice(currency: Currency): Double {
        return when (currency) {
            Currency.EURO -> price_eur
            Currency.DOLLAR -> price_usd
            Currency.BTC -> price_btc
        }
    }
}