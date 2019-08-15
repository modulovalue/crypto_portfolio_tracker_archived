package modestasvalauskas.com.cryptopurchasesimulator.screens.DonateActivity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_display_address.*
import kotlinx.android.synthetic.main.content_display_address.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappings


class DisplayAddressActivity : AppCompatActivity() {

    companion object {
        val COINTYPE = "coinType"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_display_address)
        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(resources.getString(R.string.donate))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var extra: Triple<CurrencyMappings, String, Int> = intent.getSerializableExtra(COINTYPE) as Triple<CurrencyMappings, String, Int>

        donationimagetext.text = "${extra.first.naming} (${extra.first.shortnaming})"
        donationaddress.text = extra.second
        donationimage.setImageDrawable(ContextCompat.getDrawable(applicationContext, extra.first.resID))
        donationaddressqr.setImageDrawable(ContextCompat.getDrawable(applicationContext, extra.third))
        donatecopytoclipboard.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(resources.getString(R.string.copiedprivateaddress, extra.first.shortnaming), extra.second)
            Toasty.success(
                    applicationContext,
                    resources.getString(R.string.copiedprivateaddress, extra.first.shortnaming),
                    Toast.LENGTH_SHORT, true).show()
            clipboard.setPrimaryClip(clip)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}