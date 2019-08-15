package modestasvalauskas.com.cryptopurchasesimulator.screens.ProfileActivity

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.itemprofilecell.view.*
import modestasvalauskas.com.cryptopurchasesimulator.R
import modestasvalauskas.com.cryptopurchasesimulator.persistence.TransactionData
import modestasvalauskas.com.cryptopurchasesimulator.persistence.UnsupportedCurrencyException
import modestasvalauskas.com.cryptopurchasesimulator.screens.MainActivity.MainActivity
import modestasvalauskas.com.cryptopurchasesimulator.utils.GlobalSettings


class ProfileData(var number: Int, var name: String, var onClick: () -> Unit, var profileValue: String, var profileSpent: String, var dbName: String)

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.setTitle(resources.getString(R.string.profiletitle))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        populateData()
    }

    fun populateData() {
        val profiles = arrayListOf<ProfileData>()

        val activeProfile = TransactionData.activeProfile()

        TransactionData.profiles().forEachIndexed { index, it ->

            val onClick = {
                // is executed when user clicks on a profile

                // Ask for user to choose profile action
                MaterialDialog.Builder(this)
                        .title(R.string.profileselected)
                        .content(R.string.profilewhatdoyouwanttodo)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .negativeText(getString(R.string.edit))
                        .onNegative { _, _ ->
                            MaterialDialog.Builder(this)
                                    .title(R.string.edit)
                                    .content(R.string.profilewhatdoyouwanttodo)
                                    .inputType(InputType.TYPE_CLASS_TEXT)
                                    .positiveText(getString(R.string.rename))
                                    .onPositive { _, _ ->
                                        MaterialDialog.Builder(this)
                                                .title(R.string.enteranewprofilename)
                                                .content(R.string.createprofilecontent)
                                                .inputType(InputType.TYPE_CLASS_TEXT)
                                                .negativeText(R.string.abort)
                                                .input(it.profileName, it.profileName, { _, input ->
                                                    TransactionData.renameProfile(it, input.toString())
                                                    populateData()
                                                })
                                                .show()
                                    }
                                    .negativeText(getString(R.string.delete))
                                    .onNegative { _, _ ->
                                        if (it.dbName != TransactionData.activeProfile().dbName) {
                                            MaterialDialog.Builder(this)
                                                    .title(R.string.suredeleteprofile)
                                                    .positiveText(getString(R.string.delete))
                                                    .onPositive { _, _ ->
                                                        TransactionData.deleteProfile(it)
                                                        populateData()
                                                    }
                                                    .negativeText(R.string.abort)
                                                    .show()
                                        } else {
                                            Toasty.error(applicationContext, resources.getString(R.string.cantdeleteselectedprofile), Toast.LENGTH_LONG, true).show()
                                        }
                                    }
                                    .neutralText(R.string.abort)
                                    .show()

                        }
                        .positiveText(getString(R.string.open))
                        .onPositive { _, _ ->
                            TransactionData.setActiveProfile(it)
                            finish()
                        }
                        .neutralText(R.string.abort)
                        .show()

                Unit
            }

            TransactionData.setActiveProfile(it)

            var profileSpent: String

            try {
                profileSpent = TransactionData.totalBoughtAndSold().format(GlobalSettings.euroOrDollar, 2, false, true, this)
            } catch (e: UnsupportedCurrencyException) {
                profileSpent = "-"
            }

            var profileValue: String

            if (MainActivity.staticActivity != null) {
                profileValue = TransactionData.currentPortfolioValueString(MainActivity.staticActivity!!.getData())
            } else {
                profileValue = "-"
            }

            profiles.add(ProfileData(
                            number = index + 1,
                            name = it.profileName,
                            onClick = onClick,
                            profileSpent = profileSpent,
                            profileValue = profileValue,
                            dbName = it.dbName))
        }

        profileRV.layoutManager = LinearLayoutManager(this)
        profileRV.adapter = MyRecyclerViewAdapter({ this }, profiles)
        profileRV.addItemDecoration(DividerItemDecoration(this, (profileRV.layoutManager as LinearLayoutManager).orientation))
        profileRV.adapter.notifyDataSetChanged()

        TransactionData.setActiveProfile(activeProfile)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profilemenubar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.profileaddprofile -> {
                MaterialDialog.Builder(this)
                        .title(R.string.createprofiletitle)
                        .content(R.string.createprofilecontent)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .negativeText(R.string.abort)
                        .input(R.string.hintprofilename, 0, { _, input ->
                            TransactionData.addProfile(input.toString())
                            populateData()
                        })
                        .show()

            }
        }
        return true
    }
}

class MyRecyclerViewAdapter(var context: () -> Context, var data: ArrayList<ProfileData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(context()).inflate(R.layout.itemprofilecell, parent, false)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val d = data.get(position)
        if (d.dbName == TransactionData.activeProfile().dbName) {
            holder.itemView.profilenumber.setTextColor(ContextCompat.getColor(context(), R.color.realblack))
        } else {
            holder.itemView.profilenumber.setTextColor(ContextCompat.getColor(context(), R.color.slightlierblackColor))
        }
        holder.itemView.profilenumber.text = "#${d.number}"
        holder.itemView.profilename.text = d.name
        holder.itemView.profileinvestmentvalue.text = d.profileValue
        holder.itemView.profileinvestmentspent.text = d.profileSpent
        holder.itemView.setOnClickListener {  d.onClick() }
    }

}