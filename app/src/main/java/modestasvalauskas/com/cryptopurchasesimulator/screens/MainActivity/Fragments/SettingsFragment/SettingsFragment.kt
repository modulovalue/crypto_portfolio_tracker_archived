package modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Fragments.SettingsFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.fragment_settings.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.screens.DonateActivity.DonateActivity
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.Refreshable
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.TitleInterface


class SettingsFragment : Fragment() {

    val titleInterface: TitleInterface by lazy { (activity as TitleInterface) }

    val refreshable: Refreshable by lazy { (activity as Refreshable) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onStart() {
        super.onStart()
        titleInterface.setTitle(activity!!.resources.getString(R.string.settingstitle))
        titleInterface.setSubtitle("")

        val animalNames = arrayListOf<Triple<String, String, () -> Unit>>()

        animalNames.add(Triple(activity!!.resources.getString(R.string.ssource), "", {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coinmarketcap.com"))
            startActivity(browserIntent)
        }))
        animalNames.add(Triple(activity!!.resources.getString(R.string.scontact), activity!!.resources.getString(R.string.ssclickhere), {
            val emailIntent = Intent(android.content.Intent.ACTION_SEND)
            emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            emailIntent.type = "vnd.android.cursor.item/email"
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("mod-val@web.de"))
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity!!.resources.getString(R.string.scontactbetreff))
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.sendmailusing)))
        }))
        animalNames.add(Triple(activity!!.resources.getString(R.string.sdonate), activity!!.resources.getString(R.string.sdonatesub), {
            startActivity(Intent(context, DonateActivity::class.java))
        }))
        animalNames.add(Triple(activity!!.resources.getString(R.string.soslicences),"", {
            MaterialDialog.Builder(context!!)
                    .title(R.string.oslizenzen)
                    .content(R.string.oslizenzentext)
                    .positiveText(R.string.closedialog)
                    .show()
            Unit
        }))
        animalNames.add(Triple(activity!!.resources.getString(R.string.sresettransactions), activity!!.resources.getString(R.string.sresettransactionssub), {
            MaterialDialog.Builder(context!!)
                    .title(R.string.resettransactionstitle)
                    .content(R.string.resettransactionscontent)
                    .positiveText(R.string.agree)
                    .negativeText(R.string.disagree)
                    .onPositive { _, _ ->
                        TransactionData.resetTransactions()
                    }
                    .show()
            Unit
        }))

        settingsRV.layoutManager = LinearLayoutManager(context)
        settingsRV.adapter = MyRecyclerViewAdapter(context!!, animalNames)
        settingsRV.addItemDecoration(DividerItemDecoration(context, (settingsRV.layoutManager as LinearLayoutManager).orientation))
    }

}