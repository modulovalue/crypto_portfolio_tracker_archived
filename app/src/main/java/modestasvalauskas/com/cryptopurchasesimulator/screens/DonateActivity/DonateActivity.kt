package modestasvalauskas.com.cryptopurchasesimulator.screens.DonateActivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_donate.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.data.CurrencyMappings

class DonateActivity : AppCompatActivity() {

    companion object {
        val donateCoinTypes = arrayListOf(
                Triple(CurrencyMappings.BITCOIN,"1LZknetftezNYeRajrUum2ghntqW5Dxnzj",R.drawable.privatebitcoin),
                Triple(CurrencyMappings.ETHEREUM,"0x56965cc5be7fdc4491e97c42f47bdc71b1a13b9f",R.drawable.privateethereum),
                Triple(CurrencyMappings.ETHEREUMCLASSIC,"0x35d610adc7ba2b2d1fae3b0c2f42c29f09bc6464",R.drawable.privateethereumclassic),
                Triple(CurrencyMappings.LITECOIN,"LgvaT5ZBgKib7v6QhfNEzRwbgakxbSTJTN",R.drawable.privatelitecoin),
                Triple(CurrencyMappings.DOGECOIN,"D9ArurG3yzEiEtBzXDmCCu6FvuCjfswcGx",R.drawable.privatedogecoin),
                Triple(CurrencyMappings.DASH,"Xcg5Q9sEUcL8jLk8BHQUR9C9Fea8L3Emik",R.drawable.privatedash),
                Triple(CurrencyMappings.AUGUR,"0x56965cc5be7fdc4491e97c42f47bdc71b1a13b9f",R.drawable.privateaugur),
                Triple(CurrencyMappings.ZCASH,"t1J2mt1hFDmhT26ZhX2DuxzpASswAknCbrW",R.drawable.privatezcash)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)
        supportActionBar?.setTitle(resources.getString(R.string.donate))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        donaterv.layoutManager = LinearLayoutManager(applicationContext)
        donaterv.adapter = MyAdapter({ this }, donateCoinTypes, applicationContext, R.layout.donalecell)
        donaterv.addItemDecoration(DividerItemDecoration(applicationContext, (donaterv.layoutManager as LinearLayoutManager).orientation))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}

